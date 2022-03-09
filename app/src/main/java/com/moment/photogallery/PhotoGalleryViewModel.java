package com.moment.photogallery;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {
    public LiveData<List<GalleryItem>> galleryItemLiveData;
    private FlickrFetchr flickrFetchr = new FlickrFetchr();
    private MutableLiveData<String> mutableSearchTerm = new MutableLiveData<>();


    {
        mutableSearchTerm.setValue("planets");
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm, input -> flickrFetchr.searchPhotos(input));

    }

    public void fetchPhotos(String query) {
        mutableSearchTerm.setValue(query);
    }

}
