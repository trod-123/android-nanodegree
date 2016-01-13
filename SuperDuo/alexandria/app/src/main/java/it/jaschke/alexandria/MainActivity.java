package it.jaschke.alexandria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import it.jaschke.alexandria.model.Volume;

/**
 * Uses a Navigation DrawerLayout. Follows implementation guide provided by CodePath
 *  url: https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
 * Also followed a static Navigation Drawer tutorial by Derek Woods
 *  url: http://derekrwoods.com/2013/09/creating-a-static-navigation-drawer-in-android/
 */
public class MainActivity extends AppCompatActivity
        implements FetchBooksFragment.ResultSelectionCallback,
        ViewBooksFragment.BookSelectionCallback, ViewBooksFragment.FetchButtonClickedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mDrawerList;

    private boolean TABLET_MODE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set custom toolbar for the appbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_nav);
        setSupportActionBar(mToolbar);

        // Get drawer view (i.e. drawer list) and set it up
        mDrawerList = (NavigationView) findViewById(R.id.nav_view_main);
        setupDrawerContent(mDrawerList);

        // Get drawer layout
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        // Check to see if the device is a phone or tablet by comparing the main content frame's
        //  leftMargin attribute with that loaded in resources. If the left margin is the same as
        //  the width of the drawer, then device is in tablet mode (in phone mode, left margin is 0) -->
        LinearLayout rootView = (LinearLayout) findViewById(R.id.container_content_frame_main);
        if (((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).leftMargin ==
                (int) getResources().getDimension(R.dimen.navigation_drawer_width)) {
            TABLET_MODE = true;
            // Lock the drawer
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mDrawerList);
            mDrawer.setScrimColor(Color.TRANSPARENT);
            // Disable status bar translucency to workaround otherwise grey status bar resulting
            //   from having a translucent status bar style attribute for phone devices
            //   (only for Lollipop and above)
            //   url: http://stackoverflow.com/questions/26702000/change-status-bar-color-with-appcompat-actionbaractivity
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
            }
        } else {
            TABLET_MODE = false;
            // tie drawer layout with the toolbar with drawer toggle
            mDrawerToggle = setupDrawerToggle();
            // TODO: This does not work yet.
            // Make sure drawer is closed upon activity reconstruction
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawer.closeDrawers();
        }

        // Set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Load up the fragment specified by the value of the start screen preference saved in the
        //  shared preferences. Pass in the appropriate menuId to the selectDrawerItem() method
        //  which ultimately creates the fragment
        if (savedInstanceState == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String prefStartScreen = sp.getString(getString(R.string.pref_startScreen_key), getString(R.string.pref_startScreen_default));
            int menuId = -1;
            if (prefStartScreen.equals(getResources().getStringArray(R.array.pref_start_values)[0])) {
                menuId = R.id.nav_fetch_books;
            } else if (prefStartScreen.equals(getResources().getStringArray(R.array.pref_start_values)[1])) {
                menuId = R.id.nav_my_books;
            }
            if (menuId != -1) {
                selectDrawerItem(mDrawerList.getMenu().findItem(menuId));
            } else {
                Log.e(LOG_TAG, "There was an error creating the fragment.");
            }
        }
    }

    // TODO: This is not working. Fix it.
    // This is for animating the hamburger icon, indicating whether drawer is being opened or closed
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        // Complete the "About <APP_NAME>" menu entry title
        navigationView.getMenu().findItem(R.id.nav_about).setTitle(getString(R.string.title_fragment_about, getString(R.string.app_name)));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    /**
     * This method essentially starts the fragments
     * @param menuItem The MenuItem of the fragment to start
     */
    public void selectDrawerItem(MenuItem menuItem) {
        // TODO: Fix the bug where two options can potentially be selected at once
        // Create new fragment and specify which to display based on position. Launch settings
        //  activity if selected.
        Fragment fragment = null;
        PreferenceFragment preferenceFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_my_books:
                fragment = ViewBooksFragment.newInstance();
                break;
            case R.id.nav_fetch_books:
                fragment = FetchBooksFragment.newInstance();
                break;
            case R.id.nav_about:
                fragment = AboutFragment.newInstance();
                break;
            case R.id.nav_settings:
                preferenceFragment = SettingsFragment.newInstance();
                break;
            default:
                fragment = ViewBooksFragment.newInstance();
        }
        String tag;
        if (fragment != null) {
            tag = fragment.getClass().getSimpleName();
        } else {
            tag = preferenceFragment.getClass().getSimpleName();
        }
        // Insert the fragment by replacing any existing fragment. Check for both FragmentManager
        //  and FragmentSupportManager because only the Settings fragment loads via the
        //  FragmentManager
        FragmentManager sfm = getSupportFragmentManager();
        if (sfm.findFragmentByTag(DetailFragment.class.getSimpleName()) != null) {
            // If detail fragment is still loaded, remove it
            sfm.popBackStack();
        }
        if (sfm.findFragmentByTag(tag) != null || getFragmentManager().findFragmentByTag(tag) != null) {
            // TODO: Fix and finish the code below that would allow switching back and forth between
            //  the FetchFragment and the ViewFragment without losing state (or just implement
            //  a Tab layout instead... but the purpose of this exercise was to learn and use the
            //  Navigation DrawerLayout! Next time.)
            // But currently, do nothing if the current fragment is the same as the requested
            //  fragment. It is redundant to load the fragment again when it's already loaded.

//            Log.d(LOG_TAG, "There are " + sfm.getBackStackEntryCount() + " in the backstack.");
//            Log.d(LOG_TAG, "The tag is " + tag);
//            if (sfm.getBackStackEntryCount() > 1) {
//                Log.d(LOG_TAG, "The fragment in position " + (sfm.getBackStackEntryCount() - 2) + " is " + sfm.getBackStackEntryAt(sfm.getBackStackEntryCount() - 1).getName());
//                if (sfm.getBackStackEntryAt(sfm.getBackStackEntryCount() - 2).getName().equals(tag)) {
//                    Log.d(LOG_TAG, "The fragment is already loaded in the backstack. Returning...");
//                    sfm.popBackStack();
//                } else {
//                    Log.d(LOG_TAG, "The fragment is currently loaded on top");
//                }
//            }
        } else {
            if (fragment != null) {
                // Load selected fragment
                sfm.beginTransaction()
                        .replace(R.id.container_fragment_main, fragment, tag)
//                    .addToBackStack(fragment.getClass().getSimpleName())
                        .commit();
                // If preference fragment is still loaded, remove it
                if (getFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName()) != null) {
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName()))
                            .commit();
                }
            } else {
                // Load preference fragment if selected
                getFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment_main, preferenceFragment, tag)
