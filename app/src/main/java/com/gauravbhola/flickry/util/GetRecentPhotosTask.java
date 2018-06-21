package com.gauravbhola.flickry.util;


import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.data.remote.GetRecentResponse;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;


import retrofit2.Call;
import retrofit2.Response;

public class GetRecentPhotosTask implements Runnable {
    private FlickrApiService mFlickrApiService;
    private String mQuery;
    private MutableLiveData<Resource<PhotosResponse>> mLiveData;
    private int mPage;
    private int mPerPage;

    public GetRecentPhotosTask(FlickrApiService apiService, String query, int page, int perPage) {
        mFlickrApiService = apiService;
        mQuery = query;
        mLiveData = new MutableLiveData<>();
        mLiveData.setValue(Resource.loading(null));
        mPage = page;
        mPerPage = perPage;
    }

    @Override
    public void run() {
        Call call = null;
        if (mQuery.equals("")) {
            call = mFlickrApiService.getRecentPhotos(mPage, mPerPage);
        } else {
            call = mFlickrApiService.searchPhotos(mQuery, mPage, mPerPage);
        }
        try {
            Response<GetRecentResponse> response = call.execute();
            ApiResponse apiResponse = new ApiResponse(response);
            if (apiResponse.isSuccessful()) {
                mLiveData.postValue(Resource.success(apiResponse.body.getPhotos()));
            } else {
                mLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
            }
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(e);
            mLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
        }
    }

    public LiveData<Resource<PhotosResponse>> asLiveData() {
        return mLiveData;
    }
}
