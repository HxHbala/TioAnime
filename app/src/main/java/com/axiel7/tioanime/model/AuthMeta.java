package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class AuthMeta {
    @SerializedName("status")
    private Integer status;
    @SerializedName("title")
    private String title;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
