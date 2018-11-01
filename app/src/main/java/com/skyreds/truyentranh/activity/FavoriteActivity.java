package com.skyreds.truyentranh.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.ComicFavoriteAdapter;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.model.ComicDatabase;
import com.skyreds.truyentranh.until.RecyclerViewClickListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class FavoriteActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private ComicFavoriteAdapter adapterBXH;
    private ArrayList<Comic> lstBXH;
    private RecyclerView mFavoriteRv;
    private LinearLayout mNoPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initView();


        Realm myRealm = Realm.getDefaultInstance();
        RealmResults<ComicDatabase> results1 =
                myRealm.where(ComicDatabase.class).findAll();
        for (ComicDatabase c : results1) {
            lstBXH.add(new Comic(c.getName(), c.getView(), c.getThumb(), c.getChapter(), c.getUrl()));
        }
        if (lstBXH.size() == 0) {
            mNoPost.setVisibility(View.VISIBLE);
        } else{
            mNoPost.setVisibility(View.GONE);
        }
        adapterBXH = new ComicFavoriteAdapter(FavoriteActivity.this, lstBXH);
        RecyclerView.LayoutManager verticalLayout = new GridLayoutManager(FavoriteActivity.this, 3);
        mFavoriteRv.setLayoutManager(verticalLayout);
        mFavoriteRv.setHasFixedSize(true);
        mFavoriteRv.setItemAnimator(new DefaultItemAnimator());
        mFavoriteRv.setAdapter(adapterBXH);
    }

    private void initView() {
        lstBXH = new ArrayList<>();
        mFavoriteRv = (RecyclerView) findViewById(R.id.rv_favorite);
        mNoPost =  findViewById(R.id.noPost);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

    }
}
