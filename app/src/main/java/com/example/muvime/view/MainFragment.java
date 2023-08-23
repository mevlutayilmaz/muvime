package com.example.muvime.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muvime.R;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.model.Movie;
import com.example.muvime.network.NetworkChangeReceiver;
import com.example.muvime.response.MovieResponse;
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


public class MainFragment extends Fragment {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";

    private Button button_watch;
    private ImageView popular_bg_image;
    private TextView popular_tittle, popular_overview;
    private ImageButton btn_search;
    private EditText text_search;
    private RecyclerView recyclerView_popular, recycler_view_free;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private String language;
    private SharedPreferences sharedPreferences;

    public MainFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        button_watch = view.findViewById(R.id.button_watch);
        popular_bg_image = view.findViewById(R.id.popular_bg_image);
        popular_tittle = view.findViewById(R.id.popular_tittle);
        popular_overview = view.findViewById(R.id.popular_overview);
        btn_search = view.findViewById(R.id.main_btn_search);
        text_search = view.findViewById(R.id.main_text_search);
        recyclerView_popular = view.findViewById(R.id.recycler_view_popular);
        recycler_view_free = view.findViewById(R.id.recycler_view_free);
        recyclerView_popular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_free.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("language" ,null);



        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loadDataPopular();
        loadDataTopRated();


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataSearch();
            }
        });


        return view;
    }

    private void loadDataSearch(){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        String search = text_search.getText().toString();

        compositeDisposable.add(apiService.getMovieSearch(search, API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseSearch));
    }

    private void loadDataPopular() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getPopularMovies(API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponsePopular));
    }

    private void loadDataTopRated() {
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(apiService.getTopRatedMovies(API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseTopRated));
    }

    private void handleResponseSearch(MovieResponse movieResponse) {
        movies = new ArrayList<>(movieResponse.getResults());

        List<Integer> idList = new ArrayList<>();

        for(Movie m : movies)
            idList.add(m.getId());


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("idList", (ArrayList<Integer>) idList);
        searchResultFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_frame_layout, searchResultFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void handleResponsePopular(MovieResponse movieResponse){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        popular_overview.setText(movies.get(0).getOverview());
        popular_tittle.setText(movies.get(0).getTitle());
        Glide.with(this).load("https://image.tmdb.org/t/p/original" + movies.get(0).getBackdrop_path()).into(popular_bg_image);

        movieAdapter = new MovieAdapter(movies, requireContext(), false);
        recyclerView_popular.setAdapter(movieAdapter);
    }

    private void handleResponseTopRated(MovieResponse movieResponse){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        movies = new ArrayList<>(movieResponse.getResults());
        compositeDisposable = new CompositeDisposable();

        movieAdapter = new MovieAdapter(movies, requireContext(), false);
        recycler_view_free.setAdapter(movieAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(compositeDisposable != null)
            compositeDisposable.clear();
    }
}