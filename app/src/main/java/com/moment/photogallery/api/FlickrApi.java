package com.moment.photogallery.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FlickrApi {
    @GET("services/rest?method=flickr.interestingness.getList")
    Call<FlickrResponse> fetchPhotos();

    @GET
    Call<ResponseBody> fetchUrlBytes(@Url String url);

    @GET("services/rest?method=flickr.photos.search")
    Call<FlickrResponse> searchPhotos(@Query("text") String query);


}
