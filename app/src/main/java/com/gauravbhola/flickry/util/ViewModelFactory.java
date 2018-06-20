package com.gauravbhola.flickry.util;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.ui.home.HomeViewModel;
import com.gauravbhola.flickry.ui.splash.SplashViewModel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ImagesRepository mImagesRepository;
    private FlickrApiService mFlickrApiService;
    private Application mApplication;

    public ViewModelFactory(ImagesRepository imagesRepository, FlickrApiService flickrApiService, Application application) {
        mImagesRepository = imagesRepository;
        mFlickrApiService = flickrApiService;
        mApplication = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mApplication, mImagesRepository, mFlickrApiService);
        } else {
            return (T) new SplashViewModel(mApplication);
        }
    }
}