package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.muvime.model.Movie;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.service.MovieApiService;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";
    private String session_id;
    private int list_id;

    private Button button_watch;
    private ImageView popular_bg_image, account_image;
    private TextView popular_tittle, popular_overview, toolbar_tittle;
    private ImageButton btn_search;
    private EditText text_search;
    private RecyclerView recyclerView_popular, recycler_view_free;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private List<Movie> movieListPopular = new ArrayList<>();
    private List<Movie> movieListTopRated = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkChangeReceiver = new NetworkChangeReceiver();
        registerNetworkChangeReceiver();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        button_watch = findViewById(R.id.button_watch);
        account_image = findViewById(R.id.account_image);
        toolbar_tittle = findViewById(R.id.main_toolbar_tittle);
        popular_bg_image = findViewById(R.id.popular_bg_image);
        popular_tittle = findViewById(R.id.popular_tittle);
        popular_overview = findViewById(R.id.popular_overview);
        btn_search = findViewById(R.id.main_btn_search);
        text_search = findViewById(R.id.main_text_search);
        recyclerView_popular = findViewById(R.id.recycler_view_popular);
        recycler_view_free = findViewById(R.id.recycler_view_free);
        recyclerView_popular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_view_free.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loadDataPopular();
        loadDataTopRated();


        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<MovieApiService.GuestSessionResponse> call = apiService.getGuestSessionId(API_KEY);
        call.enqueue(new Callback<MovieApiService.GuestSessionResponse>() {
            @Override
            public void onResponse(Call<MovieApiService.GuestSessionResponse> call, Response<MovieApiService.GuestSessionResponse> response) {
                if (response.isSuccessful()) {
                    MovieApiService.GuestSessionResponse guestSessionResponse = response.body();
                    session_id = guestSessionResponse.getGuestSessionId();
                    Log.v("Tag", "merhaba  " + guestSessionResponse.getGuestSessionId());
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<MovieApiService.GuestSessionResponse> call, Throwable t) {
                // Handle error
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataSearch();
            }
        });

        toolbar_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        account_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataSearch(){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        String search = text_search.getText().toString();

        compositeDisposable.add(apiService.getMovieSearch(search, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseSearch));
    }

    private void loadDataPopular() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getPopularMovies(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponsePopular));
    }

    private void loadDataTopRated() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getTopRatedMovies(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseTopRated));
    }

    private void handleResponseSearch(MovieResponse movieResponse) {
        movies = new ArrayList<>(movieResponse.getResults());

        List<Integer> idList = new ArrayList<>();

        for(Movie m : movies)
            idList.add(m.getId());

        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
        intent.putIntegerArrayListExtra("idList", (ArrayList<Integer>) idList);
        startActivity(intent);
    }

    private void handleResponsePopular(MovieResponse movieResponse){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        popular_overview.setText(movies.get(0).getOverview());
        popular_tittle.setText(movies.get(0).getTitle());
        Glide.with(this).load("https://image.tmdb.org/t/p/original" + movies.get(0).getBackdrop_path()).into(popular_bg_image);

        for(Movie m : movies){
            compositeDisposable.add(apiService.getMovieDetails(m.getId(), API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseDetailsPopular));
        }
    }

    private void handleResponseTopRated(MovieResponse movieResponse){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        for(Movie m : movies){
            compositeDisposable.add(apiService.getMovieDetails(m.getId(), API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseDetailsTopRated));

        }
    }

    private void handleResponseDetailsPopular(Movie movie){
        movieListPopular.add(movie);
        movieAdapter = new MovieAdapter(movieListPopular, MainActivity.this);
        recyclerView_popular.setAdapter(movieAdapter);
    }

    private void handleResponseDetailsTopRated(Movie movie){
        movieListTopRated.add(movie);
        movieAdapter = new MovieAdapter(movieListTopRated, MainActivity.this);
        recycler_view_free.setAdapter(movieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);
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

}