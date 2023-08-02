package com.example.muvime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    MovieApiService apiService = retrofit.create(MovieApiService.class);

    private RecyclerView recyclerView_search;
    private MovieAdapter movieAdapter;
    private TextView search_result;
    private EditText text_search;
    private ImageButton btn_search;
    private List<MovieDeneme> movieDenemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        search_result = findViewById(R.id.search_result);
        text_search = findViewById(R.id.search_text_search);
        btn_search = findViewById(R.id.search_btn_search);
        recyclerView_search = findViewById(R.id.recycler_search);
        recyclerView_search.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String toolbarTitle = "MUVİME";
        SpannableString spannableString = new SpannableString(toolbarTitle);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, toolbarTitle.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        Intent intent = getIntent();
        ArrayList<Integer> idList = intent.getIntegerArrayListExtra("idList");

        search_result.setText(idList.size() + " records");
        addRecyclerViews(idList);


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

                            search_result.setText(idList.size() + " records");

                            addRecyclerViews((ArrayList<Integer>) idList);
                            text_search.setText("");

                        } else {
                            Toast.makeText(SearchResultActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Toast.makeText(SearchResultActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void addRecyclerViews(ArrayList<Integer> idList){
        List<Movie> movieList = new ArrayList<>();
        for(int id : idList){
            Call<MovieDeneme> call = apiService.getMovieDetails(id, API_KEY);
            call.enqueue(new Callback<MovieDeneme>() {
                @Override
                public void onResponse(Call<MovieDeneme> call, Response<MovieDeneme> response) {
                    if(response.isSuccessful()){
                        MovieDeneme movie = response.body();

                        String duration = movie.getRuntime()/60 + "h " + movie.getRuntime()%60 + "m";
                        String image_link = "https://image.tmdb.org/t/p/w500"+movie.getPoster_path();
                        String bg_image_link = "https://image.tmdb.org/t/p/w500"+movie.getBackdrop_path();;
                        String year = movie.getRelease_date();
                        String vote = new DecimalFormat("##.#").format(Double.parseDouble(movie.getVote_average()));
                        String genres = "";

                        for(MovieDeneme.Genre g : movie.getGenres())
                            genres += g.getName() + ", ";

                        movieList.add(new Movie(image_link, movie.getTitle(), genres, duration, year, Double.parseDouble(vote), movie.getOverview(), bg_image_link));
                        movieAdapter = new MovieAdapter(movieList, SearchResultActivity.this);
                        recyclerView_search.setAdapter(movieAdapter);


                    }else{
                        Toast.makeText(SearchResultActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieDeneme> call, Throwable t) {
                    Toast.makeText(SearchResultActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}