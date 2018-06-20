package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.remote.FlickrApiService;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;


public class HomeViewModel extends AndroidViewModel {
    private ImagesRepository mImagesRepository;
    private FlickrApiService mFlickrApiService;

    public HomeViewModel(@NonNull Application application, ImagesRepository imagesRepository, FlickrApiService flickrApiService) {
        super(application);
        mImagesRepository = imagesRepository;
        mFlickrApiService = flickrApiService;
    }
}
