package com.zn.expirytracker.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class DetailImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mImageUris;
    private boolean mEditMode;

    public DetailImagePagerAdapter(FragmentManager fm, boolean editMode) {
        // This is where you pass in what's needed to create each of the fragments
        super(fm);
        mEditMode = editMode;
    }

    public void setImageUris(@Nullable List<String> imageUris) {
        mImageUris = imageUris;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        // This is where we create paged items
        if (mImageUris != null) {
            if (position < mImageUris.size()) {
                return DetailImageFragment.newInstance(mImageUris.get(position), mEditMode);
            } else if (mEditMode) {
                // Guaranteed to always be the last position; only create if we're editing
                return DetailImageFragment.newInstance(null, true);
            }
        }
        // Don't create anything if null
        return null;
    }

    @Override
    public int getCount() {
        // Add one for the dedicated add image fragment, only if we're editing
        return mImageUris != null ? (mEditMode ? mImageUris.size() + 1 : mImageUris.size()) : 0;
    }
}
