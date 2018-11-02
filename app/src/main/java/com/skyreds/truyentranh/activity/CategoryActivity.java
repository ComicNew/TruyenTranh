package com.skyreds.truyentranh.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.ChapterAdapter;
import com.skyreds.truyentranh.adapter.LoadmoreAdapter;
import com.skyreds.truyentranh.model.Chapter;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.until.Link;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("StatementWithEmptyBody")
public class CategoryActivity extends AppCompatActivity {

    private RecyclerView mRvComic;

    private ArrayList<Chapter> lstTheLoai;
    private ArrayList<Comic> lstComic;
    private ArrayList<String> lstName;

    private ChapterAdapter KindAdapter;
    private LoadmoreAdapter comicAdapter;

    private PickerUI mUiViewPicker;


    private Boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private LinearLayoutManager manager;
    private ArrayList<Comic> listMore;
    private int pos = 0;
    private ArrayList<String> lstUrl;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initView();

        mToolbar.setTitle(getString(R.string.categoryComic));
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listMore = new ArrayList<>();
        loadCategory();


        comicAdapter = new LoadmoreAdapter(CategoryActivity.this, lstComic);
        manager = new LinearLayoutManager(CategoryActivity.this);
        mRvComic.setLayoutManager(manager);
        mRvComic.setHasFixedSize(true);
        mRvComic.setItemAnimator(new DefaultItemAnimator());
        mRvComic.setAdapter(comicAdapter);

        lstUrl = new ArrayList<>();

        mUiViewPicker.setOnClickItemPickerUIListener(new PickerUI.PickerUIItemClickListener() {
            @Override
            public void onItemClickPickerUI(int which, int position, String valueResult) {
                loadBook(lstTheLoai.get(position).getUrl());
            }
        });

        setScrollRV();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setScrollRV() {
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
                        loadBookmore(lstUrl.get(pos));
                    }
                }
            }
        });
    }

    private void initView() {
        mRvComic = findViewById(R.id.rvComic);
        lstComic = new ArrayList<>();
        lstTheLoai = new ArrayList<>();
        lstName = new ArrayList<>();
        mUiViewPicker = findViewById(R.id.picker_ui_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void loadBook(final String url) {
        lstComic.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(CategoryActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Elements all = document.select("div#ctl00_divCenter");
                        Elements sub = all.select(".item");
                        for (Element element : sub) {
                            Element image = element.getElementsByTag("img").get(0);
                            Element linkComic = element.getElementsByTag("a").get(0);
                            Element chap = element.getElementsByTag("a").get(2);
                            Element nameComic = element.getElementsByTag("h3").get(0);
                            Element viewer = element.getElementsByTag("span").get(0);
                            Element viewer2 = null;
                            try {
                                viewer2 = element.getElementsByTag("span").get(1);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String thumb;
                            String thumb1 = image.attr("src");
                            String thumb2 = image.attr("data-original");
                            if (thumb2.equals("")) {
                                thumb = thumb1;
                            } else {
                                thumb = thumb2;
                            }
                            String name = nameComic.text();
                            String link = linkComic.attr("href");
                            String view;
                            if (viewer.text().equals("")) {
                                view = Objects.requireNonNull(viewer2).text();
                            } else {
                                view = viewer.text();
                            }
                            String string = view;
                            String[] parts = string.split(" ");
                            String viewCount = parts[0];
                            if (thumb.startsWith("http:") || thumb.startsWith("https:")) {

                            } else {
                                thumb = "http:" + thumb;
                            }
                            String chapter = chap.text();
                            lstComic.add(new Comic(name, viewCount, thumb, chapter, link));
                        }

                        Elements more = document.select("div#ctl00_mainContent_ctl01_divPager");
                        Elements more1 = more.select("ul.pagination");
                        Elements allli = more1.select("li");
                        Elements next = allli.select("a");
                        for (Element element : next) {
                            String url = element.attr("href");
                            lstUrl.add(url);
                        }
                        comicAdapter.notifyDataSetChanged();


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

    private void loadBookmore(final String url) {
        pos++;
        listMore.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(CategoryActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Elements all = document.select("div#ctl00_divCenter");
                        Elements sub = all.select(".item");
                        for (Element element : sub) {
                            Element image = element.getElementsByTag("img").get(0);
                            Element linkComic = element.getElementsByTag("a").get(0);
                            Element chap = element.getElementsByTag("a").get(2);
                            Element nameComic = element.getElementsByTag("h3").get(0);
                            Element viewer = element.getElementsByTag("span").get(0);
                            Element viewer2 = null;
                            try {
                                viewer2 = element.getElementsByTag("span").get(1);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String thumb;
                            String thumb1 = image.attr("src");
                            String thumb2 = image.attr("data-original");
                            if (thumb2.equals("")) {
                                thumb = thumb1;
                            } else {
                                thumb = thumb2;
                            }
                            String name = nameComic.text();
                            String link = linkComic.attr("href");
                            String view;
                            if (viewer.text().equals("")) {
                                view = viewer2.text();
                            } else {
                                view = viewer.text();
                            }
                            String string = view;
                            String[] parts = string.split(" ");
                            String viewCount = parts[0];
                            if (thumb.startsWith("http:") || thumb.startsWith("https:")) {

                            } else {
                                thumb = "http:" + thumb;
                            }
                            String chapter = chap.text();
                            listMore.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        lstComic.addAll(listMore);
                        comicAdapter.notifyDataSetChanged();


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

    private void loadCategory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(CategoryActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_KIND, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lstTheLoai.clear();
                        Document document = Jsoup.parse(response);
                        Elements start = document.select("div.box.darkBox.genres.Module.Module-204");
                        Elements all = start.select("ul.nav");
                        Elements allli = all.select("li");
                        Elements category = allli.select("a");
                        for (Element element : category) {
                            String url = element.attr("href");
                            String name = element.text();
                            lstTheLoai.add(new Chapter(name, url));
                            lstName.add(name);
                        }

                        lstTheLoai.remove(lstTheLoai.size() - 1);
                        lstTheLoai.remove(lstTheLoai.size() - 1);
                        lstName.remove(lstName.size() - 1);
                        lstName.remove(lstName.size() - 1);
                        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                                .withItems(lstName)
                                .withAutoDismiss(false)
                                .withColorTextCenter(Color.WHITE)
                                .withColorTextNoCenter(Color.DKGRAY)
                                .withItemsClickables(false)
                                .withUseBlur(false)
                                .build();
                        mUiViewPicker.setSettings(pickerUISettings);
                        loadBook(lstTheLoai.get(5).getUrl());
                        mUiViewPicker.slide(5);

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
