package com.gauravbhola.gcache;


import com.gauravbhola.gcache.customlru.LRUCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskLruCache implements BitmapLruCache, LRUCache.ItemRemoveCallback {
    public static final String CACHE_SUB_DIR = "imagecache";
    private LruCache<String, Bitmap> mLruCache = new LruCache<>(50);
    private LRUCache mDiskBoundCache = new LRUCache(500, this);
    private String mStoragePath;
    private Context mContext;


    protected DiskLruCache(Context context) {
        mContext = context;
        mStoragePath = mContext.getCacheDir().getPath() + File.separator + CACHE_SUB_DIR;
        this.buildDiskBoundCache();
    }

    private void buildDiskBoundCache() {
        File directory = new File(mStoragePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File[] files = directory.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            mDiskBoundCache.put(Integer.parseInt(files[i].getName()), 1);
        }
    }

    @Override
    public synchronized boolean contains(String key) {
        if(mLruCache.get(key) != null) {
            return true;
        }
        if (mDiskBoundCache.get(key.hashCode()) != -1) {
            return true;
        }
        return false;
    }

    @Override
    public synchronized void put(String key, Bitmap bitmap) {
        mLruCache.put(key, bitmap);
        writeToDisk(key, bitmap);
        mDiskBoundCache.put(key.hashCode(), 1);
    }

    @Override
    public synchronized Bitmap get(String key) {
        if(mLruCache.get(key) != null) {
            return mLruCache.get(key);
        }
        // PAGE FAULT -> Get it from disk
        if (mDiskBoundCache.get(key.hashCode()) != -1) {
            // Update in-memory cache
            Bitmap b = readFromDisk(key.hashCode());
            mLruCache.put(key, b);
            return b;
        }
        return null;
    }

    @Override
    public void onRemove(int item) {
        deleteOnDisk(item);
    }

    private void writeToDisk(String url, Bitmap bitmap) {
        // Add to disk
        String fileName = url.hashCode() + "";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mStoragePath + File.separator + fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap readFromDisk(int item) {
        String fileName = item + "";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mStoragePath + File.separator + fileName, options);
        return bitmap;
    }

    private void deleteOnDisk(int item) {
        String fileName = item + "";
        File file = new File(mStoragePath + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
