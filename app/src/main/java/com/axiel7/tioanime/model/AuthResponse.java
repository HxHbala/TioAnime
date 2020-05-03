package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("data")
    private String data;
    @SerializedName("meta")
    private AuthMeta meta;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public AuthMeta getMeta() {
        return meta;
    }

    public void setMeta(AuthMeta meta) {
        this.meta = meta;
    }
}
