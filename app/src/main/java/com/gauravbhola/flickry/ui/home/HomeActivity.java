package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.FlickryApplication;
import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.util.ViewModelFactory;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import java.util.List;

import static com.gauravbhola.flickry.data.model.Resource.Status.*;


public class HomeActivity extends AppCompatActivity {
    private HomeViewModel mHomeViewModel;
    private RecyclerView mPicsRecyclerView;
    private PhotosRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewModelFactory modelFactory = ((FlickryApplication)getApplication()).getViewModelFactory();
        mHomeViewModel = ViewModelProviders.of(this, modelFactory).get(HomeViewModel.class);
        getViews();
        setupPicsRecyclerView();
        subscribeToViewModel();
        if (savedInstanceState == null) {
            mHomeViewModel.fetchPhotos("");
        }
    }

    private void getViews() {
        mPicsRecyclerView = findViewById(R.id.recycler_view_pics);
    }

    private void setupPicsRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 3);
        mPicsRecyclerView.setLayoutManager(manager);
        mPicsRecyclerView.addItemDecoration(new SpacesItemDecoration(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics())
        ));
        mAdapter = new PhotosRecyclerAdapter();

        mPicsRecyclerView.setAdapter(mAdapter);
    }

    private void subscribeToViewModel() {
        mHomeViewModel.getResults().observe(this, (val) -> {
            if (val.status == SUCCESS) {
                showResults(val.data);
            }
        });
    }

    private void showResults(List<Photo> photoList) {
        mAdapter.setPhotoList(photoList);
        mAdapter.notifyDataSetChanged();
    }
}
