package com.gauravbhola.flickry.data;


import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.data.remote.PhotosResponse;
import com.gauravbhola.flickry.util.GetRecentPhotosTask;

import android.app.Application;
import androidx.lifecycle.LiveData;

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

    public LiveData<Resource<PhotosResponse>> getPhotos(String searchString) {
        GetRecentPhotosTask task = new GetRecentPhotosTask(mFlickrApiService, searchString, 0, 20);
        mExecutor.execute(task);
        return task.asLiveData();
    }

    public LiveData<Resource<PhotosResponse>> getNextPage(String searchString, int previousPage) {
        GetRecentPhotosTask task = new GetRecentPhotosTask(mFlickrApiService, searchString, previousPage + 1, 20);
        mExecutor.execute(task);
        return task.asLiveData();
    }
}
