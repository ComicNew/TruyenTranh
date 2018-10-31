package com.skyreds.truyentranh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.activity.PageComicActivity;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.model.ComicDatabase;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    private List<Comic> lst;
    private Context mContext;
    Realm myRealm = Realm.getDefaultInstance();

    public ComicAdapter(Context context, List<Comic> comicList) {
        this.lst = comicList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_comic, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvChapter, tvName, tvView;
        public ImageView thumbnail;
        public ImageButton btnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChapter = (TextView) itemView.findViewById(R.id.tvChapter);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvView = (TextView) itemView.findViewById(R.id.tv_View);
            thumbnail = (ImageView) itemView.findViewById(R.id.imgThumb);
            btnFavorite = (ImageButton) itemView.findViewById(R.id.btn_Like);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Comic item = lst.get(position);
        holder.tvChapter.setText(item.getChapter());
        holder.tvName.setText(item.getName());
        holder.tvView.setText(item.getView());

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = false;
                RealmResults<ComicDatabase> results1 =
                        myRealm.where(ComicDatabase.class).findAll();
                for (ComicDatabase c : results1) {
                    if (c.getName() != null)
                        if (c.getName().equals(item.getName().toString().trim())) {
                            Toast.makeText(mContext, "Truyện đã có trong mục yêu thích !", Toast.LENGTH_SHORT).show();
                            check = true;
                            Log.e("check:", String.valueOf(check));
                            break;

                        }
                }

                if (check == false) {
                    myRealm.beginTransaction();
                    ComicDatabase comic = myRealm.createObject(ComicDatabase.class);
                    comic.setName(item.getName().toString());
                    comic.setChapter(item.getChapter().toString());
                    comic.setThumb(item.getThumb().toString());
                    comic.setView(item.getView());
                    comic.setUrl(item.getLinkComic());
                    myRealm.commitTransaction();
                    Toast.makeText(mContext, "Đã lưu truyện " + item.getName() + " vào mục yêu thích !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Glide.with(mContext).

                load(item.getThumb()).

                into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PageComicActivity.class);
                intent.putExtra("url", item.getLinkComic().toString());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lst.size();
    }


}