package com.moment.photogallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {
    public LiveData<List<GalleryItem>> galleryItemLiveData;

    {
        galleryItemLiveData = new FlickrFetchr().fetchPhotos();
    }

}
