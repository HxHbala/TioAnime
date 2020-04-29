package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class CategorySource {
    @SerializedName("main_title")
    private String categoryMainTitle;
    @SerializedName("titles")
    private String[] categoryTitles;
    @SerializedName("synopsis")
    private String categorySynopsis;
    @SerializedName("genres")
    private String[] categoryGenres;
    @SerializedName("type")
    private Integer categoryType;
    @SerializedName("status")
    private  Integer categoryStatus;
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

    public String getCategoryMainTitle() {
        return categoryMainTitle;
    }
    public void setCategoryMainTitle(String mainTitle) {
        this.categoryMainTitle = mainTitle;
    }
    public String[] getCategoryTitles() {
        return categoryTitles;
    }
    public void setCategoryTitles(String[] categoryTitles) {
        this.categoryTitles = categoryTitles;
    }
    public String getCategorySynopsis() {
        return categorySynopsis;
    }
    public void setCategorySynopsis(String categorySynopsis) {
        this.categorySynopsis = categorySynopsis;
    }
    public String[] getCategoryGenres() {
        return categoryGenres;
    }
    public void setCategoryGenres(String[] categoryGenres) {
        this.categoryGenres = categoryGenres;
    }
    public Integer getCategoryType() {
        return categoryType;
    }
    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
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
