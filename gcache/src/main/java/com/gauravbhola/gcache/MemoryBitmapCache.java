package com.gauravbhola.gcache;


import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryBitmapCache implements BitmapLruCache{
    LruCache<String, Bitmap> mLruCache = new LruCache<>(50);
    Object lock = new Object();

    @Override
    public boolean contains(String key) {
        synchronized (lock) {
            return mLruCache.get(key) != null;
        }
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (lock) {
            mLruCache.put(key, bitmap);
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (lock) {
            return mLruCache.get(key);
        }
    }
}
