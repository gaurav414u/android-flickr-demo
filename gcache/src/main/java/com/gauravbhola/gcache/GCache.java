package com.gauravbhola.gcache;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * GCache ('G'aurav :D 'Cache') Image caching class
 */
public class GCache {
    private static Executor sExecutor = Executors.newFixedThreadPool(3);
    private static Executor sLoadExecutor = Executors.newSingleThreadExecutor();
    private static Map<ImageView, GCacheTask> sImageViewTasks = new HashMap<>();
    static Handler sMainHandler = new Handler();
    private static BitmapLruCache sLruCache;

    public static GCacheRequestBuilder with(Context context) {
        if (sLruCache == null) {
            sLruCache = new DiskBackedLruCache(context);
        }
        return new GCacheRequestBuilder();
    }

    @UiThread
    static void load(GCacheRequestBuilder requestBuilder) {
        // Clear existing image
        requestBuilder.getImageViewWeakReference().get().setImageResource(0);

        // If there is an existing task for this image view, cancel that task
        if (sImageViewTasks.containsKey(requestBuilder.getImageViewWeakReference().get())) {
            sImageViewTasks.get(requestBuilder.getImageViewWeakReference().get()).cancel();
            sImageViewTasks.remove(requestBuilder.getImageViewWeakReference().get());
        }

        // Take it off the main thread, as sLruCache's methods are synchronized
        sLoadExecutor.execute(() -> {
            // If bitmap is present in cache, no need to do fancy stuff
            if (sLruCache.get(requestBuilder.getUrl()) != null) {
                showImage(requestBuilder,sLruCache.get(requestBuilder.getUrl()));
                return;
            }
            GCacheTask task = new GCacheTask(requestBuilder, sLruCache, (imageView) -> {
                sMainHandler.post(() -> {
                    // To ensure sImageViewTasks is handled by a single thread only
                    if (imageView != null && sImageViewTasks.containsKey(imageView)) {
                        sImageViewTasks.remove(imageView);
                    }
                });
            });
            sImageViewTasks.put(requestBuilder.getImageViewWeakReference().get(), task);
            sExecutor.execute(task);
        });
    }

    private static void showImage(GCacheRequestBuilder requestBuilder, Bitmap bitmap) {
        sMainHandler.post(() -> {
            if (requestBuilder.getImageViewWeakReference().get() != null) {
                requestBuilder.getImageViewWeakReference().get().setImageBitmap(bitmap);
            }
        });
    }
}
