package com.gauravbhola.flickry.ui.home;


import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.PhotoViewHolder> {
    private List<Photo> mPhotoList;

    public PhotosRecyclerAdapter() {
        super();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(mPhotoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotoList != null ? mPhotoList.size() : 0;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImage;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            mPhotoImage = itemView.findViewById(R.id.image_photo);
        }

        public void bind(Photo photo) {
            // Use image loader
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}

