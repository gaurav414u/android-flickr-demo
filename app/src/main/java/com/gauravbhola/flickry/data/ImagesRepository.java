package com.gauravbhola.flickry.data;


import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.util.GetRecentPhotosTask;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImagesRepository {
    private FlickrApiService mFlickrApiService;
    private Application mApplication;
    private Executor mExecutor;

    public ImagesRepository(FlickrApiService flickrApiService, Application application) {
        mFlickrApiService = flickrApiService;
        mApplication = application;
        mExecutor = Executors.newFixedThreadPool(2);
    }

    public LiveData<Resource<List<Photo>>> getPhotos(String searchString) {
        GetRecentPhotosTask task = new GetRecentPhotosTask(mFlickrApiService, searchString);
        mExecutor.execute(task);
        return task.asLiveData();
    }
}
