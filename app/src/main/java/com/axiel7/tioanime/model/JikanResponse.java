package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class JikanResponse {
    @SerializedName("score")
    private float scoreMal;

    public float getScoreMal() {
        return scoreMal;
    }

    public void setScoreMal(float scoreMal) {
        this.scoreMal = scoreMal;
    }
}
