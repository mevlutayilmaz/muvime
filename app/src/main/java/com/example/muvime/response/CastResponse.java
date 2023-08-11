package com.example.muvime.response;

import com.example.muvime.model.Cast;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {

    @SerializedName("cast")
    private List<Cast> results;

    // Diğer gerekli alanları ekleyebilirsiniz.

    public List<Cast> getResults() {
        return results;
    }

    // Diğer gerekli getter ve setter metotlarını ekleyebilirsiniz.
}
