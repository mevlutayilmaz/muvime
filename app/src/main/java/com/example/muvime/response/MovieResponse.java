package com.example.muvime.response;

import com.example.muvime.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> results;

    // Diğer gerekli alanları ekleyebilirsiniz.

    public List<Movie> getResults() {
        return results;
    }

    // Diğer gerekli getter ve setter metotlarını ekleyebilirsiniz.
}

