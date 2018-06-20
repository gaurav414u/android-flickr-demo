package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.FlickryApplication;
import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.util.ViewModelFactory;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import static com.gauravbhola.flickry.data.model.Resource.Status.*;


public class HomeActivity extends AppCompatActivity {
    private HomeViewModel mHomeViewModel;
    private RecyclerView mPicsRecyclerView;
    private TextView mTitleView;
    private EditText mSearchView;
    private PhotosRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_home);
        ViewModelFactory modelFactory = ((FlickryApplication)getApplication()).getViewModelFactory();
        mHomeViewModel = ViewModelProviders.of(this, modelFactory).get(HomeViewModel.class);
        getViews();
        setupSearchView();
        setupTitleView();
        setupPicsRecyclerView();
        subscribeToViewModel();
        if (savedInstanceState == null) {
            mHomeViewModel.fetchPhotos("");
        }
    }

    private void getViews() {
        mPicsRecyclerView = findViewById(R.id.recycler_view_pics);
        mSearchView = findViewById(R.id.et_search);
        mTitleView = findViewById(R.id.tv_title);
    }

    private void setupSearchView() {
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //mViewModel.search(v.getText().toString());
                dismissKeyboard(v.getWindowToken());
                return true;
            }
            return false;
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //mViewModel.searchTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupTitleView() {
        mTitleView.setText(R.string.app_name);
    }

    private void dismissKeyboard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
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
