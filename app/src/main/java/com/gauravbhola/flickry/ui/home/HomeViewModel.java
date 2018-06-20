package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.util.AbsentLiveData;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.List;
import static com.gauravbhola.flickry.data.model.Resource.Status.*;


public class HomeViewModel extends AndroidViewModel {
    private ImagesRepository mImagesRepository;
    private FlickrApiService mFlickrApiService;
    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private LiveData<Resource<List<Photo>>> mResults;

    public HomeViewModel(@NonNull Application application, ImagesRepository imagesRepository, FlickrApiService flickrApiService) {
        super(application);
        mImagesRepository = imagesRepository;
        mFlickrApiService = flickrApiService;

        mResults = Transformations.switchMap(mQuery, (query) -> {
//            if (query.equals("") || query.trim().equals("")) {
//                return AbsentLiveData.create();
//            }
            return Transformations.map(mImagesRepository.getPhotos(query), (val) -> {
                if (val.data == null) {
                    return val;
                }
                injectUrl(val.data);
                return val;
            });
        });
    }

    public void fetchPhotos(String query) {
        mQuery.setValue(query);
    }

    public LiveData<Resource<List<Photo>>> getResults() {
        return mResults;
    }

    private void injectUrl(List<Photo> photos) {
        for (Photo photo : photos) {
            photo.setUrl(String.format("https://farm%d.staticflickr.com/%s/%s_%s.jpg",
                    photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret()
            ));
        }
    }
}
