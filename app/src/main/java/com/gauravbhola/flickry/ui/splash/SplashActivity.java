package com.gauravbhola.flickry.ui.splash;

import com.gauravbhola.flickry.FlickryApplication;
import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.ui.home.HomeActivity;
import com.gauravbhola.flickry.util.ViewModelFactory;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {
    private SplashViewModel mSplashViewModel;
    private LinearLayout mAnimContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewModelFactory modelFactory = ((FlickryApplication)getApplication()).getViewModelFactory();
        mSplashViewModel = ViewModelProviders.of(this, modelFactory).get(SplashViewModel.class);
        mSplashViewModel.splashDisplayed();
        getViews();
        subscribeToViewModel();
        mAnimContainer.setLayoutAnimation(getListAnimationController(1000));
    }

    private void getViews() {
        mAnimContainer = findViewById(R.id.container_anim_items);
    }

    private void subscribeToViewModel() {
        mSplashViewModel.getNavigationCommand().observe(this, (v) -> navigateToHome());
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public LayoutAnimationController getListAnimationController(long animationDuration) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(animationDuration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.ABSOLUTE, 300.0f,Animation.ABSOLUTE, 0.0f
        );
        animation.setDuration(animationDuration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addAnimation(animation);

        return new LayoutAnimationController(set, 0.2f);
    }
}
