package com.thirdarm.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;

import com.thirdarm.footballscores.sync.ScoresSyncAdapter;
import com.thirdarm.footballscores.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import klogi.com.RtlViewPager;

public class MainActivity extends AppCompatActivity {
    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";

    private static final int NUM_PAGES = 5;

    private Toolbar mToolbar;
    public ViewPager mViewPager;

    public static String[] mFragmentDates = new String[NUM_PAGES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (RtlViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Make sure there is internet connection first before going to sync
        ScoresSyncAdapter.initializeSyncAdapter(this);

//        if (Network.isNetworkAvailable(this)) {
//            ScoresSyncAdapter.initializeSyncAdapter(this);
//        } else {
//            Toast.makeText(this, getString(R.string.status_no_internet), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // This is for restoring the current fragment that is displayed when activity restarts itself
        // Uses a pager handler to handle all the fragments in a single object
        outState.putInt("Pager_Current", mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        current_fragment = savedInstanceState.getInt("Pager_Current");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d(LOG_TAG, "In setupViewPager");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Create 5 fragments, each with its own date assigned to it
        for (int i = 0; i < NUM_PAGES; i++) {
            String date = Utilities.getUserDate(System.currentTimeMillis() + ((i - 2) * 86400000));
            ScoresFragment newFragment = new ScoresFragment();
            newFragment.setFragmentDate(date);
            mFragmentDates[i] = date;
            newFragment.setFragmentIndex(i);
            adapter.addFragment(newFragment, "useless string");
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MainActivity.current_fragment);
        // Allow all 5 fragments to be held by the system at once to avoid recreating fragments.
        // Was stuck trying to restore fragments after they were being recreated because
        //  fragmentdate would always be returning null for the fragment that has not yet been
        //  loaded, causing the app to crash because fragmentdate is used as part of a selection
        //  argument in the cursor. For example, from app start, moving to the left/right
        //  fragment, rotating twice, and then trying to move back to the home fragment causes
        //  the app to crash. I have been at it for hours trying to fix this, but it seems that
        //  even trying to use savedInstanceStates still caused fragmentdate to become a null
        //  value. Retaining the fragments in this way helps to alleviate that issue, but at the
        //  cost of performance.
        viewPager.setOffscreenPageLimit(4);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(getApplicationContext(), System.currentTimeMillis() + ((position - 2) * 86400000));
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            String day;
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                day = context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                day = context.getString(R.string.tomorrow);
            }
            else if ( julianDay == currentJulianDay -1) {
                day = context.getString(R.string.yesterday);
            }
            else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                day = dayFormat.format(dateInMillis);
            }
            SimpleDateFormat dayFormat = new SimpleDateFormat("M/d");
            return day + "\n" + dayFormat.format(dateInMillis);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
