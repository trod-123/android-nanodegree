package com.zn.expirytracker.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import timber.log.Timber;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 2;
    public static int FRAGMENT_AT_A_GLANCE = 0;
    public static int FRAGMENT_LIST = 1;

    public MainPagerAdapter(FragmentManager fm, boolean leftToRightLayout) {
        super(fm);
        if (!leftToRightLayout) {
            FRAGMENT_AT_A_GLANCE = 1;
            FRAGMENT_LIST = 0;
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == FRAGMENT_AT_A_GLANCE) {
            return AtAGlanceFragment.newInstance();
        } else if (position == FRAGMENT_LIST) {
            return FoodListFragment.newInstance();
        } else {
            Timber.e("MainPagerAdapter: invalid position provided. Returning no fragment...");
            return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
