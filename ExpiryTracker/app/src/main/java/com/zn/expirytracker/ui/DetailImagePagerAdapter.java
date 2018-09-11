package com.zn.expirytracker.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class DetailImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mImageUris;

    public DetailImagePagerAdapter(FragmentManager fm) {
        // This is where you pass in what's needed to create each of the fragments
        super(fm);
    }

    public void setImageUris(List<String> imageUris) {
        mImageUris = imageUris;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        // This is where we create paged items
        if (mImageUris != null) {
            if (position < mImageUris.size()) {
                return DetailImageFragment.newInstance(-1, mImageUris.get(position));
            } else {
                // Guaranteed to always be the last position
                return DetailImageFragment.newInstance(-1, null);
            }
        } else {
            // Don't create anything if null
            return null;
        }
    }

    @Override
    public int getCount() {
        // Add one for the dedicated add image fragment
        return mImageUris != null ? mImageUris.size() + 1 : 0;
    }
}
