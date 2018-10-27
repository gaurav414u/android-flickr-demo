package com.gauravbhola.flickry.ui.home;


import com.gauravbhola.flickry.R;
import com.gauravbhola.flickry.data.model.Photo;
import com.gauravbhola.gcache.GCache;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.PhotoViewHolder> {
    private List<Photo> mPhotoList;

    public PhotosRecyclerAdapter() {
        super();
        setHasStableIds(true);
    }

    public void setPhotoList(List<Photo> photoList) {
        mPhotoList = photoList;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(mPhotoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotoList != null ? mPhotoList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mPhotoList.get(position).getId());
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImage;
        private Context mContext;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            mPhotoImage = itemView.findViewById(R.id.image_photo);
            mContext = itemView.getContext();
        }

        public void bind(Photo photo) {
            // Use image loader
            GCache.with(mContext).load(photo.getUrl())
                    .into(mPhotoImage);

//            Glide.with(mContext).load(photo.getUrl())
//                    .into(mPhotoImage);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}

