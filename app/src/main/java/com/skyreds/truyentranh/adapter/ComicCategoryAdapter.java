package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.activity.PageComicActivity;
import com.skyreds.truyentranh.model.Category;

import java.util.List;

public class ComicCategoryAdapter extends RecyclerView.Adapter<ComicCategoryAdapter.ViewHolder>{

    private final List<Category> lst;
    private Context mContext;

    public ComicCategoryAdapter(Context context, List<Category> comicList) {
        this.lst = comicList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);

        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDesc;
        public TextView tvName;
        public ImageView thumbnail;
        public ImageView rootbg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvName = itemView.findViewById(R.id.tvName);
            thumbnail = itemView.findViewById(R.id.imgThumb);
            rootbg = itemView.findViewById(R.id.rootbg);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category item = lst.get(position);
        Glide.with(mContext).load(item.getThumb()).into(holder.rootbg);
        holder.tvDesc.setText(Html.fromHtml(item.getContent()));
        holder.tvName.setText(item.getName());
        Glide.with(mContext).load(item.getThumb()).into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PageComicActivity.class);
                intent.putExtra("url",item.getUrlBook());
                mContext.startActivity(intent);
//                Intent i = new Intent(mContext, DetailActivity.class);
//                i.putExtra("position",position);
//                i.putStringArrayListExtra("list",lstLink);
//                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

}