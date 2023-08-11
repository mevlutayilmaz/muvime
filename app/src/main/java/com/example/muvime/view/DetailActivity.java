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
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muvime.model.Cast;
import com.example.muvime.adapter.CastAdapter;
import com.example.muvime.model.Crew;
import com.example.muvime.model.Movie;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.response.CastResponse;
import com.example.muvime.response.CrewResponse;
import com.example.muvime.service.MovieApiService;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.R;
import com.google.android.material.snackbar.Snackbar;
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

public class DetailActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";

    private ImageView movie_image, detail_bg_image, add_btn;
    private TextView duration, category, name_year, realese_year, overview, director, writer, toolbar_tittle;
    private RecyclerView recyclerView_more_like, recyclerView_top_cast;
    private CastAdapter adapter;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private List<Movie> movieListTopRated = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        loadDataTopRated();


        movie_image = findViewById(R.id.detail_movie_image);
        add_btn = findViewById(R.id.detail_add);
        director = findViewById(R.id.detail_director);
        writer = findViewById(R.id.detail_writer);
        detail_bg_image = findViewById(R.id.detail_bg_image);
        overview = findViewById(R.id.detail_overview);
        realese_year = findViewById(R.id.detail_realese_year);
        duration = findViewById(R.id.detail_hour);
        duration = findViewById(R.id.detail_hour);
        name_year = findViewById(R.id.detail_name_year);
        category = findViewById(R.id.detail_category);

        recyclerView_more_like = findViewById(R.id.recycler_more_like);
        toolbar_tittle = findViewById(R.id.detail_toolbar_tittle);
        recyclerView_top_cast = findViewById(R.id.recycler_top_cast);
        recyclerView_more_like.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_top_cast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id",0);
            String movie_name = intent.getStringExtra("name");
            String movie_imageResource = intent.getStringExtra("imageResource");
            String movie_category = intent.getStringExtra("category");
            String movie_duration = intent.getStringExtra("duration");
            String movie_year = intent.getStringExtra("year");
            String movie_overview = intent.getStringExtra("overview");
            String movie_bg_image = intent.getStringExtra("bgImage");

            String[] year = movie_year.split("-");

            name_year.setText(movie_name + " (" + year[0] + ")");
            realese_year.setText(movie_year);
            duration.setText(movie_duration);
            category.setText(movie_category);
            overview.setText(movie_overview);
            Glide.with(this).load(movie_imageResource).into(movie_image);
            Glide.with(this).load(movie_bg_image).into(detail_bg_image);

            loadDataCredits(id);
            loadDataCrew(id);
        }

        toolbar_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Movie added to Watchlist.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadDataTopRated() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getTopRatedMovies(API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseTopRated));
    }

    private void loadDataCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCreditsCast(id, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCredits));
    }

    private void loadDataCrew(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCrewDetails(id, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCrew));
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

    private void handleResponseCredits(CastResponse castResponse){
        List<Cast> casts = new ArrayList<>(castResponse.getResults());

        adapter = new CastAdapter(casts, this);
        recyclerView_top_cast.setAdapter(adapter);
    }

    private void handleResponseCrew(CrewResponse crewResponse){
        List<Crew> crews = new ArrayList<>(crewResponse.getResults());

        for(Crew c : crews){
            if(c.getJob().equals("Director"))
                director.setText(c.getName());
            if(c.getJob().equals("Screenplay") || c.getJob().equals("Writer"))
                writer.setText(writer.getText() + c.getName()+", ");
        }

    }

    private void handleResponseDetailsTopRated(Movie movie){
        movieListTopRated.add(movie);
        movieAdapter = new MovieAdapter(movieListTopRated, DetailActivity.this);
        recyclerView_more_like.setAdapter(movieAdapter);
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