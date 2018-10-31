package com.zn.expirytracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.stephentuso.welcome.WelcomeHelper;
import com.zn.expirytracker.AdStrategy;
import com.zn.expirytracker.R;
import com.zn.expirytracker.constants.Constants;
import com.zn.expirytracker.constants.DebugFields;
import com.zn.expirytracker.constants.KeyConstants;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.firebase.UserMetrics;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.settings.SettingsActivity;
import com.zn.expirytracker.ui.capture.CaptureActivity;
import com.zn.expirytracker.ui.dialog.AddItemInputPickerBottomSheet;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements AddItemInputPickerBottomSheet.OnInputMethodSelectedListener,
        FoodListFragment.FoodListFragmentListener {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.viewPager_main)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout_main)
    TabLayout mTabLayout;
    @BindView(R.id.fab_food_list_add)
    FloatingActionButton mFabAdd;
    @BindView(R.id.root_main)
    View mRootMain;
    @Nullable
    @BindView(R.id.adView_main)
    AdView mAdView;

    MainPagerAdapter mPagerAdapter;
    private boolean mPickerShowing;
    private FoodViewModel mViewModel;
    private int mDatabaseSize = 0;

    WelcomeHelper mWelcomeScreen;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWelcomeScreen.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(MainActivity.class.getSimpleName());

        mWelcomeScreen = new WelcomeHelper(this, IntroActivity.class);
