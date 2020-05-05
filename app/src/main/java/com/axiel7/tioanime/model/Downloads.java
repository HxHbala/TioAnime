package com.axiel7.tioanime.model;

import com.google.gson.annotations.SerializedName;

public class Downloads {
    @SerializedName("id")
    private Integer downloadId;
    @SerializedName("episode_id")
    private Integer downloadEpisodeId;
    @SerializedName("title")
    private String serverName;
    @SerializedName("url")
    private String downloadUrl;

    public Integer getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Integer downloadId) {
        this.downloadId = downloadId;
    }

    public Integer getDownloadEpisodeId() {
        return downloadEpisodeId;
    }

    public void setDownloadEpisodeId(Integer downloadEpisodeId) {
        this.downloadEpisodeId = downloadEpisodeId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
