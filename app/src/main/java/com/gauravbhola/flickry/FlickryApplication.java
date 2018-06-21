package com.gauravbhola.flickry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.util.ViewModelFactory;

import android.app.Application;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FlickryApplication extends Application {
    ImagesRepository mImagesRepository;
    FlickrApiService mFlickrApiService;
    ViewModelFactory mViewModelFactory;


    @Override
    public void onCreate() {
        super.onCreate();
        // Create global dependencies
        mFlickrApiService = provideFlickrApiService(new GsonBuilder().create());
        mImagesRepository = provideImagesRepository(mFlickrApiService);
        mViewModelFactory = provideViewModelFactory(mImagesRepository);
    }

    private FlickrApiService provideFlickrApiService(Gson gson) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClientBuilder.build())
                .build();
        return retrofit.create(FlickrApiService.class);
    }

    private ImagesRepository provideImagesRepository(FlickrApiService flickrApiService) {
        return new ImagesRepository(flickrApiService, this);
    }

    private ViewModelFactory provideViewModelFactory(ImagesRepository imagesRepository) {
        return new ViewModelFactory(imagesRepository, this);
    }


    public ImagesRepository getImagesRepository() {
        return mImagesRepository;
    }

    public FlickrApiService getFlickrApiService() {
        return mFlickrApiService;
    }

    public ViewModelFactory getViewModelFactory() {
        return mViewModelFactory;
    }
}
