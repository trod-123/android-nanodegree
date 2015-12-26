package com.thirdarm.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.thirdarm.footballscores.sync.ScoresSyncAdapter;
import com.thirdarm.footballscores.utilities.Network;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Why is this static? What is this used for?
    // This is for keeping track of which element has been expanded so that upon activity
    //  recreation, that element remains expanded. Used in onSaveInstanceState().
    // It needs to be in the main activity because we are using pagers and the main activity
    //  keeps track of all the pagers and all the elements that are within each pager.
    //  Upon recreation, the activity should also remember which fragment is visible, so that
    //  it also loads that up.
    // TODO: HOWEVER. There is a bug in which if you expand 2 games from 2 different tabs, both
    //  may remain expanded, whereas the intent is that only one remains expanded, when you switch
    //  tabs.
    public static int selected_match_id;

    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";

    private static final int NUM_PAGES = 5;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;


    private ViewPagerAdapter mPagerAdapter;
    private ScoresFragment[] viewFragments = new ScoresFragment[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        // Make sure there is internet connection first before going to sync
        if (Network.isNetworkAvailable(this)) {
            ScoresSyncAdapter.initializeSyncAdapter(this);
        } else {
            Toast.makeText(this, getString(R.string.status_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // This is for restoring the current fragment that is displayed when activity restarts itself
        // Uses a pager handler to handle all the fragments in a single object
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: " + String.valueOf(mViewPager.getCurrentItem()));
        Log.v(save_tag,"selected id: " + selected_match_id);
        outState.putInt("Pager_Current", mViewPager.getCurrentItem());
        outState.putInt("Selected_match", selected_match_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Create 5 fragments, each with its own date assigned to it
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            ScoresFragment newFragment = new ScoresFragment();
            newFragment.setFragmentDate(mformat.format(fragmentdate));
            adapter.addFragment(newFragment, "useless string");
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MainActivity.current_fragment);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
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
            SimpleDateFormat dayFormat = new SimpleDateFormat("MM/dd");
            return day + "\n" + dayFormat.format(dateInMillis);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
