package com.zn.expirytracker.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 2;
    public static final int FRAGMENT_AT_A_GLANCE = 0;
    public static final int FRAGMENT_LIST = 1;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRAGMENT_AT_A_GLANCE:
                return AtAGlanceFragment.newInstance();
            case FRAGMENT_LIST:
                return FoodListFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
