package com.gauravbhola.flickry.ui.splash;

import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.ui.home.HomeActivity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    SplashViewModel mSplashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        mSplashViewModel.splashDisplayed();
        subscribeToViewModel();
    }

    private void subscribeToViewModel() {
        mSplashViewModel.getNavigationCommand().observe(this, (v) -> navigateToHome());
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
