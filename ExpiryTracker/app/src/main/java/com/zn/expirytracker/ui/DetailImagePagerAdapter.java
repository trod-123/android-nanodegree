package com.zn.expirytracker.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DetailImagePagerAdapter extends FragmentStatePagerAdapter {

    // TODO: For testing, get an array of color ids instead of images
    private int[] mColorResIds;

    public DetailImagePagerAdapter(FragmentManager fm, int[] colorResIds) {
        // This is where you pass in what's needed to create each of the fragments
        super(fm);
        mColorResIds = colorResIds;
    }

    @Override
    public Fragment getItem(int position) {
        // This is where we create paged items
        if (position < mColorResIds.length) {
            return DetailImageFragment.newInstance(-1, mColorResIds[position]);
        } else {
            return DetailImageFragment.newInstance(-1, -1);
        }
    }

    @Override
    public int getCount() {
        // Add one for the dedicated add image fragment
        return mColorResIds.length + 1;
    }
}