//                        .addToBackStack(preferenceFragment.getClass().getSimpleName())
                        .commit();
                // If there is another fragment still loaded, remove it
                List<Fragment> fragments = sfm.getFragments();
                if (fragments != null) {
                    for (Fragment f : fragments) {
                        if (f != null) {
                            sfm.beginTransaction()
                                    .remove(f)
                                    .commit();
                        }
                    }
                }
            }

            // Highlight the selected item and close drawer. Title is updated in corresponding
            //  fragment's onResume() method for the purposes of the TD to be implemented above
            menuItem.setChecked(true);
        }
        // Only close the drawer if not in tablet mode
        if (!TABLET_MODE) mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!TABLET_MODE) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                // Allow ActionBarToggle to handle events
                return true;
            }
        }
        // Action bar home/up action should open or close drawer
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!TABLET_MODE) {
            // Sync toggle state after onRestoreInstanceState has occurred
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!TABLET_MODE) {
            // Pass any configuration change to the drawer toggles
            mDrawerToggle.onConfigurationChanged(newConfig);

        }
    }


    /*
        Callbacks with content fragments
     */

    // This callback starts the detail activity. This is called within ViewBooksFragment,
    //  which allows communication of which book was selected.
    @Override
    public void onBookItemSelected(String bookId, ViewAdapter.ViewHolder vh) {
        if (!TABLET_MODE) {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.BOOK_ID, bookId);
            startActivity(intent);
        } else {
            Bundle arguments = new Bundle();
            arguments.putString(DetailFragment.BOOK_ID, bookId);
            Fragment fragment = DetailFragment.newInstance(arguments);
            // Start the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_main, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // This callback starts the detail activity. This is called within FetchBooksFragment,
    //  which allows communication of the actual book that was selected and whether the book should
    //  be updated.
    @Override
    public void onResultItemSelected(Volume volume, boolean update, FetchAdapter.ViewHolder vh) {
        if (!TABLET_MODE) {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.VOLUME_OBJECT, volume)
                    .putExtra(DetailFragment.UPDATE_BOOK, update);
            startActivity(intent);
        } else {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.VOLUME_OBJECT, volume);
            arguments.putBoolean(DetailFragment.UPDATE_BOOK, update);
            Fragment fragment = DetailFragment.newInstance(arguments);
            // Start the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_main, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // This callback starts the Fetch fragment. This is called within ViewBooksFragment when the
    //  user clicks on the + FAB.
    @Override
    public void onFetchButtonClicked() {
        selectDrawerItem(mDrawerList.getMenu().findItem(R.id.nav_fetch_books));
    }
}
