package com.example.muvime.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.muvime.model.Movie;
import com.example.muvime.R;
import com.example.muvime.service.MovieApiService;
import com.example.muvime.view.DetailFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String session_id = "58c7db57ed28b057cd931d64eadaaef746684aaa";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";

    private List<Movie> movieList;
    private Context context;
    private boolean remove;
    private SQLiteDatabase database;
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private String username;
    private int list_id;

    public MovieAdapter(List<Movie> movieList, Context context, Boolean remove) {
        this.movieList = movieList;
        this.context = context;
        this.remove = remove;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        if(remove)
            holder.detail_remove.setVisibility(View.VISIBLE);

        sharedPreferences = context.getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        database = context.openOrCreateDatabase("Accounts", context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ?", new String[]{username});
        int index_list = cursor.getColumnIndex("listId");

        if (cursor.moveToFirst()) {
            list_id = cursor.getInt(index_list);
        }



        Movie movie = movieList.get(position);
        String image_link = "https://image.tmdb.org/t/p/original"+movie.getPoster_path();
        String release_date = movie.getRelease_date();
        String vote = new DecimalFormat("##.#").format(Double.parseDouble(movie.getVote_average()));

        Glide.with(context).load(image_link).into(holder.imageView);
        holder.nameTextView.setText(movie.getTitle());
        holder.yearTextView.setText(String.valueOf(release_date));
        holder.ratingTextView.setText(String.valueOf(vote));

        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager(); // Eğer fragment içindeyseniz getActivity() kullanın
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DetailFragment detailFragment = new DetailFragment(); // Diğer fragmentın sınıfını kullanın
                Bundle bundle = new Bundle();
                bundle.putInt("id", movie.getId());
                detailFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.main_frame_layout, detailFragment);
                fragmentTransaction.addToBackStack(null); // Geri düğmesine basıldığında önceki fragmenta dönebilmek için geri yığın ekleyin
                fragmentTransaction.commit();

                //Intent intent = new Intent(context, DetailActivity.class);
                //intent.putExtra("id", movie.getId());
//
                //context.startActivity(intent);
            }
        });

        holder.detail_remove.setOnClickListener(view -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                int id = movieList.get(position).getId();
                movieList.remove(clickedPosition);
                notifyItemRemoved(clickedPosition);

                loadDataRemoveMovie(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton detail_remove;
        TextView nameTextView;
        TextView yearTextView;
        TextView ratingTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_image);
            detail_remove = itemView.findViewById(R.id.detail_remove);
            nameTextView = itemView.findViewById(R.id.movie_name);
            yearTextView = itemView.findViewById(R.id.movie_year);
            ratingTextView = itemView.findViewById(R.id.movie_rating);
        }
    }

    private void loadDataRemoveMovie(int id){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        String movieJson = "{"
                + "\"media_id\": "+id+""
                + "}";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), movieJson);

        compositeDisposable.add(apiService.getRemoveMovieToList(list_id, API_KEY, session_id, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseRemoveMovie));
    }

    private void handleResponseRemoveMovie(ResponseBody responseBody) {
    }
}