package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.activity.DetailComicActivity;
import com.skyreds.truyentranh.model.Chapter;
import com.skyreds.truyentranh.model.Image;

import java.util.List;

public class ComicViewAdapter extends RecyclerView.Adapter<ComicViewAdapter.ViewHolder>{

    private List<Image> lst;
    private Context mContext;

    public ComicViewAdapter(Context context, List<Image> chapterList) {
        this.lst = chapterList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_image, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgComic;

        public ViewHolder(View itemView) {
            super(itemView);
            imgComic = (ImageView) itemView.findViewById(R.id.photo_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Image item = lst.get(position);

//        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
//        imageLoader.displayImage(item.getUrl().toString(), holder.imgComic);

        Glide.with(mContext).load(item.getUrl().toString()).into(holder.imgComic);
//        CustomPicasso.with(mContext)
//                .load(item.getUrl().toString())
//                .error(R.drawable.bgtest)
//                .into(holder.imgComic);
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

}