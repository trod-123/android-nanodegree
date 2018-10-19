package com.zn.expirytracker.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.settings.SettingsActivity;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DataToolbox;
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
        Timber.tag(MainActivity.class.getSimpleName());
        ButterKnife.bind(this);

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), Toolbox.isLeftToRightLayout());
        mViewPager.setAdapter(mPagerAdapter);
        // Required for RTL layouts, so AAG is always the first tab
        mViewPager.setCurrentItem(MainPagerAdapter.FRAGMENT_AT_A_GLANCE);
        // This needs to be set so tab layout can be linked with view pager. Despite what the
        // documentation says, this is required to be called, otherwise tab layout will have no tabs
        mTabLayout.setupWithViewPager(mViewPager);

        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.setPrefsChildEventListener(this);
            FirebaseUpdaterHelper.listenForPrefsTimestampChanges(true, this);
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(true, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.listenForPrefsTimestampChanges(false, this);
            FirebaseUpdaterHelper.listenForPrefsChanges(false);
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(false, this);
        }
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
            case R.id.action_share:
                shareApp();
                return true;
            // TODO: Hide for now
//            case R.id.action_search:
//                launchSearch();
//                return true;
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
            tabAtAGlance.setText(R.string.fragment_at_a_glance_name)
                    .setIcon(R.drawable.ic_chart_bar_white_24dp)
                    .setContentDescription(R.string.fragment_at_a_glance_name);
            tabAtAGlance.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        } else {
            Timber.e("MainActivity/At a glance tab was null! Not setting tab elements...");
        }
        TabLayout.Tab tabList = mTabLayout.getTabAt(MainPagerAdapter.FRAGMENT_LIST);
        if (tabList != null) {
            tabList.setText(R.string.fragment_food_list_name)
                    .setIcon(R.drawable.ic_format_list_bulleted_white_24dp)
                    .setContentDescription(R.string.fragment_food_list_name);
            tabList.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            tabList.getIcon().setAlpha(128);
        } else {
            Timber.e("MainActivity/List tab was null! Not setting tab elements...");
        }

        // Change color of icons by selection
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(255);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(128);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void launchSettings() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void shareApp() {
        Pair<String, String> emoji = DataToolbox.getRandomAnimalEmojiNamePair();
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text,
                        emoji.first, emoji.second))
                .setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));
    }

    /**
     * Allows this ViewModel instance to be bound to MainActivity, and allow it to be shared
     * across AAG and List Fragments
     *
     * @param activity
     * @return
     */
    public static FoodViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(FoodViewModel.class);
    }

    private void launchSearch() {
        // TODO: Implement
        Toolbox.showToast(this, "This will launch Search!");
    }
}
