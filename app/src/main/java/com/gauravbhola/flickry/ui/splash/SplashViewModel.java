package com.gauravbhola.flickry.ui.splash;


import com.gauravbhola.flickry.util.SingleLiveEvent;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.Handler;
import android.support.annotation.NonNull;

public class SplashViewModel extends AndroidViewModel {
    private Handler mHandler = new Handler();
    private SingleLiveEvent<Void> mNavigationCommand = new SingleLiveEvent<>();

    public SplashViewModel(@NonNull Application application) {
        super(application);
    }

    public void splashDisplayed() {
        mHandler.postDelayed(() -> mNavigationCommand.call(), 1500);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHandler.removeCallbacksAndMessages(null);
    }

    public SingleLiveEvent<Void> getNavigationCommand() {
        return mNavigationCommand;
    }
}

