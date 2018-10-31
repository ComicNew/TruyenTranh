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

    private List<Category> lst;
    private Context mContext;

    public ComicCategoryAdapter(Context context, List<Category> comicList) {
        this.lst = comicList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDesc, tvName;
        public ImageView thumbnail;
        public ImageView rootbg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            thumbnail = (ImageView) itemView.findViewById(R.id.imgThumb);
            rootbg = (ImageView) itemView.findViewById(R.id.rootbg);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category item = lst.get(position);
        Glide.with(mContext).load(item.getThumb()).into(holder.rootbg);
        holder.tvDesc.setText(Html.fromHtml(item.getContent().toString()));
        holder.tvName.setText(item.getName().toString());
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