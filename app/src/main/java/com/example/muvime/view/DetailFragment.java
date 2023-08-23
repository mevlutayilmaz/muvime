package com.example.muvime.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muvime.R;
import com.example.muvime.adapter.CastAdapter;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.model.Cast;
import com.example.muvime.model.Crew;
import com.example.muvime.model.Movie;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.response.CastResponse;
import com.example.muvime.response.CrewResponse;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.service.MovieApiService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";
    private static final String SESSION_ID = "58c7db57ed28b057cd931d64eadaaef746684aaa";
    private  int list_id, movie_id;
    private ImageView movie_image, detail_bg_image, add_btn;
    private TextView duration, category, name_year, realese_year, overview, director, writer;
    private RecyclerView recyclerView_more_like, recyclerView_top_cast;
    private CastAdapter adapter;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private List<Movie> movieListTopRated = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private String movieJson, username, language;
    private SharedPreferences sharedPreferences;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loadDataTopRated();

        movie_image = view.findViewById(R.id.detail_movie_image);
        add_btn = view.findViewById(R.id.detail_add);
        director = view.findViewById(R.id.detail_director);
        writer = view.findViewById(R.id.detail_writer);
        detail_bg_image = view.findViewById(R.id.detail_bg_image);
        overview = view.findViewById(R.id.detail_overview);
        realese_year = view.findViewById(R.id.detail_realese_year);
        duration = view.findViewById(R.id.detail_hour);
        duration = view.findViewById(R.id.detail_hour);
        name_year = view.findViewById(R.id.detail_name_year);
        category = view.findViewById(R.id.detail_category);

        recyclerView_more_like = view.findViewById(R.id.recycler_more_like);
        recyclerView_top_cast = view.findViewById(R.id.recycler_top_cast);
        recyclerView_more_like.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView_top_cast.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        language = sharedPreferences.getString("language" ,null);

        database = getActivity().openOrCreateDatabase("Accounts", getActivity().MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ?", new String[]{username});
        int index_list = cursor.getColumnIndex("listId");

        if (cursor.moveToFirst())
            list_id = cursor.getInt(index_list);


        Bundle arguments = getArguments();
        if(arguments != null){
            movie_id = arguments.getInt("id", -1);

            movieJson = "{"
                    + "\"media_id\": "+movie_id+""
                    + "}";

            loadDataMovie();
            loadDataCredits(movie_id);
            loadDataCrew(movie_id);
        }



        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieApiService movieApiService = retrofit2.create(MovieApiService.class);

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), movieJson);

                Call<ResponseBody> call = movieApiService.getAddMovieToList(list_id, API_KEY, SESSION_ID, requestBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view, getResources().getString(R.string.movie_added), Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(view, getResources().getString(R.string.movie_added_already), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });


        return view;
    }

    private void loadDataMovie(){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();


        compositeDisposable.add(apiService.getMovieDetails(movie_id, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseMovie));
    }

    private void handleResponseMovie(Movie movie){
        String[] year = movie.getRelease_date().split("-");
        String runtime = movie.getRuntime()/60 + "h " + movie.getRuntime()%60 + "m";
        String image_link = "https://image.tmdb.org/t/p/original"+movie.getPoster_path();
        String bg_image_link = "https://image.tmdb.org/t/p/original"+movie.getBackdrop_path();
        String genres = "";

        for(Movie.Genre g : movie.getGenres())
            genres += g.getName() + ", ";

        name_year.setText(movie.getTitle() + " (" + year[0] + ")");
        realese_year.setText(movie.getRelease_date());
        duration.setText(runtime);
        category.setText(genres);
        overview.setText(movie.getOverview());
        Glide.with(this).load(image_link).into(movie_image);
        Glide.with(this).load(bg_image_link).into(detail_bg_image);
    }


    private void loadDataTopRated() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getTopRatedMovies(API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseTopRated));
    }

    private void loadDataCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCreditsCast(id, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCredits));
    }

    private void loadDataCrew(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCrewDetails(id, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCrew));
    }

    private void handleResponseTopRated(MovieResponse movieResponse){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        for(Movie m : movies){
            compositeDisposable.add(apiService.getMovieDetails(m.getId(), API_KEY, language)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseDetailsTopRated));

        }
    }

    private void handleResponseCredits(CastResponse castResponse){
        List<Cast> casts = new ArrayList<>(castResponse.getResults());

        adapter = new CastAdapter(casts, getActivity());
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
        movieAdapter = new MovieAdapter(movieListTopRated, requireContext(), false);
        recyclerView_more_like.setAdapter(movieAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(compositeDisposable != null)
            compositeDisposable.clear();

    }
}