package com.moment.photogallery.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moment.photogallery.FlickrFetchr;
import com.moment.photogallery.R;

public class PhotoGalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        recyclerView = view.findViewById(R.id.photo_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return view;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveData<String> flickrLiveData = new FlickrFetchr().fetchPhotos();
        flickrLiveData.observe(this, s -> Log.d(TAG, "Response received:" + s));

    }

    public static class Companion {
        public PhotoGalleryFragment newInstance() {
            return new PhotoGalleryFragment();
        }
    }
}
