package com.gauravbhola.flickry.data.remote;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiService {
    @GET("?method=flickr.photos.getRecent&apiKey=3e7cc266ae2b0e0d78e279ce8e361736&format=json")
    public Call<GetRecentResponse> getRecentPhotos(@Query("text") String searchQuery);
}
