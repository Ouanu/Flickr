package com.moment.photogallery.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
    "&api_key=9919616d9e0c55a370de8d1d1ef1ac64"+
    "&format=json"+
    "&nojsoncallback=1"+
    "&extras=url_s")
    Call<FlickrResponse> fetchPhotos();


    @GET
    Call<ResponseBody> fetchUrlBytes(@Url String url);


}
