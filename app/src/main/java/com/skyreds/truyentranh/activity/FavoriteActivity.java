package com.skyreds.truyentranh.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.ComicFavoriteAdapter;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.model.ComicDatabase;
import com.skyreds.truyentranh.until.RecyclerViewClickListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class FavoriteActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private ArrayList<Comic> lstBXH;
    private RecyclerView mFavoriteRv;
    private LinearLayout mNoPost;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initView();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mToolbar.setTitle(getString(R.string.favorite));
        mToolbar.setTitleTextColor(Color.WHITE);
        Realm myRealm = Realm.getDefaultInstance();
        RealmResults<ComicDatabase> results1 =
                myRealm.where(ComicDatabase.class).findAll();
        for (ComicDatabase c : results1) {
            lstBXH.add(new Comic(c.getName(), c.getView(), c.getThumb(), c.getChapter(), c.getUrl()));
        }
        if (lstBXH.size() == 0) {
            mNoPost.setVisibility(View.VISIBLE);
        } else {
            mNoPost.setVisibility(View.GONE);
        }
        ComicFavoriteAdapter adapterBXH = new ComicFavoriteAdapter(FavoriteActivity.this, lstBXH);
        RecyclerView.LayoutManager verticalLayout = new GridLayoutManager(FavoriteActivity.this, 3);
        mFavoriteRv.setLayoutManager(verticalLayout);
        mFavoriteRv.setHasFixedSize(true);
        mFavoriteRv.setItemAnimator(new DefaultItemAnimator());
        mFavoriteRv.setAdapter(adapterBXH);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        lstBXH = new ArrayList<>();
        mFavoriteRv = findViewById(R.id.rv_favorite);
        mNoPost = findViewById(R.id.noPost);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

}
