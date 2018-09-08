package com.zn.expirytracker.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zn.expirytracker.data.TestDataGen;

public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private static final float DEFAULT_PAGE_WIDTH = 0.98f;

    private TestDataGen mDataGenerator;

    public DetailPagerAdapter(FragmentManager fm, TestDataGen dataGenerator) {
        super(fm);
        mDataGenerator = dataGenerator;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mDataGenerator.getDatabaseSize();
    }

    @Override
    public float getPageWidth(int position) {
        return DEFAULT_PAGE_WIDTH;
    }
}
