package com.moment.photogallery.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moment.photogallery.GalleryItem;
import com.moment.photogallery.PhotoGalleryViewModel;
import com.moment.photogallery.R;
import com.moment.photogallery.ThumbnailDownloader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    private PhotoGalleryViewModel photoGalleryViewModel;
    private int column = 0;
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private PhotoAdapter adapter;
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
        getViewLifecycleOwner().getLifecycle().addObserver(thumbnailDownloader.lifecycleObserver);
        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoGalleryViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
        adapter = new PhotoAdapter(photoGalleryViewModel.galleryItemLiveData.getValue());
//        LiveData<List<GalleryItem>> flickrLiveData = new FlickrFetchr().fetchPhotos();
//        flickrLiveData.observe(this, s -> Log.d(TAG, "Response received:" + s));
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Handler responseHandler;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            responseHandler = Handler.createAsync(Looper.getMainLooper());
        } else {
            responseHandler = new Handler(Looper.getMainLooper());
        }
        thumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownload", responseHandler);
        getLifecycle().addObserver(thumbnailDownloader.fragmentLifecycleObserver);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                photoGalleryViewModel.fetchPhotos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(v -> searchView.setQuery(photoGalleryViewModel.getSearchTerm(), false));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_clear) {
            photoGalleryViewModel.fetchPhotos("");
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public class PhotoHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PhotoHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }

        public void bindTitle(Drawable drawable) {
            imageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItems;
        private PhotoHolder mHolder;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @NonNull
        @NotNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull PhotoGalleryFragment.PhotoHolder holder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
//            Drawable placeholder = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background);
//            holder.bindTitle(placeholder);
            mHolder = holder;
            thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }

        public PhotoHolder getmHolder() {
            return mHolder;
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getViewLifecycleOwner().getLifecycle().removeObserver(thumbnailDownloader.lifecycleObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(thumbnailDownloader.fragmentLifecycleObserver);
    }
}
