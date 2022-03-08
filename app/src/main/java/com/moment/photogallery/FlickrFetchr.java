package com.moment.photogallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moment.photogallery.api.FlickrApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static FlickrApi flickrApi;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/")
                .addConverterFactory(ScalarsConverterFactory.create()) //数据类型转化器，将Retrofit把网络响应数据反序列化为String类型
                .build();

        flickrApi = retrofit.create(FlickrApi.class);
    }

    public LiveData<String> fetchPhotos() {
        MutableLiveData<String> responseLiveData = new MutableLiveData<>();

        // 返回代表网络请求的Call<String>对象
        //然后由你决定何时执行该Call对象
        Call<String> flickrRequest = flickrApi.fetchPhotos();
        //在后台线程上执行网络请求，一切都由Retrofit管理和调度
        flickrRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: received " + response.body());
                responseLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Failed to fetch photos", t);
            }
        });
        return responseLiveData;
    }
}
