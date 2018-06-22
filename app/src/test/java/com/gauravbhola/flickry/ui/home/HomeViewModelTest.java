package com.gauravbhola.flickry.ui.home;


import com.gauravbhola.flickry.data.ImagesRepository;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.GetRecentResponse;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import static org.hamcrest.MatcherAssert.*;

import static org.mockito.Mockito.*;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class HomeViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ImagesRepository mImagesRepository;
    private Application mApplication;
    private HomeViewModel mHomeViewModel;

    @Before
    public void setup() {
        mImagesRepository = mock(ImagesRepository.class);
        mApplication = new Application();
        mHomeViewModel = new HomeViewModel(mApplication, mImagesRepository);
        mHomeViewModel.mHandler = mock(Handler.class);
        when(mHomeViewModel.mHandler.postDelayed(any(Runnable.class), anyLong())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable run = invocation.getArgument(0);
                run.run();
                return null;
            }
        });
    }

    @Test
    public void notNull() {
        assertThat(mHomeViewModel.getAllResults(), Matchers.notNullValue());
        assertThat(mHomeViewModel.getResultsState(), Matchers.notNullValue());
        assertThat(mHomeViewModel.getLoadMoreState(), Matchers.notNullValue());
    }

    @Test
    public void injectUrl() {
        List<Photo> photoList = new ArrayList<>();
        Photo photo = new Photo();
        photo.setFarm(123);
        photo.setServer("server");
        photo.setId("id");
        photo.setSecret("secret");
        photoList.add(photo);
        PhotosResponse response = new PhotosResponse();
        response.setPhoto(photoList);
        mHomeViewModel.injectUrl(response);
        assertThat(response.getPhoto().get(0).getUrl(),
                Matchers.is("https://farm123.staticflickr.com/server/id_secret.jpg")
        );
    }

    @Test
    public void searchTextChanged() {
        LiveData<Resource<PhotosResponse>> resourceLiveData = new MutableLiveData<>();
        when(mImagesRepository.getPhotos(anyString()))
                .thenReturn(resourceLiveData);

        mHomeViewModel.getResultsState().observeForever(mock(Observer.class));
        mHomeViewModel.getLoadMoreState().observeForever(mock(Observer.class));
        mHomeViewModel.getAllResults().observeForever(mock(Observer.class));

        mHomeViewModel.searchTextChanged("hello");
        verify(mImagesRepository).getPhotos("hello");
    }


    @Test
    public void fetchPhotosFromRepository() {
        LiveData<Resource<PhotosResponse>> resourceLiveData = new MutableLiveData<>();
        when(mImagesRepository.getPhotos(anyString()))
                .thenReturn(resourceLiveData);

        mHomeViewModel.getResultsState().observeForever(mock(Observer.class));
        mHomeViewModel.getLoadMoreState().observeForever(mock(Observer.class));
        mHomeViewModel.getAllResults().observeForever(mock(Observer.class));

        mHomeViewModel.fetchPhotos("asd");
        verify(mImagesRepository).getPhotos("asd");
    }

    private PhotosResponse getMockPhotosRepsonse() {
         List<Photo> photoList = new ArrayList<>();
        Photo photo = new Photo();
        photo.setFarm(123);
        photo.setServer("server");
        photo.setId("id");
        photo.setSecret("secret");
        photoList.add(photo);
        PhotosResponse response = new PhotosResponse();
        response.setPhoto(photoList);
        return response;
    }

    @Test
    public void refresh() {
        LiveData<Resource<PhotosResponse>> resourceLiveData = new MutableLiveData<>();
        when(mImagesRepository.getPhotos(anyString()))
                .thenReturn(resourceLiveData);

        mHomeViewModel.getResultsState().observeForever(mock(Observer.class));
        mHomeViewModel.getLoadMoreState().observeForever(mock(Observer.class));
        mHomeViewModel.getAllResults().observeForever(mock(Observer.class));

        mHomeViewModel.fetchPhotos("123");
        mHomeViewModel.refresh();
        verify(mImagesRepository, times(2)).getPhotos("123");
    }

    @Test
    public void loadingPhotos() {
        MutableLiveData<Resource<PhotosResponse>> resourceLiveData = new MutableLiveData<>();
        resourceLiveData.setValue(Resource.loading(null));
        when(mImagesRepository.getPhotos(anyString()))
                .thenReturn(resourceLiveData);

        Observer resultsObserver = mock(Observer.class);
        Observer resultStateObserver = mock(Observer.class);

        mHomeViewModel.getResultsState().observeForever(resultStateObserver);
        mHomeViewModel.getLoadMoreState().observeForever(mock(Observer.class));
        mHomeViewModel.getAllResults().observeForever(resultsObserver);

        mHomeViewModel.fetchPhotos("asd");
        verify(mImagesRepository).getPhotos("asd");

        Pair expectedState = new Pair(Resource.loading(null), "asd");

        verify(resultStateObserver).onChanged(expectedState);
        verify(resultsObserver).onChanged(null);

        verifyNoMoreInteractions(resultsObserver);
        verifyNoMoreInteractions(mImagesRepository);
    }

    @Test
    public void photosSuccess() {
        MutableLiveData<Resource<PhotosResponse>> resourceLiveData = new MutableLiveData<>();

        PhotosResponse r = getMockPhotosRepsonse();
        resourceLiveData.setValue(Resource.success(r));

        when(mImagesRepository.getPhotos(anyString()))
                .thenReturn(resourceLiveData);

        Observer resultsObserver = mock(Observer.class);
        Observer resultStateObserver = mock(Observer.class);
        mHomeViewModel.getResultsState().observeForever(resultStateObserver);
        mHomeViewModel.getLoadMoreState().observeForever(mock(Observer.class));
        mHomeViewModel.getAllResults().observeForever(resultsObserver);

        mHomeViewModel.fetchPhotos("asd");
        verify(mImagesRepository).getPhotos("asd");

        Pair expectedState = new Pair(Resource.success(r), "asd");
        verify(resultStateObserver).onChanged(expectedState);

        Pair expectedResults = new Pair(r.getPhoto(), "asd");
        verify(resultsObserver).onChanged(expectedResults);

        verifyNoMoreInteractions(resultsObserver);
        verifyNoMoreInteractions(mImagesRepository);
    }
}
