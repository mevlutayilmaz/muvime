package com.example.muvime.response;

import com.example.muvime.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WatchListResponse {

    @SerializedName("items")
    private List<Movie> items;

    public List<Movie> getItems() {
        return items;
    }
}
