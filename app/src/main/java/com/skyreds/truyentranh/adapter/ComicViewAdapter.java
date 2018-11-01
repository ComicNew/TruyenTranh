package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.model.Image;

import java.util.List;

public class ComicViewAdapter extends RecyclerView.Adapter<ComicViewAdapter.ViewHolder>{

    private final List<Image> lst;
    private Context mContext;

    public ComicViewAdapter(Context context, List<Image> chapterList) {
        this.lst = chapterList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_image, parent, false);

        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgComic;

        public ViewHolder(View itemView) {
            super(itemView);
            imgComic = itemView.findViewById(R.id.photo_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Image item = lst.get(position);

//        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
//        imageLoader.displayImage(item.getUrl().toString(), holder.imgComic);

        Glide.with(mContext).load(item.getUrl()).into(holder.imgComic);
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