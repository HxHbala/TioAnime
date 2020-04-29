package com.axiel7.tioanime.model;

public class FavAnime {
    private Integer animeId;
    private String animeTitle;
    private Integer animeType;
    private String animePosterUrl;
    private int orderIndex;

    public Integer getAnimeId() {
        return animeId;
    }

    public void setAnimeId(Integer animeId) {
        this.animeId = animeId;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public Integer getAnimeType() {
        return animeType;
    }

    public void setAnimeType(Integer animeType) {
        this.animeType = animeType;
    }

    public String getAnimePosterUrl() {
        return animePosterUrl;
    }

    public void setAnimePosterUrl(String animePosterUrl) {
        this.animePosterUrl = animePosterUrl;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
