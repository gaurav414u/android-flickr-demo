package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.R;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


public class HomeActivity extends AppCompatActivity {
    private HomeViewModel mHomeViewModel;
    private RecyclerView mPicsRecyclerView;
    private PhotosRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        getViews();
        setupPicsRecyclerView();
        subscribeToViewModel();
    }

    private void getViews() {
        mPicsRecyclerView = findViewById(R.id.recycler_view_pics);
    }

    private void setupPicsRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 3);
        mPicsRecyclerView.setLayoutManager(manager);
        mAdapter = new PhotosRecyclerAdapter();
        mPicsRecyclerView.setAdapter(mAdapter);
    }


    private void subscribeToViewModel() {

    }
}
