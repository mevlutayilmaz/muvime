package com.example.muvime;

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
        String[] year = movie.getReleaseYear().split("-");
        Glide.with(context).load(movie.getImageResource()).into(holder.imageView);
        holder.nameTextView.setText(movie.getName());
        holder.categoryTextView.setText(movie.getCategory());
        holder.durationTextView.setText(movie.getDuration());
        holder.yearTextView.setText(String.valueOf(year[0]));
        holder.ratingTextView.setText(String.valueOf(movie.getRating()));

        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("name", movie.getName());
                intent.putExtra("imageResource", movie.getImageResource());
                intent.putExtra("bgImage", movie.getBg_image());
                intent.putExtra("category", movie.getCategory());
                //intent.putExtra("rating", movie.getRating());
                intent.putExtra("duration", movie.getDuration());
                intent.putExtra("year", movie.getReleaseYear());
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