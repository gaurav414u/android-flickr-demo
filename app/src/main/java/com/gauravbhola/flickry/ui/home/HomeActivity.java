package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.FlickryApplication;
import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import static com.gauravbhola.flickry.data.model.Resource.Status.*;


public class HomeActivity extends AppCompatActivity {
    private HomeViewModel mHomeViewModel;
    private RecyclerView mPicsRecyclerView;
    private TextView mTitleView;
    private EditText mSearchView;
    private View mStatusLayout;
    private ProgressBar mProgressBar;
    private TextView mMessageView;
    private ImageView mErrorImageView;
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
        mStatusLayout = findViewById(R.id.layout_status);
        mProgressBar = findViewById(R.id.progressbar);
        mMessageView = findViewById(R.id.tv_message);
        mErrorImageView = findViewById(R.id.image_error);
    }

    private void setupSearchView() {
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //mViewModel.search(v.getText().toString());
                dismissKeyboard(v.getWindowToken());
                mHomeViewModel.fetchPhotos(mSearchView.getText().toString());
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
                mHomeViewModel.searchTextChanged(charSequence.toString());
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

    private Spannable getHeaderText(String query) {
        String finalString = "Showing results for " + query;
        Spannable spannable = new SpannableString(finalString);

        int color = getResources().getColor(android.R.color.holo_blue_dark);

        spannable.setSpan(new ForegroundColorSpan(color),
                finalString.indexOf(query),
                finalString.indexOf(query) + query.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f),
                finalString.indexOf(query),
                finalString.indexOf(query) + query.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    private void subscribeToViewModel() {
        mHomeViewModel.getResults().observe(this, (val) -> {
            Resource<List<Photo>> result = val.first;

            if (result.status == SUCCESS) {
                showResults(result.data, val.second);
            }
            if (result.status == ERROR) {
                showError(result.message);
            }
            if (result.status == LOADING) {
                showLoading();
            }
        });
    }

    private void showLoading() {
        mStatusLayout.setVisibility(View.VISIBLE);
        mMessageView.setText("Loading pictures");
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.GONE);

        mAdapter.setPhotoList(null);
        mAdapter.notifyDataSetChanged();
    }

    private void showError(String message){
        mStatusLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mMessageView.setText(message);
        mErrorImageView.setVisibility(View.VISIBLE);
    }


    private void showResults(List<Photo> photoList, String query) {
        if (query.equals("")) {
            mTitleView.setText(R.string.app_name);
        } else {
            mTitleView.setText(getHeaderText(query));
        }
        mStatusLayout.setVisibility(View.GONE);
        mAdapter.setPhotoList(photoList);
        mAdapter.notifyDataSetChanged();
    }
}
