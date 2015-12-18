package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment {

    private static final String LOG_TAG = PagerFragment.class.getSimpleName();

    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private ScoresFragment[] viewFragments = new ScoresFragment[5];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On CREATEVIEW FOR VIEWPAGER");
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);

        // getChildFragmentManager() is used if it is a fragment that will contain all the view
        //  pager's fragments. Use getSupportFragmentManager() if the hosting class is an activity
        //  instead of a fragment.
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());

        // TODO: Fix bug where dates are not perfectly aligned with their respective fragment, and
        //  where the current - 2 and current + 2 day does not have dates printed out
        // OBSERVATION: IT SEEMS THAT THE DATES THAT ARE BEING LOGGED OR TOASTED ARE THOSE FOR THE
        //  FRAGMENTS WHICH HAVE RELOADED. The pager loads fragments to the left and right of the
        //  current fragment. So it loads at most 3 at once. The fragment that is at least 2 away
        //  ends up getting destroyed, and is re-created when it returns to the 1-1 threshold.
        //  When that fragment is re-created, the dates are logged and toasted because it returns
        //  to onCreateView() being called for that fragment.
        // It is not that the dates are non existant in the endpoint fragments; it's just that their
        //  onCreateView() methods have already been called in the fragment next to it.
        // BUT THIS DOES NOT EXPLAIN WHY CURSOR ELEMENTS ARE NOT LOADING!!!

        // Create 5 fragments, each with its own date assigned to it
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new ScoresFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }

        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        return rootView;
    }

    // This class implements swipe views, using the FragmentStatePagerAdapter, which should be
    //  used when there is an unknown amount of fragments to be loaded into the swipe views
    //  (Use FragmentPagerAdapter when there is a fixed, small number of pages).
    // It contains all the methods necessary to run the Pager Adapter to the minimum implementation
    private class myPageAdapter extends FragmentStatePagerAdapter {

        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1) {
                return context.getString(R.string.yesterday);
            }
            else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
