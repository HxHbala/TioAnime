package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    private Integer genreId;
    @SerializedName("title")
    private String genreTitle;
    @SerializedName("slug")
    private String genreSlug;

    public Integer getGenreId() {
        return genreId;
    }
    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }
    public String getGenreTitle() {
        return genreTitle;
    }
    public void setGenreTitle(String genreTitle) {
        this.genreTitle = genreTitle;
    }
    public String getGenreSlug() {
        return genreSlug;
    }
    public void setGenreSlug(String genreSlug) {
        this.genreSlug = genreSlug;
    }
}
