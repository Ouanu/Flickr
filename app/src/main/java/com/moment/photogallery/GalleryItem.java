package com.moment.photogallery;

import com.google.gson.annotations.SerializedName;

public class GalleryItem {
    private String title = "";
    private String id = "";
    @SerializedName("url_s")
    private String url = "";


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
