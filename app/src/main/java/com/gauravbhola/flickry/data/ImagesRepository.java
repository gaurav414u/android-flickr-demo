package com.gauravbhola.flickry.data;


import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import android.arch.lifecycle.LiveData;

public class ImagesRepository {
    public LiveData<Resource<PhotosResponse>> getPhotos(String searchString) {
        return null;
    }

}
