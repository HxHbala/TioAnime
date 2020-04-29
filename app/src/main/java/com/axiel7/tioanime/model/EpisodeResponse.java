package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class EpisodeResponse {
    @SerializedName("data")
    private Episode episodeData;

    public Episode getEpisodeData() {
        return episodeData;
    }

    public void setEpisodeData(Episode episodeData) {
        this.episodeData = episodeData;
    }
}
