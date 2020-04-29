package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("id")
    private Integer episodeId;
    @SerializedName("anime_id")
    private Integer animeId;
    @SerializedName("number")
    private Integer number;
    @SerializedName("filler")
    private Integer filler;
    @SerializedName("owner")
    private Integer owner;
    @SerializedName("created_at")
    private Integer createdAt;
    @SerializedName("uploaded_at")
    private Integer uploadedAt;
    @SerializedName("videos")
    private String[][] videos;

    public Episode(Integer episodeId, Integer animeId, Integer number, Integer filler, Integer owner,
                   Integer createdAt, Integer uploadedAt, String[][] videos) {
        this.episodeId = episodeId;
        this.animeId = animeId;
        this.number = number;
        this.filler = filler;
        this.owner = owner;
        this.createdAt = createdAt;
        this.uploadedAt = uploadedAt;
        this.videos = videos;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }
    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }
    public Integer getAnimeId() {
        return animeId;
    }
    public void setAnimeId(Integer animeId) {
        this.animeId = animeId;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public Integer getFiller() {
        return filler;
    }
    public void setFiller(Integer filler) {
        this.filler = filler;
    }
    public Integer getOwner() {
        return owner;
    }
    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    public Integer getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(Integer uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    public String[][] getVideos() {
        return videos;
    }
    public void setVideos(String[][] videos) {
        this.videos = videos;
    }
}
