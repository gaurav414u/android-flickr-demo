package com.gauravbhola.flickry.data;


import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import android.app.Application;
import android.arch.lifecycle.LiveData;

public class ImagesRepository {
    private FlickrApiService mFlickrApiService;
    private Application mApplication;

    public ImagesRepository(FlickrApiService flickrApiService, Application application) {
        mFlickrApiService = flickrApiService;
        mApplication = application;
    }

    public LiveData<Resource<PhotosResponse>> getPhotos(String searchString) {
        return null;
    }

}
