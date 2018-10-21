package com.zn.expirytracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zn.expirytracker.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import timber.log.Timber;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 3;
    public static int FRAGMENT_AT_A_GLANCE = 0;
    public static int FRAGMENT_LIST = 1;
    public static int ACTIVITY_CAPTURE = 2;

    private final int[] RES_TITLES = new int[]{
            R.string.fragment_at_a_glance_name,
            R.string.fragment_food_list_name,
            R.string.action_add_item
    };
    private final int[] RES_ICONS = new int[]{
            R.drawable.ic_chart_bar_white_24dp,
            R.drawable.ic_format_list_bulleted_white_24dp,
            R.drawable.ic_add_white_24dp
    };

    public MainPagerAdapter(FragmentManager fm, boolean leftToRightLayout) {
        super(fm);
        if (!leftToRightLayout) {
            ACTIVITY_CAPTURE = 2;
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
        } else if (position == ACTIVITY_CAPTURE) {
            return BlankFragment.newInstance();
        } else {
            Timber.e("MainPagerAdapter: invalid position provided. Returning no fragment...");
            return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    /**
     * Inflates a custom layout for tabs
     * <p>
     * https://guides.codepath.com/android/google-play-style-tabs-using-tablayout
     *
     * @param position
     * @param context
     * @return
     */
    public View getTabView(int position, Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_main, null);
        ImageView iv = (ImageView) v.findViewById(R.id.iv_tab_main);
        iv.setImageResource(RES_ICONS[position]);
        iv.setContentDescription(context.getString(RES_TITLES[position]));
        TextView tv = (TextView) v.findViewById(R.id.tv_tab_main);
        tv.setText(RES_TITLES[position]);

        return v;
    }

    /**
     * Helper for setting alpha value for all elements of custom view
     *
     * @param tab
     * @param alpha
     */
    public void setAlpha(TabLayout.Tab tab, float alpha) {
        tab.getCustomView().findViewById(R.id.iv_tab_main).setAlpha(alpha);
        tab.getCustomView().findViewById(R.id.tv_tab_main).setAlpha(alpha);
    }
}
