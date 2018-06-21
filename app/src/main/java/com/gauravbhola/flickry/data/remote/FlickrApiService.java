package com.gauravbhola.flickry.data.remote;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiService {
    @GET("?method=flickr.photos.getRecent&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=?")
    public Call<GetRecentResponse> getRecentPhotos();

    @GET("?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=?")
    public Call<GetRecentResponse> searchPhotos(@Query("text") String searchQuery);
}
