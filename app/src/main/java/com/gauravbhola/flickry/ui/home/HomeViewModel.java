package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.util.List;


public class HomeViewModel extends AndroidViewModel {
    private ImagesRepository mImagesRepository;
    protected MutableLiveData<String> mQuery = new MutableLiveData<>();
    private MutableLiveData<String> mLoadMoreWithQuery = new MutableLiveData<>();
    private LiveData<Pair<Resource<PhotosResponse>, String>> mResultsState;
    private MediatorLiveData<Pair<List<Photo>, String>> mAllResults;
    protected Handler mHandler = new Handler();
    private int mCurrentPage;
    private LiveData<Pair<Resource<PhotosResponse>, String>> mLoadMoreState;

    public HomeViewModel(@NonNull Application application, ImagesRepository imagesRepository) {
        super(application);
        mImagesRepository = imagesRepository;

        mAllResults = new MediatorLiveData<>();
        mResultsState = Transformations.switchMap(mQuery, (query) -> {
            
            mAllResults.removeSource(mResultsState);
            mAllResults.addSource(mResultsState, val -> {
                if (val.first.status == Resource.Status.SUCCESS
                        && mResultsState.getValue().second.equals(query)) {
                    // If the resultsState query is equal to the given query, only then set the value in results
                    mAllResults.setValue(new Pair(val.first.data.getPhoto(), query));
                    // Remove this source from AllResults
                    mAllResults.removeSource(mResultsState);
                } else {
                    mAllResults.setValue(null);
                }
            });

            return Transformations.map(mImagesRepository.getPhotos(query), (val) -> {
                if (val.data == null) {
                    return new Pair<>(val, query);
                }
                mCurrentPage = 1;
                injectUrl(val.data);
                return new Pair<>(val, query);
            });
        });


        mLoadMoreState = Transformations.switchMap(mLoadMoreWithQuery, (query) ->
                Transformations.map(mImagesRepository.getNextPage(query, mCurrentPage), (val) -> {
            if (val.data == null) {
                return new Pair<>(val, query);
            }
            injectUrl(val.data);
            return new Pair<>(val, query);
        }));

    }

    void injectUrl(PhotosResponse response) {
        List<Photo> photos = response.getPhoto();
        for (Photo photo : photos) {
            photo.setUrl(String.format("https://farm%d.staticflickr.com/%s/%s_%s.jpg",
                    photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret()
            ));
        }
    }

    void searchTextChanged(@NonNull final String text) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> this.fetchPhotos(text), 1000);
    }

    void fetchPhotos(String query) {
        // Last known query is equal to this query
        // and result state is success, then don't do anything
        if (query.equals(mQuery.getValue())
                && mResultsState.getValue() != null
                && mResultsState.getValue().first != null
                && query.equals(mResultsState.getValue().second)
                && mResultsState.getValue().first.status == Resource.Status.SUCCESS) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        mQuery.setValue(query);
    }

    void loadNextPage() {
        if(mAllResults.getValue() == null) {
            // No existing data
            return;
        }
        if (mLoadMoreState.getValue() != null && mLoadMoreState.getValue().first.status == Resource.Status.LOADING
                && mLoadMoreWithQuery.getValue().equals(mQuery.getValue())) {
            // If already loading more data for the same query, return
            return;
        }
        mLoadMoreWithQuery.setValue(mQuery.getValue());

        mAllResults.removeSource(mLoadMoreState);
        mAllResults.addSource(mLoadMoreState, val -> {
            if(val.first.status == Resource.Status.SUCCESS
                    && mAllResults.getValue().second.equals(val.second)) {
                mCurrentPage = val.first.data.getPage();

                List<Photo> existingResults = mAllResults.getValue().first;
                existingResults.addAll(val.first.data.getPhoto());
                mAllResults.setValue(new Pair(existingResults, val.second));
            }
            if(val.first.status == Resource.Status.SUCCESS) {
                mAllResults.removeSource(mLoadMoreState);
            }
            if(val.first.status == Resource.Status.ERROR) {
                mAllResults.removeSource(mLoadMoreState);
            }
        });
    }

    void refresh() {
        fetchPhotos(mQuery.getValue());
    }

    @NonNull
    public LiveData<Pair<Resource<PhotosResponse>, String>> getResultsState() {
        return mResultsState;
    }


    @NonNull
    public MediatorLiveData<Pair<List<Photo>, String>> getAllResults() {
        return mAllResults;
    }

    @NonNull
    public LiveData<Pair<Resource<PhotosResponse>, String>> getLoadMoreState() {
        return mLoadMoreState;
    }
}
