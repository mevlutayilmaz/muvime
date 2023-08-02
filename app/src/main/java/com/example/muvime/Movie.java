package com.example.muvime;

public class Movie{
    private String imageResource;
    private String name;
    private String category;
    private String duration;
    private String releaseYear;
    private double rating;
    private String overview;
    private String bg_image;

    public String getBg_image() {
        return bg_image;
    }

    public void setBg_image(String bg_image) {
        this.bg_image = bg_image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Movie(String imageResource, String name, String category, String duration, String releaseYear, double rating, String overview, String bg_image) {
        this.imageResource = imageResource;
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.overview = overview;
        this.bg_image = bg_image;
    }

    public Movie(){}

}