package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class PageInfo {
    @SerializedName("current")
    private Integer currentPage;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("limit")
    private Integer pageSize;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
