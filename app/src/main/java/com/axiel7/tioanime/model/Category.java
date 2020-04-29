package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {
    @SerializedName("_index")
    private String index;
    @SerializedName("_type")
    private String indexType;
    @SerializedName("_id")
    private Integer categoryAnimeId;
    @SerializedName("_score")
    private Integer categoryScore;
    @SerializedName("_source")
    private CategorySource source;
    @SerializedName("genres")
    private List<Integer> genres;
    @SerializedName("type")
    private Integer type;
    @SerializedName("status")
    private Integer categoryStatus;
    @SerializedName("start_date")
    private String categoryStartDate;
    @SerializedName("end_date")
    private String categoryEndDate;
    @SerializedName("slug")
    private String categorySlug;
    @SerializedName("created_at")
    private Integer categoryCreatedAt;
    @SerializedName("updated_at")
    private Integer categoryUpdatedAt;

    public Category(String index, String indexType, Integer categoryAnimeId, Integer categoryScore,
                    CategorySource source, List<Integer> genres, Integer type, Integer categoryStatus, String categoryStartDate,
                    String categoryEndDate, String categorySlug, Integer categoryCreatedAt, Integer categoryUpdatedAt) {
        this.index = index;
        this.indexType = indexType;
        this.categoryAnimeId = categoryAnimeId;
        this.categoryScore = categoryScore;
        this.source = source;
        this.genres = genres;
        this.type = type;
        this.categoryStatus = categoryStatus;
        this.categoryStartDate = categoryStartDate;
        this.categoryEndDate = categoryEndDate;
        this.categorySlug = categorySlug;
        this.categoryCreatedAt = categoryCreatedAt;
        this.categoryUpdatedAt = categoryUpdatedAt;
    }

    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    public String getIndexType() {
        return indexType;
    }
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
    public Integer getCategoryAnimeId() {
        return categoryAnimeId;
    }
    public void setCategoryAnimeId(Integer categoryAnimeId) {
        this.categoryAnimeId = categoryAnimeId;
    }
    public Integer getCategoryScore() {
        return categoryScore;
    }
    public void setCategoryScore(Integer categoryScore) {
        this.categoryScore = categoryScore;
    }
    public CategorySource getSource() {
        return source;
    }
    public void setSource(CategorySource source) {
        this.source = source;
    }
    public List<Integer> getGenres() {
        return genres;
    }
    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getCategoryStatus() {
        return categoryStatus;
    }
    public void setCategoryStatus(Integer categoryStatus) {
        this.categoryStatus = categoryStatus;
    }
    public String getCategoryStartDate() {
        return categoryStartDate;
    }
    public void setCategoryStartDate(String categoryStartDate) {
        this.categoryStartDate = categoryStartDate;
    }
    public String getCategoryEndDate() {
        return categoryEndDate;
    }
    public void setCategoryEndDate(String categoryEndDate) {
        this.categoryEndDate = categoryEndDate;
    }
    public String getCategorySlug() {
        return categorySlug;
    }
    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }
    public Integer getCategoryCreatedAt() {
        return categoryCreatedAt;
    }
    public void setCategoryCreatedAt(Integer categoryCreatedAt) {
        this.categoryCreatedAt = categoryCreatedAt;
    }
    public Integer getCategoryUpdatedAt() {
        return categoryUpdatedAt;
    }
    public void setCategoryUpdatedAt(Integer categoryUpdatedAt) {
        this.categoryUpdatedAt = categoryUpdatedAt;
    }
}
