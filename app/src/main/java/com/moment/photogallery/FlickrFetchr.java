package com.moment.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moment.photogallery.api.FlickrApi;
import com.moment.photogallery.api.FlickrResponse;
import com.moment.photogallery.api.PhotoInterceptor;
import com.moment.photogallery.api.PhotoResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static FlickrApi flickrApi;

    static {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new PhotoInterceptor())
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create()) //数据类型转化器，将Retrofit把网络响应数据反序列化为String类型
                .client(client)
                .build();

        flickrApi = retrofit.create(FlickrApi.class);
    }

    public LiveData<List<GalleryItem>> fetchPhotos() {
//        return fetchPhotoMetadata(flickrApi.fetchPhotos());
        return fetchPhotoMetadata(fetchPhotosRequest());
    }

    public LiveData<List<GalleryItem>> searchPhotos(String query) {
        return fetchPhotoMetadata(searchPhotosRequest(query));
    }

    private LiveData<List<GalleryItem>> fetchPhotoMetadata(Call<FlickrResponse> flickrRequest) {
        MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();

        // 返回代表网络请求的Call<String>对象
        //然后由你决定何时执行该Call对象
//        Call<FlickrResponse> flickrRequest = flickrApi.fetchPhotos();
        //在后台线程上执行网络请求，一切都由Retrofit管理和调度
        flickrRequest.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                Log.d(TAG, "onResponse: received " + response.body());
                FlickrResponse flickrResponse = response.body();
                PhotoResponse photoResponse = flickrResponse.getPhotos();
                List<GalleryItem> galleryItems = photoResponse.getGalleryItems();
                galleryItems.forEach(galleryItem -> {
                    if (galleryItem.getUrl() == null || galleryItem.getUrl().isEmpty()) {
                        galleryItems.remove(galleryItem);
                    }
                });

                responseLiveData.setValue(galleryItems);
            }

            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch photos", t);
            }
        });
        return responseLiveData;
    }

    @WorkerThread
    Bitmap fetchPhoto(String url) throws IOException {
        Response<ResponseBody> response = flickrApi.fetchUrlBytes(url).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        Log.d(TAG, "Decoded bitmap = " +  bitmap + " from Response = " + response);
        return bitmap;
    }

    public Call<FlickrResponse> fetchPhotosRequest() {
        return flickrApi.fetchPhotos();
    }

    public Call<FlickrResponse> searchPhotosRequest(String query) {
        return flickrApi.searchPhotos(query);
    }


}
