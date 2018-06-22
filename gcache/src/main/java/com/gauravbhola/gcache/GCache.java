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
 * Image caching class
 */
public class GCache {
    private static Executor sExecutor = Executors.newFixedThreadPool(4);
    private static BitmapLruCache sLruCache = new MemoryBitmapCache();
    private static Map<ImageView, GCacheTask> sImageViewTasks = new HashMap<>();
    static Handler sMainHandler = new Handler();

    public static GCacheRequestBuilder with(Context context) {
        return new GCacheRequestBuilder();
    }

    static void load(GCacheRequestBuilder requestBuilder) {
        // If bitmap is present in cache
        if (sLruCache.get(requestBuilder.getUrl()) != null) {
            showImage(requestBuilder,sLruCache.get(requestBuilder.getUrl()));
            return;
        }

        // If there is an existing task for this image view, cancel that task
        if (sImageViewTasks.containsKey(requestBuilder.getImageViewWeakReference().get())) {
            sImageViewTasks.get(requestBuilder.getImageViewWeakReference().get()).cancel();
            sImageViewTasks.remove(requestBuilder.getImageViewWeakReference().get());
        }

        GCacheTask task = new GCacheTask(requestBuilder, sLruCache, (imageView) -> {
            if (imageView != null && sImageViewTasks.containsKey(imageView)) {
                sImageViewTasks.remove(imageView);
            }
        });
        sImageViewTasks.put(requestBuilder.getImageViewWeakReference().get(), task);
        sExecutor.execute(task);
    }

    @UiThread
    private static void showImage(GCacheRequestBuilder requestBuilder, Bitmap bitmap) {
        if (requestBuilder.getImageViewWeakReference().get() != null) {
            requestBuilder.getImageViewWeakReference().get().setImageBitmap(bitmap);
        }
    }
}
