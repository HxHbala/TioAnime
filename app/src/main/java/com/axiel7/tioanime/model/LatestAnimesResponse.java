package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LatestAnimesResponse {
    @SerializedName("data")
    private List<Category> latestAnimes;
    @SerializedName("pageInfo")
    private PageInfo pageInfo;

    public List<Category> getLatestAnimes() {
        return latestAnimes;
    }

    public void setLatestAnimes(List<Category> latestAnimes) {
        this.latestAnimes = latestAnimes;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
