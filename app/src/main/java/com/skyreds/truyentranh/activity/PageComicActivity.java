package com.skyreds.truyentranh.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.ChapterAdapter;
import com.skyreds.truyentranh.model.Chapter;
import com.skyreds.truyentranh.model.ComicDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class PageComicActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImgThumb;
    private TextView mTvName;
    private TextView mTvAuthor;
    private TextView mTvTrangThai;
    private TextView mTvTheLoai;

    private String URL_COMIC;
    private String tacgia;
    private String theloai;
    private String trangthai;
    private String theodoi;
    private String content;
    private String updatetime;
    private String thumb;
    private String title;
    private ArrayList<Chapter> lstChapter;
    private ArrayList<Chapter> lstChapNormal;
    private RecyclerView mRvChapter;
    private ChapterAdapter adapter;
    private TextView mUpdateTv;
    private TextView mViewTv;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> lstSpinner;
    private Spinner mSpinnerSort;
    private ImageButton mLikeBtn;
    private Realm myRealm;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_comic);
        initView();
        myRealm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        URL_COMIC = intent.getStringExtra("url");
        lstSpinner = new ArrayList<>();
        lstSpinner.add("Sắp xếp");
        lstSpinner.add("Từ trên xuống");
        lstSpinner.add("Từ dưới lên");
        int restore = restoringPreferences();
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, lstSpinner);
        mSpinnerSort.setAdapter(spinnerAdapter);
        mSpinnerSort.setSelection(restore);
        mSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    savingPreferences(i);
                }

                if (i == 2) {
                    adapter = new ChapterAdapter(getApplicationContext(), lstChapNormal);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                    mRvChapter.setLayoutManager(mLayoutManager);
                    mRvChapter.setHasFixedSize(true);
                    mRvChapter.setItemAnimator(new DefaultItemAnimator());
                    adapter.notifyDataSetChanged();
                    mRvChapter.setAdapter(adapter);
                }
                if (i == 1) {
                    adapter = new ChapterAdapter(getApplicationContext(), lstChapter);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                    mRvChapter.setLayoutManager(mLayoutManager);
                    mRvChapter.setHasFixedSize(true);
                    mRvChapter.setItemAnimator(new DefaultItemAnimator());
                    adapter.notifyDataSetChanged();
                    mRvChapter.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadBook(URL_COMIC);


    }

    public void addToDB(String title, String chapter, String thumb, String view, String url) {

        boolean check = false;
        RealmResults<ComicDatabase> results1 =
                myRealm.where(ComicDatabase.class).findAll();
        for (ComicDatabase c : results1) {
            if (c.getName() != null)
                if (c.getName().equals(title)) {
                    Toast.makeText(this, "Truyện đã có trong mục yêu thích !", Toast.LENGTH_SHORT).show();
                    check = true;
                    Log.e("check:", String.valueOf(check));
                    break;

                }
        }

        if (check == false) {
            myRealm.beginTransaction();
            ComicDatabase comic = myRealm.createObject(ComicDatabase.class);
            comic.setName(title);
            comic.setChapter(chapter);
            comic.setThumb(thumb);
            comic.setView(view);
            comic.setUrl(url);
            myRealm.commitTransaction();
            Toast.makeText(this, "Đã lưu truyện " + title + " vào mục yêu thích !", Toast.LENGTH_SHORT).show();
        }
    }

    public void savingPreferences(int i) {
        SharedPreferences pre = getSharedPreferences("sort", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("sort", i);
        editor.commit();
    }


    public int restoringPreferences() {
        SharedPreferences pre = getSharedPreferences
                ("sort", MODE_PRIVATE);
        int sort = pre.getInt("sort", 2);
        return sort;
    }

    public void loadBook(final String url) {
        lstChapNormal.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {


                RequestQueue requestQueue = Volley.newRequestQueue(PageComicActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        //load list chapter
                        Elements chapx = document.select("div.list-chapter");
                        Elements chap = chapx.select("li.row");
                        Elements aElements = chap.select("a");
                        for (Element element : aElements) {
                            String urlChap = element.attr("href");
                            String nameChap = element.text();
                            lstChapNormal.add(new Chapter(nameChap, urlChap));
                        }

                        for (int i = 0; i < lstChapNormal.size(); i++) {
                            String x1 = lstChapNormal.get(lstChapNormal.size() - (i + 1)).getName();
                            String x2 = lstChapNormal.get(lstChapNormal.size() - (i + 1)).getUrl();
                            lstChapter.add(new Chapter(x1, x2));
                        }
                        //load thumbnail
                        Elements contents = document.select("div.detail-info");
                        Element image = contents.select("img").first();
                        thumb = image.attr("src");
                        Glide.with(getApplicationContext()).load(thumb).into(mImgThumb);

                        //load title
                        Elements tieude = document.select("article#item-detail");
                        Element tit = tieude.select("h1").first();
                        title = tit.text();
                        mTvName.setText(title);
                        //time update
                        Element update = tieude.select("time").first();
                        updatetime = update.text();
                        mUpdateTv.setText(updatetime);

                        Elements author;
                        Element tac;
                        Element trang;
                        Element the;
                        Element view;
                        author = document.select("div.col-xs-8.col-info");
                        tac = author.select("p").get(1);
                        trang = author.select("p").get(3);
                        the = author.select("p").get(5);
                        view = author.select("p").get(7);

                        String test = tac.text().trim();
                        Log.e("TEst=", "'" + test + "'");
                        if (test.equals("Tác giả")) {
                            Log.e("TEst:", "true");
                            tac = author.select("p").get(2);
                            trang = author.select("p").get(4);
                            the = author.select("p").get(6);
                            view = author.select("p").get(8);
                        } else {
                            Log.e("TEst:", "false");
                        }
                        tacgia = tac.text();
                        mTvAuthor.setText(tacgia);

                        trangthai = trang.text();
                        mTvTrangThai.setText(trangthai);

                        theloai = the.text();
                        mTvTheLoai.setText(theloai);

                        theodoi = view.text();
                        mViewTv.setText(theodoi);

                        /*Nội dung*/
                        Elements noidung = document.select("div.detail-content");
                        Element cont = noidung.select("p").first();
                        content = cont.text();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        150000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
                mRvChapter.post(new Runnable() {
                    @Override
                    public void run() {
                        int i = restoringPreferences();
                        if (i == 2) {
                            adapter = new ChapterAdapter(getApplicationContext(), lstChapNormal);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                            mRvChapter.setLayoutManager(mLayoutManager);
                            mRvChapter.setHasFixedSize(true);
                            mRvChapter.setItemAnimator(new DefaultItemAnimator());
                            adapter.notifyDataSetChanged();
                            mRvChapter.setAdapter(adapter);
                        }
                        if (i == 1) {
                            adapter = new ChapterAdapter(getApplicationContext(), lstChapter);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
                            mRvChapter.setLayoutManager(mLayoutManager);
                            mRvChapter.setHasFixedSize(true);
                            mRvChapter.setItemAnimator(new DefaultItemAnimator());
                            adapter.notifyDataSetChanged();
                            mRvChapter.setAdapter(adapter);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Like:
                addToDB(title, lstChapNormal.get(0).getName().toString(), thumb, theodoi, URL_COMIC);
                break;
            default:
                break;
        }
    }

    static class SavedState extends View.BaseSavedState {
        public int mScrollPosition;

        SavedState(Parcel in) {
            super(in);
            mScrollPosition = in.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mScrollPosition);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private void initView() {
        mImgThumb = (ImageView) findViewById(R.id.imgThumb);
        mTvName = (TextView) findViewById(R.id.tvName);
        mTvAuthor = (TextView) findViewById(R.id.tvAuthor);
        mTvTrangThai = (TextView) findViewById(R.id.tvTrangThai);
        mTvTheLoai = (TextView) findViewById(R.id.tvTheLoai);
        lstChapter = new ArrayList<>();
        lstChapNormal = new ArrayList<>();
        mRvChapter = (RecyclerView) findViewById(R.id.rvChapter);
        mUpdateTv = (TextView) findViewById(R.id.tv_Update);
        mViewTv = (TextView) findViewById(R.id.tv_View);
        mSpinnerSort = (Spinner) findViewById(R.id.spinnerSort);
        mLikeBtn = (ImageButton) findViewById(R.id.btn_Like);
        mLikeBtn.setOnClickListener(this);
    }
}