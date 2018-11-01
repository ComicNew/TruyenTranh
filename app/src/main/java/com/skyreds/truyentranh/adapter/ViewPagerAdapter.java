package com.skyreds.truyentranh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skyreds.truyentranh.fragment.TopDayFragment;
import com.skyreds.truyentranh.fragment.TopMonthFragment;
import com.skyreds.truyentranh.fragment.TopWeekFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new TopDayFragment();
                break;
            case 1:
                frag = new TopWeekFragment();
                break;
            case 2:
                frag = new TopMonthFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Ngày";
                break;
            case 1:
                title = "Tuần";
                break;
            case 2:
                title = "Tháng";
                break;
        }
        return title;
    }

}