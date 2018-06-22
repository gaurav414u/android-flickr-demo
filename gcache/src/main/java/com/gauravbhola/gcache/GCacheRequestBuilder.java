package com.gauravbhola.gcache;


import android.support.annotation.UiThread;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class GCacheRequestBuilder {
    private String mUrl;
    private WeakReference<ImageView> mImageViewWeakReference;

    protected GCacheRequestBuilder() {
    }

    @UiThread
    public GCacheRequestBuilder load(String url) {
        this.mUrl = url;
        return this;
    }

    @UiThread
    public void into(ImageView imageView) {
        mImageViewWeakReference = new WeakReference<ImageView>(imageView);
        // Clear existing image
        imageView.setImageResource(0);
        GCache.load(this);
    }

    public String getUrl() {
        return mUrl;
    }

    public WeakReference<ImageView> getImageViewWeakReference() {
        return mImageViewWeakReference;
    }
}