//        mWelcomeScreen.forceShow(); // for debugging only
        mWelcomeScreen.show(savedInstanceState);

        // Set the ad view accordingly
        // https://stackoverflow.com/questions/13323097/in-app-purchase-remove-ads
        if (AdStrategy.areAdsEnabled(this)) {
            setContentView(R.layout.activity_main_ads);
        } else {
            setContentView(R.layout.activity_main);
        }
        ButterKnife.bind(this);

        // Set the logo toolbar
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        // Set up the ad
        if (mAdView != null) {
            AdStrategy.loadAds(mAdView);
        }

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), Toolbox.isLeftToRightLayout());
        mViewPager.setAdapter(mPagerAdapter);
        // Required for RTL layouts, so AAG is always the first tab
        mViewPager.setCurrentItem(MainPagerAdapter.FRAGMENT_AT_A_GLANCE);
        // This needs to be set so tab layout can be linked with view pager. Despite what the
        // documentation says, this is required to be called, otherwise tab layout will have no tabs
        mTabLayout.setupWithViewPager(mViewPager);

        setupTabs();

        if (DebugFields.SHOW_ADD_FAB_IN_MAIN) {
            mFabAdd.setVisibility(View.VISIBLE);
            mFabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInputTypePickerDialog();
                    mFabAdd.hide();
                }
            });
        }

        // For checking database size and showing/hiding the Fab add accordingly
        // By using a ViewModel observer, we could continuously check after user removes items in
        // any way
        if (KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT != Constants.MAX_FOODS_DATABASE_SIZE_NO_LIMIT) {
            mViewModel = obtainViewModel(this);
            mViewModel.getAllFoods(false).observe(this, new Observer<PagedList<Food>>() {
                @Override
                public void onChanged(PagedList<Food> foods) {
                    mDatabaseSize = foods.size();
                    invalidateOptionsMenu();
                    if (mDatabaseSize + 1 > KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT) {
                        Toolbox.showToast(MainActivity.this, getString(
                                R.string.limits_food_storage_hit, KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT));
                        mFabAdd.hide();
                    } else {
                        mFabAdd.show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        if (AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.setPrefsChildEventListener(this);
            FirebaseUpdaterHelper.listenForPrefsTimestampChanges(true, this);
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(true, this);
            // If we're coming from "guest" state, then upload all currently stored foods into
            // Firebase
            if (sp.getBoolean(Constants.AUTH_GUEST, true)) {
                obtainViewModel(this).upload();
                Toolbox.showSnackbarMessage(mRootMain, getString(R.string.message_welcome_sync));
            }
        }
        // Since MainActivity is the gate into the app after SignInActivity, and after,
        // a user signs out or deletes their account, store auth status here to determine
        // a change in auth mode
        sp.edit().putBoolean(Constants.AUTH_GUEST, !AuthToolbox.isSignedIn()).apply();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_database_size);
        if (KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT != Constants.MAX_FOODS_DATABASE_SIZE_NO_LIMIT) {
            item.setTitle(KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT - mDatabaseSize + "");
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_database_size:
                showDatabaseLimitMessage();
                return true;
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
            tabAtAGlance.setCustomView(mPagerAdapter.getTabView(
                    MainPagerAdapter.FRAGMENT_AT_A_GLANCE, this));
            mPagerAdapter.setColor(tabAtAGlance, getResources().getColor(R.color.tab_icon_selected));
        } else {
            Timber.e("MainActivity/At a glance tab was null! Not setting tab elements...");
        }
        TabLayout.Tab tabList = mTabLayout.getTabAt(MainPagerAdapter.FRAGMENT_LIST);
        if (tabList != null) {
            tabList.setCustomView(mPagerAdapter.getTabView(
                    MainPagerAdapter.FRAGMENT_LIST, this));
            mPagerAdapter.setColor(tabList, getResources().getColor(R.color.tab_icon_unselected));
        } else {
            Timber.e("MainActivity/List tab was null! Not setting tab elements...");
        }

        if (!DebugFields.SHOW_ADD_FAB_IN_MAIN) {
            TabLayout.Tab tabCapture = mTabLayout.getTabAt(MainPagerAdapter.ACTIVITY_CAPTURE);
            if (tabCapture != null) {
                tabCapture.setCustomView(mPagerAdapter.getTabView(
                        MainPagerAdapter.ACTIVITY_CAPTURE, this));
                mPagerAdapter.setColor(tabCapture, getResources().getColor(R.color.tab_icon_unselected));
            } else {
                Timber.e("MainActivity/Capture tab was null! Not setting tab elements...");
            }

            // Prevent view pager from showing Capture tab when tab is clicked
            ((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(MainPagerAdapter.ACTIVITY_CAPTURE).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!mPickerShowing)
                            showInputTypePickerDialog();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        // Change color of icons by selection
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPagerAdapter.setColor(tab, getResources().getColor(R.color.tab_icon_selected));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mPagerAdapter.setColor(tab, getResources().getColor(R.color.tab_icon_unselected));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showDatabaseLimitMessage() {
        String message = mDatabaseSize != KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT ?
                getString(R.string.limits_food_storage_no_hit, KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT,
                        KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT - mDatabaseSize) :
                getString(R.string.limits_food_storage_hit, KeyConstants.MAX_FOODS_DATABASE_SIZE_DEFAULT);
        Toolbox.showSnackbarMessage(mRootMain, message);
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

    // region Input type picker dialog

    private void showInputTypePickerDialog() {
        updatePickerState(true);
        // Show the bottom sheet
        AddItemInputPickerBottomSheet bottomSheet = new AddItemInputPickerBottomSheet();
        bottomSheet.show(getSupportFragmentManager(),
                AddItemInputPickerBottomSheet.class.getSimpleName());
    }

    @Override
    public void onCameraInputSelected() {
        // Ensure device has camera activity to handle this first
        if (Toolbox.checkCameraHardware(this)) {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivity(intent);
        } else {
            Timber.d("Attempted to start Capture, but device does not have a camera");
            Toolbox.showSnackbarMessage(mRootMain, getString(R.string.message_camera_required));
        }
        updatePickerState(false);
    }

    @Override
    public void onTextInputSelected() {
        UserMetrics.incrementUserTextOnlyInputCount();
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
        updatePickerState(false);
    }

    @Override
    public void onCancelled() {
        updatePickerState(false);
    }

    private void updatePickerState(boolean pickerShowing) {
        mPickerShowing = pickerShowing;
        if (DebugFields.SHOW_ADD_FAB_IN_MAIN) {
            if (pickerShowing) {
                mFabAdd.hide();
            } else {
                mFabAdd.show();
            }
        }
    }

    // endregion

    // region "Undelete" Snackbars

    /**
     * For removing old callbacks
     */
    private Snackbar mCurrentSnackbar;

    /**
     * For replacing the current Snackbar's callback, ensuring callbacks do not stack
     */
    private Snackbar.Callback mDismissCallback;

    @Override
    public void onFoodSwiped(String message, View.OnClickListener actionListener,
                             Snackbar.Callback dismissCallback) {
        mCurrentSnackbar = Snackbar.make(mRootMain, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), actionListener).addCallback(mDismissCallback = dismissCallback);
        mCurrentSnackbar.show();

    }

    @Override
    public void onSnackbarDismissed() {
        mCurrentSnackbar.removeCallback(mDismissCallback);
    }

    // endregion
}
