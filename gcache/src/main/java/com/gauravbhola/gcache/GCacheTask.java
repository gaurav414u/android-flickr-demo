package com.gauravbhola.gcache;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCacheTask implements Runnable {
    GCacheRequestBuilder mRequestBuilder;
    BitmapLruCache mLruCache;
    private boolean mIsCancelled;
    private DoneCallback mDoneCallback;


    public interface DoneCallback {
        public void done(ImageView imageView);
    }

    GCacheTask(GCacheRequestBuilder requestBuilder, BitmapLruCache lruCache, DoneCallback callback) {
        mRequestBuilder = requestBuilder;
        mLruCache = lruCache;
        mDoneCallback = callback;
    }

    @WorkerThread
    public void doWork() {
        // No need to bother about done() in case the request is cancelled
        if (mIsCancelled) {
            return;
        }

        if (mLruCache.contains(mRequestBuilder.getUrl())) {
            // If present in cache, update
            showImage(mLruCache.get(mRequestBuilder.getUrl()));
            done();
            return;
        }

        if (mIsCancelled) {
            return;
        }
        // Load the bitmap from network
        Bitmap b = this.getBitmapFromURL(mRequestBuilder.getUrl());
        if (mIsCancelled) {
            return;
        }
        if (b == null) {
            // Network error? retry?
            // Skip for now
        } else {
            // Update cache and showImage
            mLruCache.put(mRequestBuilder.getUrl(), b);
            showImage(b);
        }
        done();
    }

    public void done() {
        if (mDoneCallback != null) {
            mDoneCallback.done(mRequestBuilder.getImageViewWeakReference().get());
        }
    }

    @Override
    public void run() {
        doWork();
    }

    @WorkerThread
    private void showImage(Bitmap bitmap) {
        if (mRequestBuilder.getImageViewWeakReference().get() != null) {
            GCache.sMainHandler.post(() ->
                    mRequestBuilder.getImageViewWeakReference().get()
                            .setImageBitmap(bitmap)
            );
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void cancel() {
        mIsCancelled = true;
    }

}
