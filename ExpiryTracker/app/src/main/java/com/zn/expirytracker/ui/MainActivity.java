package com.zn.expirytracker.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zn.expirytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewPager_main)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout_main)
    TabLayout mTabLayout;

    MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        // This needs to be set so tab layout can be linked with view pager. Despite what the
        // documentation says, this is required to be called, otherwise tab layout will have no tabs
        mTabLayout.setupWithViewPager(mViewPager);

        setupTabs();
    }

    /**
     * Helper for setting up tab decor
     */
    private void setupTabs() {
        TabLayout.Tab tabAtAGlance = mTabLayout.getTabAt(MainPagerAdapter.FRAGMENT_AT_A_GLANCE);
        if (tabAtAGlance != null) {
            tabAtAGlance.setText(R.string.fragment_at_a_glance_name);
            tabAtAGlance.setIcon(R.drawable.ic_chart_bar_white_24dp);
        } else {
            Timber.e("At a glance tab was null! Not setting tab elements...");
        }
        TabLayout.Tab tabList = mTabLayout.getTabAt(MainPagerAdapter.FRAGMENT_LIST);
        if (tabList != null) {
            tabList.setText(R.string.fragment_food_list_name);
            tabList.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
        } else {
            Timber.e("List tab was null! Not setting tab elements...");
        }


        // For changing selected icon color in TabLayout
        // https://stackoverflow.com/questions/34562117/how-do-i-change-the-color-of-icon-of-the-selected-tab-of-tablayout
    }
}
