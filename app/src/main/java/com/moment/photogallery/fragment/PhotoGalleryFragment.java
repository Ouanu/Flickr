package com.moment.photogallery.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moment.photogallery.FlickrFetchr;
import com.moment.photogallery.GalleryItem;
import com.moment.photogallery.PhotoGalleryViewModel;
import com.moment.photogallery.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    private PhotoGalleryViewModel photoGalleryViewModel;
    private int column = 0;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // 动态调整列数
            column = recyclerView.getMeasuredWidth() / 300;
            ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanCount(column == 0? 3 : column);
        }
    };

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        recyclerView = view.findViewById(R.id.photo_recycler_view);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return view;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoGalleryViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
//        LiveData<List<GalleryItem>> flickrLiveData = new FlickrFetchr().fetchPhotos();
//        flickrLiveData.observe(this, s -> Log.d(TAG, "Response received:" + s));

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoGalleryViewModel.galleryItemLiveData.observe(getViewLifecycleOwner(), s -> recyclerView.setAdapter(new PhotoAdapter(s)));
    }

    public static class Companion {
        public PhotoGalleryFragment newInstance() {
            return new PhotoGalleryFragment();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public PhotoHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }

        public void bindTitle(CharSequence charSequence) {
            textView.setText(charSequence);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @NonNull
        @NotNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull PhotoGalleryFragment.PhotoHolder holder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            holder.bindTitle(galleryItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
}
