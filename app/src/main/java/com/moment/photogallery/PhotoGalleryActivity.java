package com.moment.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

    public static class Companion {
        public static Intent newIntent(Context context) {
            return new Intent(context, PhotoGalleryActivity.class);
        }
    }
}