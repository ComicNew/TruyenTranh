package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.skyreds.truyentranh.activity.DetailComicActivity;
import com.skyreds.truyentranh.activity.PageComicActivity;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.model.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

    private List<Chapter> lst;
    private Context mContext;

    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.lst = chapterList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_chapter, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Button tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (Button) itemView.findViewById(R.id.tvName);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Chapter item = lst.get(position);
        holder.tvName.setText(item.getName());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailComicActivity.class);
                intent.putExtra("url",item.getUrl());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

}