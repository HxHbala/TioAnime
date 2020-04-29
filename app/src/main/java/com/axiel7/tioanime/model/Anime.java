package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Anime {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("alt_titles")
    private String[] altTitles;
    @SerializedName("synopsis")
    private String synopsis;
    @SerializedName("type")
    private Integer type;
    @SerializedName("mal_id")
    private Integer malId;
    @SerializedName("status")
    private Integer status;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("next_date")
    private String nextDate;
    @SerializedName("wait_days")
    private Integer waitDays;
    @SerializedName("episodes_count")
    private Integer episodesCount;
    @SerializedName("episodes_count_guess")
    private Integer episodesCountGuess;
    @SerializedName("slug")
    private String slug;
    @SerializedName("created_at")
    private Integer createdAt;
    @SerializedName("uploaded_at")
    private Integer uploadedAt;
    @SerializedName("episodes")
    private List<Episode> episodes;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("relateds")
    private List<Related> relateds;
    @SerializedName("season")
    private Integer season;

    public Anime(Integer id, String title, String[] altTitles, String synopsis, Integer type, Integer malId,
                 Integer status, String startDate, String endDate, String nextDate, Integer waitDays,
                 Integer episodesCount, Integer episodesCountGuess, String slug, Integer createdAt, Integer uploadedAt,
                 List<Episode> episodes, List<Genre> genres, List<Related> relateds,Integer season) {
        this.id = id;
        this.title = title;
        this.altTitles = altTitles;
        this.synopsis = synopsis;
        this.type = type;
        this.malId = malId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDate = nextDate;
        this.waitDays = waitDays;
        this.episodesCount = episodesCount;
        this.episodesCountGuess = episodesCountGuess;
        this.slug = slug;
        this.createdAt = createdAt;
        this.uploadedAt = uploadedAt;
        this.episodes = episodes;
        this.genres = genres;
        this.relateds = relateds;
        this.season = season;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String[] getAltTitles() {
        return altTitles;
    }
    public void setAltTitles(String[] altTitles) {
        this.altTitles = altTitles;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getMalId() {
        return malId;
    }
    public void setMalId(Integer malId) {
        this.malId = malId;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getNextDate() {
        return nextDate;
    }
    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }
    public Integer getWaitDays() {
        return waitDays;
    }
    public void setWaitDays(Integer waitDays) {
        this.waitDays = waitDays;
    }
    public Integer getEpisodesCount() {
        return episodesCount;
    }
    public void setEpisodesCount(Integer episodesCount) {
        this.episodesCount = episodesCount;
    }
    public Integer getEpisodesCountGuess() {
        return episodesCountGuess;
    }
    public void setEpisodesCountGuess(Integer episodesCountGuess) {
        this.episodesCountGuess = episodesCountGuess;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public float getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }
    public float getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(Integer uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    public List<Episode> getEpisodes() {
        return episodes;
    }
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    public List<Related> getRelateds() {
        return relateds;
    }
    public void setRelateds(List<Related> relateds) {
        this.relateds = relateds;
    }
    public Integer getSeason() {
        return season;
    }
    public void setSeason(Integer season) {
        this.season = season;
    }
}
