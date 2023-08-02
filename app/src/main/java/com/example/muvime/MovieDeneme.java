package com.example.muvime;

import java.util.List;

public class MovieDeneme {
    private int id;
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;
    private String vote_average;
    private int runtime;
    private List<Genre> genres;
    private String backdrop_path;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public MovieDeneme(int id, String title, String poster_path, String overview, String release_date, String vote_average, int runtime, List<Genre> genres, String backdrop_path) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.runtime = runtime;
        this.genres = genres;
        this.backdrop_path = backdrop_path;
    }

    public MovieDeneme(){}

    public static class Genre {
        private String name;
        private int id;


        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}


