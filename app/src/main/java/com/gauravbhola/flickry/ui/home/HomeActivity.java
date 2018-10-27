package com.gauravbhola.flickry.ui.home;

import com.gauravbhola.flickry.FlickryApplication;
import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.flickry.data.model.Resource;
import com.gauravbhola.flickry.data.remote.PhotosResponse;
import com.gauravbhola.flickry.util.ViewModelFactory;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private ProgressBar mLoadMoreProgressBar;
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
        mLoadMoreProgressBar = findViewById(R.id.progressbar_loadmore);
        mMessageView = findViewById(R.id.tv_message);
        mErrorImageView = findViewById(R.id.image_error);
    }

    private void setupSearchView() {
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    private void setupPicsRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 3);
        mPicsRecyclerView.setLayoutManager(manager);
        mPicsRecyclerView.addItemDecoration(new SpacesItemDecoration(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics())
        ));
        mAdapter = new PhotosRecyclerAdapter();

        mPicsRecyclerView.setAdapter(mAdapter);
        mPicsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();
                int lastPosition = layoutManager
                        .findLastVisibleItemPosition();
                if (lastPosition == mAdapter.getItemCount() - 1) {
                    mHomeViewModel.loadNextPage();
                }
            }
        });
    }

    private Spannable getStyledText(String finalString, String query) {
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
        mHomeViewModel.getResultsState().observe(this, (val) -> {
            Resource<PhotosResponse> result = val.first;
            if (result.status == ERROR) {
                showError(result.message);
            }
            if (result.status == LOADING) {
                showLoading(val.second);
            }
        });

        mHomeViewModel.getAllResults().observe(this, val -> {
            if (val != null) {
                showResults(val.first, val.second);
            }
        });

        mHomeViewModel.getLoadMoreState().observe(this, val -> {
            if (val == null) {
                return;
            }
            if (val.first.status == LOADING) {
                mLoadMoreProgressBar.setVisibility(View.VISIBLE);
            } else {
                mLoadMoreProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showLoading(String query) {
        mStatusLayout.setVisibility(View.VISIBLE);
        if (query.trim().equals("")) {
            mMessageView.setText("Loading pictures");
        } else {
            mMessageView.setText("Loading pictures for '" + query+"'");
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.GONE);

        mAdapter.setPhotoList(null);
        mAdapter.notifyDataSetChanged();
    }

    private void showError(String message){
        mStatusLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mMessageView.setText(message + "\n Tap here to retry");
        mStatusLayout.setOnClickListener((view) -> {
            mHomeViewModel.refresh();
            mStatusLayout.setOnClickListener(null);
        });
        mErrorImageView.setVisibility(View.VISIBLE);
    }

    private void showResults(List<Photo> photoList, String query) {
        if (photoList.size() == 0) {
            mStatusLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mMessageView.setVisibility(View.VISIBLE);
            String finalString = "No results found for " + query;
            mMessageView.setText(getStyledText(finalString, query));
            mErrorImageView.setVisibility(View.GONE);
            mTitleView.setText(R.string.app_name);
            return;
        }

        if (query.equals("")) {
            mTitleView.setText(R.string.app_name);
        } else {
            String finalString = "Showing results for " + query;
            mTitleView.setText(getStyledText(finalString, query));
        }
        mStatusLayout.setVisibility(View.GONE);
        mAdapter.setPhotoList(photoList);
        mAdapter.notifyDataSetChanged();
    }
}
