package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class AnimeResponse {
    @SerializedName("data")
    private Anime animeData;

    public Anime getAnimeData() {
        return animeData;
    }

    public void setAnimeData(Anime animeData) {
        this.animeData = animeData;
    }
}
