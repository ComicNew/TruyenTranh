package com.skyreds.truyentranh.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.skyreds.truyentranh.R;
import com.skyreds.truyentranh.adapter.LoadmoreAdapter;
import com.skyreds.truyentranh.model.Comic;
import com.skyreds.truyentranh.until.Link;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopDayFragment extends Fragment {


    private LinearLayout mNoPost;
    private RecyclerView mNgayRv;

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    private LinearLayoutManager manager;
    private int pos = 0;
    ArrayList<String> lstUrl;
    private ArrayList<Comic> lstmore;
    private ArrayList<Comic> lstBXH;
    private LoadmoreAdapter adapterBXH;
    private SwipeRefreshLayout mSwipeMain;

    private void initView(@NonNull final View itemView) {
        lstBXH = new ArrayList<>();
        lstUrl = new ArrayList<>();
        lstmore = new ArrayList<>();
        mNoPost = (LinearLayout) itemView.findViewById(R.id.noPost);
        mNgayRv = (RecyclerView) itemView.findViewById(R.id.rv_Ngay);
        adapterBXH = new LoadmoreAdapter(getContext(), lstBXH);
        manager = new LinearLayoutManager(getContext());
        mSwipeMain = (SwipeRefreshLayout) itemView.findViewById(R.id.swipeRefreshLayout);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_day, container, false);
        initView(view);

        loadBXH(Link.URL_DAY);

        mNgayRv.setLayoutManager(manager);
        mNgayRv.setHasFixedSize(true);
        mNgayRv.setItemAnimator(new DefaultItemAnimator());
        mNgayRv.setAdapter(adapterBXH);
        mSwipeMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mNgayRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (pos <= lstUrl.size() - 1) {
                        loadBookmore(lstUrl.get(pos).toString());
                    }
                }
            }
        });
        return view;
    }

    void refreshItems() {
        loadBXH(Link.URL_DAY);

    }

    void onItemsLoadComplete() {
        mSwipeMain.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lstBXH.size() > 0) {
            mNoPost.setVisibility(View.GONE);
        } else {
            mNoPost.setVisibility(View.VISIBLE);
        }

    }

    public void loadBookmore(final String url) {
        pos++;
        lstmore.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                        lstBXH.addAll(lstmore);
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
        lstBXH.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                            Log.e("name:", name);
                            String chapter = sochuong.text();
                            lstBXH.add(new Comic(name, viewCount, thumb, chapter, link));
                        }

                        Elements more = document.select("div#ctl00_mainContent_ctl01_divPager");
                        Elements more1 = more.select("ul.pagination");
                        Elements allli = more1.select("li");
                        Elements next = allli.select("a");
                        for (Element element : next) {
                            String url = element.attr("href");
                            lstUrl.add(url);
                        }

                        if (lstBXH.size() > 0) {
                            mNoPost.setVisibility(View.GONE);
                        } else {
                            mNoPost.setVisibility(View.VISIBLE);
                        }
                        onItemsLoadComplete();
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
