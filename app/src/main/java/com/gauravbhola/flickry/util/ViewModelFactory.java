package com.gauravbhola.flickry.util;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.ui.home.HomeViewModel;
import com.gauravbhola.flickry.ui.splash.SplashViewModel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ImagesRepository mImagesRepository;
    private Application mApplication;

    public ViewModelFactory(ImagesRepository imagesRepository, Application application) {
        mImagesRepository = imagesRepository;
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mApplication, mImagesRepository);
        } else {
            return (T) new SplashViewModel(mApplication);
        }
    }
}