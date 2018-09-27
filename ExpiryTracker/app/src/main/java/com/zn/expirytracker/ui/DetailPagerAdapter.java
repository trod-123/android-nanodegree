package com.zn.expirytracker.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.Constants;

import java.util.List;

import timber.log.Timber;

// TODO: After submission, animate deleting items
public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private List<Food> mFoodsList;

    public DetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFoodsList(@NonNull List<Food> foodsList) {
        mFoodsList = foodsList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        if (mFoodsList != null) {
            return DetailFragment.newInstance(mFoodsList.get(position).get_id());
        } else {
            Timber.e("Attempted to getItem in DetailPagerAdapter but there are no items. Returning null...");
            return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Necessary to call, along with .notifyDataSetChanged(), to reload all the fragments
        // https://stackoverflow.com/questions/10396321/remove-fragment-page-from-viewpager-in-android
        return FragmentStatePagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFoodsList != null ? mFoodsList.size() : 0;
    }

    @Override
    public float getPageWidth(int position) {
        return Constants.DEFAULT_DETAIL_PAGE_WIDTH;
    }
}
