package com.moment.photogallery.api;

import com.google.gson.annotations.SerializedName;
import com.moment.photogallery.GalleryItem;

import java.util.List;

public class PhotoResponse {
    @SerializedName("photo")
    List<GalleryItem> galleryItems;

    public List<GalleryItem> getGalleryItems() {
        return galleryItems;
    }

    public void setGalleryItems(List<GalleryItem> galleryItems) {
        this.galleryItems = galleryItems;
    }
}
