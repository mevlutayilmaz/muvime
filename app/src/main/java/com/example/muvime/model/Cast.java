package com.example.muvime.model;

public class Cast {
    private int id;
    private String name;
    private String birthday;
    private String biography;
    private String place_of_birth;
    private String character;
    private String profile_path;
    private String known_for_department;
    private String deathday;
    private String popularity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public Cast(int id, String name, String birthday, String biography, String place_of_birth, String character, String profile_path, String known_for_department, String deathday, String popularity) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.biography = biography;
        this.place_of_birth = place_of_birth;
        this.character = character;
        this.profile_path = profile_path;
        this.known_for_department = known_for_department;
        this.deathday = deathday;
        this.popularity = popularity;
    }

    public Cast(){}
}
