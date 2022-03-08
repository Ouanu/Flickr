package com.moment.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moment.photogallery.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        boolean isFragmentContainerEmpty = savedInstanceState == null;
        if (isFragmentContainerEmpty) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new PhotoGalleryFragment.Companion().newInstance())
                    .commit();
        }
    }
}