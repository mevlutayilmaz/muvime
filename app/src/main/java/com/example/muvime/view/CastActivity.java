package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muvime.R;
import com.example.muvime.adapter.CastAdapter;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.model.Cast;
import com.example.muvime.model.Movie;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.response.CastResponse;
import com.example.muvime.service.MovieApiService;
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

public class CastActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";

    private ImageView cast_image, bg_image;
    private TextView cast_name, cast_birthday, cast_place_of_birth, cast_biography, cast_known_for_department, cast_popularity, toolbar_tittle;
    private CastAdapter castAdapter;
    private RecyclerView recyclerView;

    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private List<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

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


        cast_name = findViewById(R.id.cast_activity_name);
        toolbar_tittle = findViewById(R.id.cast_toolbar_tittle);
        cast_biography = findViewById(R.id.cast_activity_biography);
        cast_place_of_birth = findViewById(R.id.cast_activity_place_of_birth);
        cast_birthday = findViewById(R.id.cast_activity_birthday);
        cast_image = findViewById(R.id.cast_activity_image);
        recyclerView = findViewById(R.id.recycler_know_job);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cast_known_for_department = findViewById(R.id.cast_activity_known_for_department);
        cast_popularity = findViewById(R.id.cast_popularity);
        bg_image = findViewById(R.id.cast_bg_image);


        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id",0);

            loadDataCredits(id);
            loadDataMovieCredits(id);
        }



        toolbar_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CastActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loadDataCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCastDetails(id, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCredits));
    }

    private void loadDataMovieCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCastMovieCredits(id, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseMovieCredits));
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

    private void handleResponseCredits(Cast cast){
        cast_name.setText(cast.getName());
        cast_biography.setText(cast.getBiography());
        cast_birthday.setText(cast.getBirthday() + " / " + cast.getDeathday());
        cast_place_of_birth.setText(cast.getPlace_of_birth());
        cast_popularity.setText(cast.getPopularity());
        cast_known_for_department.setText(cast.getKnown_for_department());
        String image_link = "https://image.tmdb.org/t/p/original"+cast.getProfile_path();
        Glide.with(this).load(image_link).into(cast_image);
        Glide.with(this).load(image_link).into(bg_image);

    }

    private void handleResponseMovieCredits(CastResponse castResponse){
        List<Cast> casts = new ArrayList<>(castResponse.getResults());

        ArrayList<Integer> idList = new ArrayList<>();

        int count = 0;
        for(Cast c : casts){
            if(count == 20)
                break;
            idList.add(c.getId());
            count++;
        }


        loadData(idList);
    }

    private void handleResponseDetails(Movie movie){
        movieList.add(movie);
        movieAdapter = new MovieAdapter(movieList, CastActivity.this);
        recyclerView.setAdapter(movieAdapter);
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

}