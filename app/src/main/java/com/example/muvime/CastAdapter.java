package com.example.muvime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muvime.Cast;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;
    private Context context;

    public CastAdapter(List<Cast> castList, Context context) {
        this.castList = castList;
        this.context = context;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_card, parent, false);
        return new CastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);

        holder.imageView.setImageResource(cast.getImageResource());
        holder.nameTextView.setText(cast.getName());
        holder.characterTextView.setText(cast.getCharacter());
        holder.episodeTextView.setText("Episode: " + cast.getEpisode());
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView characterTextView;
        public TextView episodeTextView;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cast_image);
            nameTextView = itemView.findViewById(R.id.cast_name);
            characterTextView = itemView.findViewById(R.id.cast_character);
            episodeTextView = itemView.findViewById(R.id.cast_episode);
        }
    }
}
