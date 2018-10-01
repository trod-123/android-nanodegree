package com.zn.expirytracker.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.Toolbox;

import java.util.List;

import timber.log.Timber;

// TODO: After submission, animate deleting items
public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Food> mFoodsList;

    public DetailPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    /**
     * Sets the food list for this adapter. Note for RTL layout, this assumes the list has already
     * been reversed as this class does not handle RTL idiosyncrasies
     *
     * @param foodsList
     */
    public void setFoodsList(List<Food> foodsList) {
        // By here, list will already have been reversed if in RTL
        mFoodsList = foodsList;
        // Setting the below after the data has changed will not invalidate views. needs to be
        // called again by hosting activity or fragment
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
        return Toolbox.getFloatFromResources(mContext.getResources(), R.dimen.detail_page_width);
    }
}
