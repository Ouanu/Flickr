package com.moment.photogallery.api;

import com.google.gson.annotations.SerializedName;
import com.moment.photogallery.GalleryItem;

import java.util.List;

public class PhotoResponse {
    @SerializedName("photo")
    List<GalleryItem> galleryItems;
}
