package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.List;


public class HomeViewModel extends AndroidViewModel {
    private ImagesRepository mImagesRepository;
    private FlickrApiService mFlickrApiService;
    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private LiveData<Pair<Resource<List<Photo>>, String>> mResults;
    private Handler mHandler = new Handler();

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
                    return new Pair<>(val, query);
                }
                injectUrl(val.data);
                return new Pair<>(val, query);
            });
        });
    }

    private void injectUrl(List<Photo> photos) {
        for (Photo photo : photos) {
            photo.setUrl(String.format("https://farm%d.staticflickr.com/%s/%s_%s.jpg",
                    photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret()
            ));
        }
    }

    void searchTextChanged(final String text) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> this.fetchPhotos(text), 1000);
    }

    void fetchPhotos(String query) {
        mHandler.removeCallbacksAndMessages(null);
        mQuery.setValue(query);
    }

    void refresh() {
        fetchPhotos(mQuery.getValue());
    }

    LiveData<Pair<Resource<List<Photo>>, String>> getResults() {
        return mResults;
    }
}
