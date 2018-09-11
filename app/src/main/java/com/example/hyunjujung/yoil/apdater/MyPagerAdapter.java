package com.example.hyunjujung.yoil.apdater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hyunjujung on 2017. 10. 12..
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] arrFrag;

    public MyPagerAdapter(FragmentManager fm, Fragment[] arrFrag) {
        super(fm);
        this.arrFrag = arrFrag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "타임라인";
            case 1:
                return "Daily";
            case 2:
                return "Weekly";
            default:
                return "";
        }
    }

    @Override
    public Fragment getItem(int position) {
        return arrFrag[position];
    }

    @Override
    public int getCount() {
        return arrFrag.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
