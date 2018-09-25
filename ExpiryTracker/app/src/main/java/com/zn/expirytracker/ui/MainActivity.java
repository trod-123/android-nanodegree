package com.zn.expirytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.zn.expirytracker.R;
import com.zn.expirytracker.settings.SettingsActivity;
import com.zn.expirytracker.utils.Toolbox;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                launchSettings();
                return true;
            case R.id.action_search:
                launchSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == MainPagerAdapter.FRAGMENT_LIST) {
            // Go back to At A Glance if currently in List
            mViewPager.setCurrentItem(MainPagerAdapter.FRAGMENT_AT_A_GLANCE);
        } else {
            super.onBackPressed();
        }
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

    private void launchSettings() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void launchSearch() {
        // TODO: Implement
        Toolbox.showToast(this, "This will launch Search!");
    }
}
