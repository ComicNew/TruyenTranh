package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.activity.PageComicActivity;
import com.skyreds.truyentranh.model.Search;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<Search> {

    private final ArrayList<Search> lst;
    private final Context mContext;
    public SearchAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Search> objects) {
        super(context, resource, objects);
        this.lst = objects;
        this.mContext = context;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int i, View view, @NonNull ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.item_custom_search,viewGroup,false);

        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(lst.get(i).getName());

        ImageView imgThumb = view.findViewById(R.id.imgThumb);
        Glide.with(mContext).load(lst.get(i).getThumb()).into(imgThumb);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PageComicActivity.class);
                intent.putExtra("url", lst.get(i).getUrl());
                mContext.startActivity(intent);
            }
        });



        return view;
    }
}
