package com.example.muvime;

public class Cast {
    private int imageResource;
    private String name;
    private String character;

    private int episode;

    public Cast(int imageResource, String name, String character, int episode) {
        this.imageResource = imageResource;
        this.name = name;
        this.character = character;
        this.episode = episode;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public Cast(){}

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

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }
}
