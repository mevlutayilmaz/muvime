package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muvime.model.Movie;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.service.MovieApiService;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";
    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;

    private RecyclerView recyclerView_search;
    private MovieAdapter movieAdapter;
    private TextView search_result, toolbar_tittle;
    private EditText text_search;
    private ImageView search_bg_image;
    private ImageButton btn_search;
    private List<Movie> movies;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        networkChangeReceiver = new NetworkChangeReceiver();
        registerNetworkChangeReceiver();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        search_result = findViewById(R.id.search_result);
        toolbar_tittle = findViewById(R.id.search_toolbar_tittle);
        text_search = findViewById(R.id.search_text_search);
        search_bg_image = findViewById(R.id.search_bg_image);
        btn_search = findViewById(R.id.search_btn_search);
        recyclerView_search = findViewById(R.id.recycler_search);
        recyclerView_search.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Intent intent = getIntent();
        ArrayList<Integer> idList = intent.getIntegerArrayListExtra("idList");
        search_result.setText(idList.size() + " records");

        loadData(idList);
        loadDataPopular();


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieList.clear();
                loadDataSearch();
                closeKeyboard();
            }
        });

        toolbar_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void loadData(ArrayList<Integer> idList){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        for(int id : idList){
            compositeDisposable.add(apiService.getMovieDetails(id, API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseDetails));
        }
    }

    private void loadDataPopular() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getPopularMovies(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponsePopular));
    }

    private void handleResponsePopular(MovieResponse movieResponse){
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        Glide.with(this).load("https://image.tmdb.org/t/p/original" + movies.get(0).getBackdrop_path()).into(search_bg_image);
    }

    private void loadDataSearch(){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        String search = text_search.getText().toString();

        compositeDisposable.add(apiService.getMovieSearch(search, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseSearch));

        text_search.setText("");
    }

    private void handleResponseDetails(Movie movie){
        movieList.add(movie);
        movieAdapter = new MovieAdapter(movieList, SearchResultActivity.this);
        recyclerView_search.setAdapter(movieAdapter);
    }

    private void handleResponseSearch(MovieResponse movieResponse) {
        movies = new ArrayList<>(movieResponse.getResults());

        List<Integer> idList = new ArrayList<>();

        for(Movie m : movies)
            idList.add(m.getId());

        search_result.setText(idList.size() + " records");
        loadData((ArrayList<Integer>) idList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
        unregisterReceiver(networkChangeReceiver);
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}