package com.example.muvime.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.muvime.model.Movie;
import com.example.muvime.R;
import com.example.muvime.view.DetailActivity;

import java.text.DecimalFormat;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        String duration = movie.getRuntime()/60 + "h " + movie.getRuntime()%60 + "m";
        String image_link = "https://image.tmdb.org/t/p/original"+movie.getPoster_path();
        String bg_image_link = "https://image.tmdb.org/t/p/original"+movie.getBackdrop_path();;
        String release_date = movie.getRelease_date();
        String vote = new DecimalFormat("##.#").format(Double.parseDouble(movie.getVote_average()));
        String genres = "";

        for(Movie.Genre g : movie.getGenres())
            genres += g.getName() + ", ";


        String[] year = release_date.split("-");
        Glide.with(context).load(image_link).into(holder.imageView);
        holder.nameTextView.setText(movie.getTitle());
        holder.categoryTextView.setText(genres);
        holder.durationTextView.setText(duration);
        holder.yearTextView.setText(String.valueOf(year[0]));
        holder.ratingTextView.setText(String.valueOf(vote));

        String finalGenres = genres;
        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("name", movie.getTitle());
                intent.putExtra("imageResource", image_link);
                intent.putExtra("bgImage", bg_image_link);
                intent.putExtra("category", finalGenres);
                //intent.putExtra("rating", movie.getRating());
                intent.putExtra("duration", duration);
                intent.putExtra("year", release_date);
                intent.putExtra("overview", movie.getOverview());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView categoryTextView;
        TextView durationTextView;
        TextView yearTextView;
        TextView ratingTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_image);
            nameTextView = itemView.findViewById(R.id.movie_name);
            categoryTextView = itemView.findViewById(R.id.movie_category);
            durationTextView = itemView.findViewById(R.id.movie_hour);
            yearTextView = itemView.findViewById(R.id.movie_year);
            ratingTextView = itemView.findViewById(R.id.movie_rating);
        }
    }
}