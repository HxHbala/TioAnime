package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class GenreResponse {
    @SerializedName("data")
    private Genre genre;

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
