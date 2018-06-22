package com.gauravbhola.gcache;


import android.graphics.Bitmap;

public interface BitmapLruCache {
    boolean contains(String key);
    void put(String key, Bitmap bitmap);
    Bitmap get(String key);
}
