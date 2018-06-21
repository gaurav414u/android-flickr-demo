package com.gauravbhola.flickry.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.gauravbhola.flickry.data.remote.GetRecentResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class ApiResponseTest {

    @Test
    public void exception() {
        Exception e = new Exception("foo");
        ApiResponse apiResponse = new ApiResponse(e);
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.httpCode, is(500));
        assertThat(apiResponse.errorMessage, is("foo"));
    }

    @Test
    public void success() {
        GetRecentResponse mockResponse = new GetRecentResponse();
        mockResponse.setStat("ok");
        ApiResponse apiResponse = new ApiResponse(Response.success(mockResponse));
        assertThat(apiResponse.body.getStat(), is("ok"));
        assertThat(apiResponse.httpCode, is(200));
        assertThat(apiResponse.errorMessage, nullValue());
        assertThat(apiResponse.isSuccessful(), is(true));
    }

    @Test
    public void networkError() {
        ApiResponse apiResponse = new ApiResponse(
                Response.error(404, ResponseBody.create(MediaType.parse("application/text"),  "sdg"))
        );
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.httpCode, is(404));
        assertThat(apiResponse.errorMessage, is("Oops! We can't reach Flickr"));
    }

    @Test
    public void flickrError() {
        GetRecentResponse mockResponse = new GetRecentResponse();
        mockResponse.setStat("fail");
        mockResponse.setMessage("mymessage");

        ApiResponse apiResponse = new ApiResponse(
                Response.success(mockResponse)
        );
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.httpCode, is(200));
        assertThat(apiResponse.errorMessage, is("mymessage"));
        assertThat(apiResponse.isSuccessful(), is(false));
    }
}
