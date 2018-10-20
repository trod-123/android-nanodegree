package com.zn.expirytracker.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mImageUris;
    private boolean mEditMode;
    private boolean mLeftToRightLayout;

    public DetailImagePagerAdapter(FragmentManager fm, boolean editMode, boolean leftToRightLayout) {
        // This is where you pass in what's needed to create each of the fragments
        super(fm);
        mEditMode = editMode;
        mLeftToRightLayout = leftToRightLayout;
    }

    /**
     * Sets the list of image uris for the adapter.
     * <p>
     * Pass in the original list, regardless if we're in LTR or RTL layout. This will handle
     * reversing the list if needed
     *
     * @param imageUris
     */
    public void setImageUris(@Nullable List<String> imageUris) {
        if (mLeftToRightLayout || imageUris == null) {
            mImageUris = imageUris;
        } else {
            // Take care not to reverse the existing list from the view model for RTL layouts
            // Preserve the original list by creating a new one and reversing the list that way
            List<String> reversedList = new ArrayList<>();
            for (int i = imageUris.size() - 1; i >= 0; i--) {
                reversedList.add(imageUris.get(i));
            }
            mImageUris = reversedList;
        }
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
            if (mLeftToRightLayout) {
                if (position < mImageUris.size()) {
                    return DetailImageFragment.newInstance(mImageUris.get(position), mEditMode);
                } else if (mEditMode) {
                    // Guaranteed to always be the last position; only create if we're editing
                    return DetailImageFragment.newInstance(null, true);
                }
            } else if (mEditMode) {
                if (position == 0) {
                    // Guaranteed to always be the first; only create if we're editing
                    return DetailImageFragment.newInstance(null, true);
                } else if (position - 1 < mImageUris.size()) {
                    // Subtract 1 from position to compensate for Add fragment added first
                    return DetailImageFragment.newInstance(mImageUris.get(position - 1), true);
                }
            } else if (position < mImageUris.size()) {
                return DetailImageFragment.newInstance(mImageUris.get(position), false);
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
