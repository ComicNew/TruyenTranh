package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.activity.PageComicActivity;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.model.ComicDatabase;

import java.util.List;

import io.realm.Realm;

public class ComicFavoriteAdapter extends RecyclerView.Adapter<ComicFavoriteAdapter.ViewHolder> {

    private List<Comic> lst;
    private Context mContext;
    private String name;

    public ComicFavoriteAdapter(Context context, List<Comic> comicList) {
        this.lst = comicList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_favorite_comic, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvChapter, tvName, tvView;
        public ImageView thumbnail;
        public ImageButton btn_Delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChapter = (TextView) itemView.findViewById(R.id.tvChapter);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvView = (TextView) itemView.findViewById(R.id.tv_View);
            thumbnail = (ImageView) itemView.findViewById(R.id.imgThumb);
            btn_Delete = (ImageButton) itemView.findViewById(R.id.btn_Delete);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Comic item = lst.get(position);
        holder.tvChapter.setText(item.getChapter());
        holder.tvName.setText(item.getName());
        holder.tvView.setText(item.getView());
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteFromDatabase(item.getName(),position);
            }
        });
        Glide.with(mContext).load(item.getThumb()).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PageComicActivity.class);
                intent.putExtra("url", item.getLinkComic().toString());
                mContext.startActivity(intent);

            }
        });

    }

    private void deleteFromDatabase(final String itemName, final int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ComicDatabase item = bgRealm.where(ComicDatabase.class).equalTo("name", itemName).findFirst();
                if (item != null) {
                    item.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.'
               removerItem(position);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
            }
        });

    }

    private void removerItem(int position) {
        lst.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lst.size());
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }


}