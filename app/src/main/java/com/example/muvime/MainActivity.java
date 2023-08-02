package com.example.muvime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";

    private Button button_watch;
    private ImageButton btn_search;
    private EditText text_search;
    private RecyclerView recyclerView_popular, recycler_view_free;
    private MovieAdapter movieAdapter;
    private List<MovieDeneme> movieDenemes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String toolbarTitle = "MUVİME";
        SpannableString spannableString = new SpannableString(toolbarTitle);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, toolbarTitle.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        button_watch = findViewById(R.id.button_watch);
        btn_search = findViewById(R.id.main_btn_search);
        text_search = findViewById(R.id.main_text_search);
        recyclerView_popular = findViewById(R.id.recycler_view_popular);
        recycler_view_free = findViewById(R.id.recycler_view_free);
        recyclerView_popular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_view_free.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //********************************************************************
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiService apiService = retrofit.create(MovieApiService.class);
        //**********************************************************************

        //****************************************** Popular Movies ****************************************************
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    movieDenemes = movieResponse.getResults();

                    List<Movie> movieList = new ArrayList<>();
                    for(MovieDeneme m : movieDenemes){
                        Call<MovieDeneme> call2 = apiService.getMovieDetails(m.getId(), API_KEY);
                        call2.enqueue(new Callback<MovieDeneme>() {
                            @Override
                            public void onResponse(Call<MovieDeneme> call2, Response<MovieDeneme> response2) {
                                if (response2.isSuccessful()) {
                                    MovieDeneme movie = response2.body();

                                    String duration = movie.getRuntime()/60 + "h " + movie.getRuntime()%60 + "m";
                                    String image_link = "https://image.tmdb.org/t/p/w500"+movie.getPoster_path();
                                    String bg_image_link = "https://image.tmdb.org/t/p/w500"+movie.getBackdrop_path();;
                                    String year = movie.getRelease_date();
                                    String vote = new DecimalFormat("##.#").format(Double.parseDouble(movie.getVote_average()));
                                    String genres = "";

                                    for(MovieDeneme.Genre g : movie.getGenres())
                                        genres += g.getName() + ", ";

                                    movieList.add(new Movie(image_link, movie.getTitle(), genres, duration, year, Double.parseDouble(vote), movie.getOverview(), bg_image_link));

                                    movieAdapter = new MovieAdapter(movieList, MainActivity.this);
                                    recyclerView_popular.setAdapter(movieAdapter);


                                } else {
                                    Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieDeneme> call2, Throwable t2) {
                                Toast.makeText(MainActivity.this, "API call failed: " + t2.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } else {
                    Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //************************************************************************************************************************

        //************************************************* Top Rated Movies ****************************************************

        Call<MovieResponse> callTop = apiService.getTopRatedMovies(API_KEY);
        callTop.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if(response.isSuccessful()){
                    MovieResponse movieResponse = response.body();
                    movieDenemes = movieResponse.getResults();

                    List<Movie> movieList = new ArrayList<>();

                    for(MovieDeneme m : movieDenemes){
                        Call<MovieDeneme> call2 = apiService.getMovieDetails(m.getId(), API_KEY);
                        call2.enqueue(new Callback<MovieDeneme>() {
                            @Override
                            public void onResponse(Call<MovieDeneme> call2, Response<MovieDeneme> response2) {
                                if (response2.isSuccessful()) {
                                    MovieDeneme movie = response2.body();

                                    String duration = movie.getRuntime()/60 + "h " + movie.getRuntime()%60 + "m";
                                    String image_link = "https://image.tmdb.org/t/p/w500"+movie.getPoster_path();
                                    String bg_image_link = "https://image.tmdb.org/t/p/w500"+movie.getBackdrop_path();;
                                    String year = movie.getRelease_date();
                                    String vote = new DecimalFormat("##.#").format(Double.parseDouble(movie.getVote_average()));
                                    String genres = "";

                                    for(MovieDeneme.Genre g : movie.getGenres())
                                        genres += g.getName() + ", ";

                                    movieList.add(new Movie(image_link, movie.getTitle(), genres, duration, year, Double.parseDouble(vote), movie.getOverview(), bg_image_link));

                                    movieAdapter = new MovieAdapter(movieList, MainActivity.this);
                                    recycler_view_free.setAdapter(movieAdapter);

                                } else {
                                    Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieDeneme> call2, Throwable t2) {
                                Toast.makeText(MainActivity.this, "API call failed: " + t2.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //************************************************************************************************************************

        //********************************************** Search Movies ***********************************************************
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = text_search.getText().toString();

                Call<MovieResponse> callSearch = apiService.getMovieSearch(search, API_KEY);
                callSearch.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            MovieResponse movieResponse = response.body();
                            movieDenemes = movieResponse.getResults();
                            List<Integer> idList = new ArrayList<>();

                            for(MovieDeneme m : movieDenemes)
                                idList.add(m.getId());

                            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                            intent.putIntegerArrayListExtra("idList", (ArrayList<Integer>) idList);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //************************************************************************************************************************

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}