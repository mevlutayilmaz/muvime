package com.example.muvime.response;

import com.example.muvime.model.Crew;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CrewResponse {

    @SerializedName("crew")
    private List<Crew> results;

    // Diğer gerekli alanları ekleyebilirsiniz.

    public List<Crew> getResults() {
        return results;
    }

    // Diğer gerekli getter ve setter metotlarını ekleyebilirsiniz.
}
