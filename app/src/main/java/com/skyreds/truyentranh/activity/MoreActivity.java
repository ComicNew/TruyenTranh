package com.skyreds.truyentranh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.ChapterAdapter;
import com.skyreds.truyentranh.adapter.LoadmoreAdapter;
import com.skyreds.truyentranh.model.Chapter;
import com.skyreds.truyentranh.model.Comic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MoreActivity extends AppCompatActivity {

    private RecyclerView mRvComic;

    private ArrayList<Comic> lstComic;
    private LoadmoreAdapter adapterBXH;

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    private LinearLayoutManager manager;
    private ArrayList<Comic> lstmore;
    private int pos = 0;
    ArrayList<String> lstUrl;
    private TextView mTvTitle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initView();

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        mTvTitle.setText(title);

        loadBXH(url);

        adapterBXH = new LoadmoreAdapter(MoreActivity.this, lstComic);
        manager = new LinearLayoutManager(MoreActivity.this);
        mRvComic.setLayoutManager(manager);
        mRvComic.setHasFixedSize(true);
        mRvComic.setItemAnimator(new DefaultItemAnimator());
        mRvComic.setAdapter(adapterBXH);


        setScrollRV();
    }

    public void setScrollRV() {
        mRvComic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    if (pos <= lstUrl.size()) {
                        loadBookmore(lstUrl.get(pos).toString());
                    }
                }
            }
        });
    }

    private void initView() {
        mRvComic = (RecyclerView) findViewById(R.id.rvComic);
        lstComic = new ArrayList<>();
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        lstmore = new ArrayList<>();
        lstUrl = new ArrayList<>();
    }

    public void loadBookmore(final String url) {
        pos++;
        lstmore.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(MoreActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Elements all = document.select("div#ctl00_divCenter");
                        Elements sub = all.select(".item");
                        for (Element element : sub) {
                            Element hinhanh = element.getElementsByTag("img").get(0);
                            Element linktruyen = element.getElementsByTag("a").get(0);
                            Element sochuong = element.getElementsByTag("a").get(2);
                            Element tentruyen = element.getElementsByTag("h3").get(0);
                            Element luotxem = element.getElementsByTag("span").get(0);
                            Element luotxem2 = null;
                            try {
                                luotxem2 = element.getElementsByTag("span").get(1);
                            } catch (Exception ex) {

                            }
                            String thumb;
                            String thumb1 = hinhanh.attr("src");
                            String thumb2 = hinhanh.attr("data-original");
                            if (thumb2.equals("")) {
                                thumb = thumb1;
                            } else {
                                thumb = thumb2;
                            }
                            String name = tentruyen.text();
                            String link = linktruyen.attr("href");
                            String view;
                            if (luotxem.text().equals("")) {
                                view = luotxem2.text();
                            } else {
                                view = luotxem.text();
                            }
                            String string = view;
                            String[] parts = string.split(" ");
                            String viewCount = parts[0];
                            if (thumb.startsWith("http:") || thumb.startsWith("https:")) {

                            } else {
                                thumb = "http:" + thumb;
                            }
                            String chapter = sochuong.text();
                            lstmore.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        lstComic.addAll(lstmore);
                        adapterBXH.notifyDataSetChanged();

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
            }
        }).start();
    }

    public void loadBXH(final String url) {
        lstComic.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(MoreActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final Document document = Jsoup.parse(response);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Elements more = document.select("div#ctl00_mainContent_ctl00_divPager");
                                Log.e("name:", more.toString());

                                Elements more1 = more.select("ul.pagination");

                                Elements allli = more1.select("li");
                                Elements next = allli.select("a");
                                for (Element element : next) {
                                    String url = element.attr("href");
                                    Log.e("name:", url);

                                    lstUrl.add(url);
                                }

                            }
                        }).start();

                        Elements all = document.select("div#ctl00_divCenter");
                        Elements sub = all.select(".item");
                        for (Element element : sub) {
                            Element hinhanh = element.getElementsByTag("img").get(0);
                            Element linktruyen = element.getElementsByTag("a").get(0);
                            Element sochuong = element.getElementsByTag("a").get(2);
                            Element tentruyen = element.getElementsByTag("h3").get(0);
                            Element luotxem = element.getElementsByTag("span").get(0);
                            Element luotxem2 = null;
                            try {
                                luotxem2 = element.getElementsByTag("span").get(1);
                            } catch (Exception ex) {

                            }
                            String thumb;
                            String thumb1 = hinhanh.attr("src");
                            String thumb2 = hinhanh.attr("data-original");
                            if (thumb2.equals("")) {
                                thumb = thumb1;
                            } else {
                                thumb = thumb2;
                            }
                            String name = tentruyen.text();
                            String link = linktruyen.attr("href");
                            String view;
                            if (luotxem.text().equals("")) {
                                view = luotxem2.text();
                            } else {
                                view = luotxem.text();
                            }
                            String string = view;
                            String[] parts = string.split(" ");
                            String viewCount = parts[0];
                            if (thumb.startsWith("http:") || thumb.startsWith("https:")) {

                            } else {
                                thumb = "http:" + thumb;
                            }

                            String chapter = sochuong.text();
                            lstComic.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        adapterBXH.notifyDataSetChanged();
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
            }
        }).start();
    }

}
