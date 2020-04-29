package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LatestEpisodesResponse {
    @SerializedName("data")
    private List<LatestEpisode> data;
    @SerializedName("meta/title")
    private String metaTitle;
    @SerializedName("meta/status")
    private Integer status;
    @SerializedName("pageInfo/current")
    private int current;
    @SerializedName("pageInfo/total_pages")
    private int totalPages;
    @SerializedName("pageInfo/limit")
    private int limit;

    public List<LatestEpisode> getData() {
        return data;
    }
    public void setData(List<LatestEpisode> data) {
        this.data = data;
    }
    public String getMetaTitle() {
        return metaTitle;
    }
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public int getCurrent() {
        return current;
    }
    public void setCurrent(int current) {
        this.current = current;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
