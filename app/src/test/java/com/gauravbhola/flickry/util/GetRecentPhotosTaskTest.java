package com.gauravbhola.flickry.util;


import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.FlickrApiService;
import com.gauravbhola.flickry.data.remote.GetRecentResponse;
import com.gauravbhola.flickry.data.remote.PhotosResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.mock.Calls;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class GetRecentPhotosTaskTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    GetRecentPhotosTask mTask;
    FlickrApiService mFlickrApiService;

    @Before
    public void setup() {
        mFlickrApiService = mock(FlickrApiService.class);
        mTask = new GetRecentPhotosTask(mFlickrApiService, "", 0, 20);
    }

    @Test
    public void callsSearchPhotosWhenQueryIsPresent() {
        mTask.mQuery = "hello";
        mTask.run();

        verify(mFlickrApiService, times(1)).searchPhotos("hello", 0, 20);
        verify(mFlickrApiService, times(0)).getRecentPhotos(anyInt(), anyInt());
    }


    @Test
    public void success() throws IOException{
        mTask.mQuery = "";
        PhotosResponse photosResponse = new PhotosResponse();
        GetRecentResponse mockResponse = new GetRecentResponse();
        mockResponse.setStat("ok");
        mockResponse.setPhotos(photosResponse);
        when(mFlickrApiService.getRecentPhotos(0, 20))
                .thenReturn(Calls.response(Response.success(mockResponse)));

        Observer<Resource<PhotosResponse>> observer = Mockito.mock(Observer.class);
        mTask.asLiveData().observeForever(observer);

        mTask.run();
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.success(photosResponse));
    }

    @Test
    public void networkError() {
        mTask.mQuery = "";
        PhotosResponse photosResponse = new PhotosResponse();
        GetRecentResponse mockResponse = new GetRecentResponse();
        mockResponse.setStat("ok");
        mockResponse.setPhotos(photosResponse);
        when(mFlickrApiService.getRecentPhotos(0, 20))
                .thenReturn(Calls.failure(new IOException()));

        Observer<Resource<PhotosResponse>> observer = Mockito.mock(Observer.class);
        mTask.asLiveData().observeForever(observer);

        mTask.run();
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.error("Oops! We can't reach Flickr", null));

    }

    @Test
    public void flickrError() {
        mTask.mQuery = "";
        PhotosResponse photosResponse = new PhotosResponse();
        GetRecentResponse mockResponse = new GetRecentResponse();
        mockResponse.setStat("fail");
        mockResponse.setMessage("errorMessageFromFlickr");
        mockResponse.setPhotos(photosResponse);

        when(mFlickrApiService.getRecentPhotos(0, 20))
                .thenReturn(Calls.response(Response.success(mockResponse)));

        Observer<Resource<PhotosResponse>> observer = Mockito.mock(Observer.class);
        mTask.asLiveData().observeForever(observer);

        mTask.run();
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.error("errorMessageFromFlickr", null));
    }
}
