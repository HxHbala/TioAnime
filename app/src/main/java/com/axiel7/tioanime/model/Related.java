package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class Related {
    @SerializedName("id")
    private Integer relatedId;
    @SerializedName("start_date")
    private String relatedStartDate;
    @SerializedName("title")
    private String relatedTitle;
    @SerializedName("type")
    private Integer relatedType;
    @SerializedName("synopsis")
    private String relatedSynopsis;
    @SerializedName("slug")
    private String relatedSlug;
    @SerializedName("relation_type")
    private Integer relationType;

    public Integer getRelatedId() {
        return relatedId;
    }
    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }
    public String getRelatedStartDate() {
        return relatedStartDate;
    }
    public void setRelatedStartDate(String relatedStartDate) {
        this.relatedStartDate = relatedStartDate;
    }
    public String getRelatedTitle() {
        return relatedTitle;
    }
    public void setRelatedTitle(String relatedTitle) {
        this.relatedTitle = relatedTitle;
    }
    public Integer getRelatedType() {
        return relatedType;
    }
    public void setRelatedType(Integer relatedType) {
        this.relatedType = relatedType;
    }
    public String getRelatedSynopsis() {
        return relatedSynopsis;
    }
    public void setRelatedSynopsis(String relatedSynopsis) {
        this.relatedSynopsis = relatedSynopsis;
    }
    public String getRelatedSlug() {
        return relatedSlug;
    }
    public void setRelatedSlug(String relatedSlug) {
        this.relatedSlug = relatedSlug;
    }
    public Integer getRelationType() {
        return relationType;
    }
    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }
}
