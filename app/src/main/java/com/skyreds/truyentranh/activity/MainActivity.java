package com.skyreds.truyentranh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.SearchAdapter;
import com.skyreds.truyentranh.adapter.ComicAdapter;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.model.Search;
import com.skyreds.truyentranh.until.CheckConnection;
import com.skyreds.truyentranh.until.Link;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

@SuppressWarnings({"StatementWithEmptyBody", "EmptyMethod"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private ArrayList<Comic> lstNewUpdate;
    private ArrayList<Comic> lstHotTrend;
    private ArrayList<Comic> lstBoy;
    private ArrayList<Comic> lstGirl;
    private BannerSlider mSliderBanner;

    private ComicAdapter newUpdateAdapter;

    private ShimmerRecyclerView rv_NewUpdate;
    private ShimmerRecyclerView rv_HotTrend;
    private ShimmerRecyclerView rv_Boy;
    private ShimmerRecyclerView rv_Girl;
    private AutoCompleteTextView mEditAuto;
    private ArrayList<String> urlBanner;
    private SearchAdapter adapterSearch;
    private ArrayList<Search> lstSearch;
    private List<Banner> banners;
    private RelativeLayout mRoot;
    private CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkConnection = new CheckConnection(MainActivity.this, mRoot);
        checkConnection.checkConnection();

        FirebaseMessaging.getInstance();

        rv_NewUpdate.showShimmerAdapter();
        rv_Girl.showShimmerAdapter();
        rv_Boy.showShimmerAdapter();
        rv_HotTrend.showShimmerAdapter();
        loadComicNewUpdate();
        loadComicHot();
        loadComicBoy();
        loadComicGirl();
        Realm.init(this);
        mEditAuto.addTextChangedListener(this);
        adapterSearch = new SearchAdapter(this, R.layout.item_custom_search, lstSearch);
        mEditAuto.setAdapter(adapterSearch);
        mEditAuto.setThreshold(0);

        mSliderBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, PageComicActivity.class);
                intent.putExtra("url", urlBanner.get(position));
                startActivity(intent);
            }
        });
    }


    private void initView() {
        lstNewUpdate = new ArrayList<>();
        lstHotTrend = new ArrayList<>();
        lstBoy = new ArrayList<>();
        lstGirl = new ArrayList<>();
        banners = new ArrayList<>();
        urlBanner = new ArrayList<>();
        lstSearch = new ArrayList<>();
        mSliderBanner = findViewById(R.id.banner_slider);
        rv_NewUpdate = findViewById(R.id.rv_NewUpdate);
        rv_HotTrend = findViewById(R.id.rv_HotTrend);
        rv_Boy = findViewById(R.id.rv_Boy);
        rv_Girl = findViewById(R.id.rv_Girl);
        Button mTheLoaiBtn = findViewById(R.id.btn_TheLoai);
        mTheLoaiBtn.setOnClickListener(this);
        Button mBXHBtn = findViewById(R.id.btn_BXH);
        mBXHBtn.setOnClickListener(this);
        Button mFavoriteBtn = findViewById(R.id.btn_Favorite);
        mFavoriteBtn.setOnClickListener(this);
        mEditAuto = findViewById(R.id.editAuto);
        mRoot = findViewById(R.id.root);
        Button mTruyenmoiBtn = findViewById(R.id.btn_newCommic);
        mTruyenmoiBtn.setOnClickListener(this);
        Button mTruyenhotBtn = findViewById(R.id.btn_hotCommic);
        mTruyenhotBtn.setOnClickListener(this);
        Button mTruyenCGBtn = findViewById(R.id.btn_truyenCG);
        mTruyenCGBtn.setOnClickListener(this);
        Button mTruyenCTBtn = findViewById(R.id.btn_truyenCT);
        mTruyenCTBtn.setOnClickListener(this);
    }

    private void loadComicNewUpdate() {
        lstNewUpdate.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_HOMEPAGE, new Response.Listener<String>() {
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
                                ex.printStackTrace();
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
                                view = Objects.requireNonNull(luotxem2).text();
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
                            lstNewUpdate.add(new Comic(name, viewCount, thumb, chapter, link));

                        }


                        for (int i = 0; i < 10; i++) {
                            String url = lstNewUpdate.get(i).getLinkComic();
                            String thumb = lstNewUpdate.get(i).getThumb();
                            banners.add(new RemoteBanner(thumb));
                            urlBanner.add(url);
                        }
                        mSliderBanner.setBanners(banners);
                        mSliderBanner.setInterval(10000);
                        mSliderBanner.setDefaultIndicator(2);
                        mSliderBanner.setMustAnimateIndicators(true);
                        mSliderBanner.setLoopSlides(true);

                        rv_NewUpdate.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstNewUpdate);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_NewUpdate.setLayoutManager(horizontalLayout);
                                rv_NewUpdate.setHasFixedSize(true);
                                rv_NewUpdate.setItemAnimator(new DefaultItemAnimator());
                                rv_NewUpdate.setAdapter(newUpdateAdapter);
                                rv_NewUpdate.hideShimmerAdapter();
                            }
                        });
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

    private void loadComicHot() {
        lstHotTrend.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {


                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_HOT_TREND, new Response.Listener<String>() {
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
                                ex.printStackTrace();
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
                            lstHotTrend.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        rv_HotTrend.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstHotTrend);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_HotTrend.setLayoutManager(horizontalLayout);
                                rv_HotTrend.setHasFixedSize(true);
                                rv_HotTrend.setItemAnimator(new DefaultItemAnimator());
                                rv_HotTrend.setAdapter(newUpdateAdapter);
                                rv_HotTrend.hideShimmerAdapter();
                            }
                        });
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

    private void loadComicBoy() {
        lstBoy.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_BOY, new Response.Listener<String>() {
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
                                ex.printStackTrace();
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
                            lstBoy.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        rv_Boy.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstBoy);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_Boy.setLayoutManager(horizontalLayout);
                                rv_Boy.setHasFixedSize(true);
                                rv_Boy.setItemAnimator(new DefaultItemAnimator());
                                rv_Boy.setAdapter(newUpdateAdapter);
                                rv_Boy.hideShimmerAdapter();
                            }
                        });
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

    private void loadComicGirl() {
        lstGirl.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {


                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_GIRL, new Response.Listener<String>() {
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
                                ex.printStackTrace();
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
                            lstGirl.add(new Comic(name, viewCount, thumb, chapter, link));
                        }
                        rv_Girl.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstGirl);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_Girl.setLayoutManager(horizontalLayout);
                                rv_Girl.setHasFixedSize(true);
                                rv_Girl.setItemAnimator(new DefaultItemAnimator());
                                rv_Girl.setAdapter(newUpdateAdapter);
                                rv_Girl.hideShimmerAdapter();
                            }
                        });
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


    @Override
    public void onClick(View v) {
        Intent more = new Intent(MainActivity.this, MoreActivity.class);

        switch (v.getId()) {
            case R.id.btn_TheLoai:
                // TODO 18/10/11
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_BXH:
                // TODO 18/10/11
                Intent inten = new Intent(MainActivity.this, TopActivity.class);
                startActivity(inten);
                break;
            case R.id.btn_Favorite:
                // TODO 18/10/11
                Intent inte = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(inte);
                break;
            case R.id.btn_newCommic:// TODO 18/10/26
                more.putExtra("url", Link.URL_HOMEPAGE);
                more.putExtra("title", "Truyện mới cập nhật");
                startActivity(more);
                break;
            case R.id.btn_hotCommic:// TODO 18/10/26
                more.putExtra("url", Link.URL_HOT_TREND);
                more.putExtra("title", "Truyện hot");
                startActivity(more);
                break;
            case R.id.btn_truyenCG:// TODO 18/10/26
                more.putExtra("url", Link.URL_GIRL);
                more.putExtra("title", "Truyện con gái thích");
                startActivity(more);
                break;
            case R.id.btn_truyenCT:// TODO 18/10/26
                more.putExtra("url", Link.URL_HOMEPAGE);
                more.putExtra("title", "Truyện con trai thích");
                startActivity(more);
                break;
            default:
                break;
        }
    }

    public void onTextChanged(CharSequence arg0, int arg1,
                              int arg2, int arg3) {
    }

    public void afterTextChanged(Editable arg0) {
        try {
            requestSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    private void requestSearch() {
        lstSearch.clear();
        final String la = mEditAuto.getText().toString();
        String keyword = la.replaceAll(" ", "+");
        String urlsearch = Link.URL_SEARCH + keyword;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlsearch, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements all = document.select("div#ctl00_divCenter");
                Elements sub = all.select(".item");
                for (Element element : sub) {
                    Element hinhanh = element.getElementsByTag("img").get(0);
                    Element linktruyen = element.getElementsByTag("a").get(0);
                    Element tentruyen = element.getElementsByTag("h3").get(0);
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
                    if (thumb.startsWith("http:") || thumb.startsWith("https:")) {
                    } else {
                        thumb = "http:" + thumb;
                    }
                    lstSearch.add(new Search(name, thumb, link));
                }
                adapterSearch = new SearchAdapter(MainActivity.this, R.layout.item_custom_search, lstSearch);
                mEditAuto.setAdapter(adapterSearch);
                adapterSearch.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
//                    catch (JSONException e2) {
//                        e2.printStackTrace();
//                    }
                }
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("status:", "onresume");
        checkConnection.checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("status:", "onPause");
    }
}
