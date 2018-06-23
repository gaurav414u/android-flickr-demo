android-flickr-demo
====================================

This is a sample app crafted for fun and learning. It uses the api from flickr which is documented here [FlickApi](https://www.flickr.com/services/api/).

# Modules
1. [Flickry](#flickry) - The Sample App
2. [GCache](#gcache) - Custom made image caching library

<a name="flickry"></a>
# Flickry

The app uses the MVVM architecture implemented using [Android Arch Components](https://developer.android.com/topic/libraries/architecture/)
* Well separation of concerns for Business Logic (in Repositories), Presentation Logic (in ViewModel) and Presentation(Activities and Xml)
* In-turn increases the testability of individual components.

**NOTE** For the app of this scale, MVVM might not be needed in general but it really good to use it in android apps because a lot of complications that arise due to configuration changes and async work is automatically taken care of by ViewModels.


Other third party libs include
* Retrofit - For making network calls
* Hamcrest - For advanced matching in unit tests
* Mockito  - For mocking

Introduction
------------

### Functionality
The app is composed of 2 main screens

#### SplashActivity
Displays the icon and name of the app and navigates to HomeActivity after 1.5 seconds. The app logic can be found in [SplashViewModel](https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/ui/splash/SplashViewModel.java)


#### HomeAcitivity
Displays the list of recent photos by default and displays the search results when user enters a filter. 

The [HomeViewModel](https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/ui/home/HomeViewModel.java) exposes 3 LiveDatas using which the state of this activity's UI is developed.
1. ResultsStateLiveData - Used to show the progress bar and error
2. AllResultsLiveData - Contains the latest results need to be shown to the user
3. LoadMoreStateLiveData - Used to show the horizontal progress bar when more items are being fetched

HomeActivity sends user actions to HomeViewModel using simple function calls like:
* `fetchPhotos("kittens")`
* `searchTextChanged("Street triple 765 rs")`
* `loadNextPage()`
* `refresh()`


### Dependency Management
Right now, no dependency injection framework like Dagger is being used and hence the small dependency graph need to be created manually which can be seen in [FlickyApplication](https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/FlickryApplication.java#L27) class

### Other important classes
#### ApiResponse [[link][apiresponse_link]]
* Used to handle the network response and convert it into a meaningful error message in case of any kind of error (Network error, Flick error, parsing Error).

* As of now this is tightly coupled with GetRecentResponse model. Eventually it should be made generic as the app scales.

#### GetRecentPhotosTask [[link][recentphotostask_link]]
This is used to perform the network task of fetching recent photos or fetching the search results for a given query.
* It spits out network resource (PhotosResponse in our case) states in the form of LiveData using [Resource][resource_link] class
* It is tightly coupled to PhotosResponse as of now which should eventually be generalised
* This class can be made an abstract class which can be a baseline for any kind of network request which needs to be backed by a LiveData.

#### Resource [[link][resource_link]]
This is a great utility which I prefer to use in apps to represent a network resource's state. This really helps to let any body know of the network resouces' state in the app including the UI.

#### ImagesRepository
Nothing fancy going on here but in the future this can be a major hub for images which supports caching results for query in a local db using Room, searching local db if network is not available etc.


### Testing
#### UI Testing
No UI tests as of now

### Local unit tests
Local unit tests can be found for `HomeViewModel`, `SplashViewModel`, `ApiResponse` and `Resource`

[apiresponse_link]: https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/util/ApiResponse.java
[recentphotostask_link]: https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/util/GetRecentPhotosTask.java
[resource_link]: https://github.com/gaurav414u/android-flickr-demo/blob/master/app/src/main/java/com/gauravbhola/flickry/data/model/Resource.java

<a name="gcache"></a>
# GCache 
GCache is a custom made image caching library which uses Disk and Memory caching together. Its built expicitly for Flickry and is fairly simple to use.

Use
-----
As simple as that
```java
GCache.using(context).load(url).into(imageView);
```

Functionality
---

* This library maintains 50 recent bitmaps in memory and upto 500 bitmaps on disk.
* It is bounded by number of images but eventually it should be bounded by a max memory limit for disk and in-memory.

### Major components
#### GCache [[link](https://github.com/gaurav414u/android-flickr-demo/blob/master/gcache/src/main/java/com/gauravbhola/gcache/GCache.java)]
Its the main class of the library. 
* Initialises the [BitmapLruCache][bitmap_lru_cache] which can either be a [MemoryOnlyLruCache][memory_lru_cache] or [DiskBackedLruCache][disk_backed_lru_cache]. Right now its `DiskBackedLruCache` always.
* Also initialises the required executors to execute image loading tasks and process loading commands.
* The load command:
	```java
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
	```

#### GCacheRequestBuilder [[link](https://github.com/gaurav414u/android-flickr-demo/blob/master/gcache/src/main/java/com/gauravbhola/gcache/GCacheRequestBuilder.java)]
* Represents a loading request made by client
* Contains a weak reference to the ImageView provided by `into()` call
* Calls `GCache.load(this)`


#### GCacheTask
Place where the actual network request happens

#### DiskBackedLruCache [[link][disk_backed_lru_cache]]
* Maintains a sub LRUCache for in memory caching of upto 50 recent bitmaps
* Maintains another LRUCache(`mDiskBoundCache`) of file names of upto 500 bitmaps

	**NOTE** The file names LRUCache's(`mDiskBoundCache`) item removal is listened by DiskBackedLruCache to delete the coressponding file on disk
* Initialises `mDiskBoundCache` by reading the files in the directory
	

* **Put** operation updates memory cache, writes to disk and then updates the `mDiskBoundCache`
	```java
	@Override
    public synchronized void put(String key, Bitmap bitmap) {
        mLruCache.put(key, bitmap);
        writeToDisk(key, bitmap);
        mDiskBoundCache.put(key.hashCode(), 1);
    }
	```

* **get** operation first gets from memory cache, if not present then gets it from disk and updates in memory cache before returning

	```java
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
	```




[bitmap_lru_cache]: https://github.com/gaurav414u/android-flickr-demo/blob/master/gcache/src/main/java/com/gauravbhola/gcache/BitmapLruCache.java
[memory_lru_cache]: https://github.com/gaurav414u/android-flickr-demo/blob/master/gcache/src/main/java/com/gauravbhola/gcache/MemoryOnlyLruCache.java
[disk_backed_lru_cache]: https://github.com/gaurav414u/android-flickr-demo/blob/master/gcache/src/main/java/com/gauravbhola/gcache/DiskBackedLruCache.java
