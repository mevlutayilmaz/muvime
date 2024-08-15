package com.example.muvime.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class CastFragment extends Fragment {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "YOUR_API_KEY";

    private ImageView cast_image, bg_image;
    private TextView cast_name, cast_birthday, cast_place_of_birth, cast_biography, cast_known_for_department, cast_popularity;
    private RecyclerView recyclerView;
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private List<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private String language;
    private SharedPreferences sharedPreferences;

    public CastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cast, container, false);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        cast_name = view.findViewById(R.id.cast_activity_name);
        cast_biography = view.findViewById(R.id.cast_activity_biography);
        cast_place_of_birth = view.findViewById(R.id.cast_activity_place_of_birth);
        cast_birthday = view.findViewById(R.id.cast_activity_birthday);
        cast_image = view.findViewById(R.id.cast_activity_image);
        recyclerView = view.findViewById(R.id.recycler_know_job);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        cast_known_for_department = view.findViewById(R.id.cast_activity_known_for_department);
        cast_popularity = view.findViewById(R.id.cast_popularity);
        bg_image = view.findViewById(R.id.cast_bg_image);


        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("language" ,null);


        Bundle arguments = getArguments();
        if(arguments != null){
            int id = arguments.getInt("id", -1);

            loadDataCredits(id);
            loadDataMovieCredits(id);
        }

        return view;
    }

    private void loadDataCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCastDetails(id, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCredits));
    }

    private void loadDataMovieCredits(int id) {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getCastMovieCredits(id, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseMovieCredits));
    }

    private void loadData(ArrayList<Integer> idList){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        for(int id : idList){
            compositeDisposable.add(apiService.getMovieDetails(id, API_KEY, language)
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
        movieAdapter = new MovieAdapter(movieList, requireContext(), false);
        recyclerView.setAdapter(movieAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(compositeDisposable != null)
            compositeDisposable.clear();
    }
}
