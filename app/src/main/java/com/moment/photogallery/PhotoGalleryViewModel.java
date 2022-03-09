package com.moment.photogallery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PhotoGalleryViewModel extends AndroidViewModel {
    public LiveData<List<GalleryItem>> galleryItemLiveData;
    private FlickrFetchr flickrFetchr = new FlickrFetchr();
    private MutableLiveData<String> mutableSearchTerm = new MutableLiveData<>();
    private Application app;
    private QueryPreferences instance = QueryPreferences.getINSTANCE();
    private String searchTerm;



    public PhotoGalleryViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = application;

        init();
    }

    private void init() {
        mutableSearchTerm.setValue(instance.getStoredQuery(app));
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm, input -> {
            searchTerm = input;
            if (!input.isEmpty()) {
                return flickrFetchr.searchPhotos(input);
            }
            return flickrFetchr.fetchPhotos();
        });
    }

    public void fetchPhotos(String query) {
        instance.setStoredQuery(app, query);
        mutableSearchTerm.setValue(query);
    }

    public String getSearchTerm() {
        return searchTerm == null ? "" : searchTerm;
    }
}
