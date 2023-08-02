package com.example.muvime;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<MovieDeneme> results;

    // Diğer gerekli alanları ekleyebilirsiniz.

    public List<MovieDeneme> getResults() {
        return results;
    }

    // Diğer gerekli getter ve setter metotlarını ekleyebilirsiniz.
}

