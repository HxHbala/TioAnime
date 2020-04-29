package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class LatestEpisode {
    @SerializedName("anime_id")
    private Integer latestAnimeId;
    @SerializedName("title")
    private String latestTitle;
    @SerializedName("type")
    private Integer latestType;
    @SerializedName("slug")
    private String latestSlug;
    @SerializedName("episode_id")
    private Integer latestEpisodeId;
    @SerializedName("number")
    private Integer latestEpisodeNumber;
    @SerializedName("created_at")
    private Integer latestCreatedAt;

    public LatestEpisode(Integer latestAnimeId, String latestTitle, Integer latestType, String latestSlug, Integer latestEpisodeId, Integer latestEpisodeNumber, Integer latestCreatedAt) {
        this.latestAnimeId = latestAnimeId;
        this.latestTitle = latestTitle;
        this.latestType = latestType;
        this.latestSlug = latestSlug;
        this.latestEpisodeId = latestEpisodeId;
        this.latestEpisodeNumber = latestEpisodeNumber;
        this.latestCreatedAt = latestCreatedAt;
    }

    public Integer getLatestAnimeId() {
        return latestAnimeId;
    }

    public void setLatestAnimeId(Integer latestAnimeId) {
        this.latestAnimeId = latestAnimeId;
    }

    public String getLatestTitle() {
        return latestTitle;
    }

    public void setLatestTitle(String latestTitle) {
        this.latestTitle = latestTitle;
    }

    public Integer getLatestType() {
        return latestType;
    }

    public void setLatestType(Integer latestType) {
        this.latestType = latestType;
    }

    public String getLatestSlug() {
        return latestSlug;
    }

    public void setLatestSlug(String latestSlug) {
        this.latestSlug = latestSlug;
    }

    public Integer getLatestEpisodeId() {
        return latestEpisodeId;
    }

    public void setLatestEpisodeId(Integer latestEpisodeId) {
        this.latestEpisodeId = latestEpisodeId;
    }

    public Integer getLatestEpisodeNumber() {
        return latestEpisodeNumber;
    }

    public void setLatestEpisodeNumber(Integer latestEpisodeNumber) {
        this.latestEpisodeNumber = latestEpisodeNumber;
    }

    public Integer getLatestCreatedAt() {
        return latestCreatedAt;
    }

    public void setLatestCreatedAt(Integer latestCreatedAt) {
        this.latestCreatedAt = latestCreatedAt;
    }
}
