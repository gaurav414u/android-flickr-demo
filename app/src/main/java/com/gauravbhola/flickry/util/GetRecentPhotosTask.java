package com.gauravbhola.flickry.util;


import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.data.remote.GetRecentResponse;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GetRecentPhotosTask implements Runnable {
    private FlickrApiService mFlickrApiService;
    private String mQuery;
    private MutableLiveData<Resource<List<Photo>>> mLiveData;

    public GetRecentPhotosTask(FlickrApiService apiService, String query) {
        mFlickrApiService = apiService;
        mQuery = query;
        mLiveData = new MutableLiveData<>();
        mLiveData.setValue(Resource.loading(null));
    }

    @Override
    public void run() {
        Call call = mFlickrApiService.getRecentPhotos(mQuery);
        try {
            Response<GetRecentResponse> response = call.execute();
            ApiResponse apiResponse = new ApiResponse(response);
            if (apiResponse.isSuccessful()) {
                mLiveData.postValue(Resource.success(apiResponse.body.getPhotos().getPhoto()));
            } else {
                mLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
            }
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(e);
            mLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
        }
    }

    public LiveData<Resource<List<Photo>>> asLiveData() {
        return mLiveData;
    }
}
