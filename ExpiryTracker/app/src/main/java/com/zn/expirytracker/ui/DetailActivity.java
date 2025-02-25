package com.zn.expirytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zn.expirytracker.AdStrategy;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity
        implements DetailFragment.DetailFragmentListener {

    public static final String ARG_ITEM_ID_LONG = Toolbox.createStaticKeyString(
            DetailActivity.class, "item_id_long");

    /**
     * Override onBackPressed() to simulate going back to MainActivity if user got here via
     * widget or notification
     */
    public static final String EXTRA_LAUNCHED_EXTERNALLY = Toolbox.createStaticKeyString(
            DetailActivity.class, "launched_externally");

    /**
     * For handling deletes via {@link EditActivity}
     */
    public static final int RC_EDIT = 13;
    public static final int RESULT_DELETED = 14;

    private static final String KEY_CURRENT_POSITION_INT = Toolbox.createStaticKeyString(
            DetailActivity.class, "current_position_int");
    private static final String KEY_INITIALIZED_BOOL = Toolbox.createStaticKeyString(
            DetailActivity.class, "initialized_bool");

    @BindView(R.id.container_detail_activity)
    View mRootView;
    @BindView(R.id.viewPager_detail)
    NonSwipeableViewPager mViewPager;
    @BindView(R.id.fab_detail_edit)
    FloatingActionButton mFabEdit;
    @Nullable
    @BindView(R.id.adView_detail)
    AdView mAdView;

    private DetailPagerAdapter mPagerAdapter;
    private FoodViewModel mViewModel;
    private List<Food> mFoodsList; // sync'd with pager adapter in onChanged()
    private Food mSoftDeletedItem;

    /**
     * For setting up the first page the user sees
     */
    private long mLaunchedItemId = 0;

    /**
     * Keep track of the current item position for when the activity is reloaded
     */
    private int mCurrentPosition;

    /**
     * Only set the position by launchedItemId when loaded for the first time
     */
    private boolean mInitialized;

    /**
     * If the user got here by widget or notification, override onBackPressed() to take the user
     * to MainActivity, mimicking a "fake" backstack to simulate a unified app experience
     */
    private boolean mLaunchedExternally;

    /**
     * Once pager views are set, don't allow recreating them unless, for instance, a food item is
     * deleted. If a food item is updated, avoid its recreation from ViewModel observer to prevent
     * the UI flashing from refreshing (individual fragments can still get updated because they
     * have their own ViewModel observers)
     * <p>
     * New foods aren't supposed to be added here, so the only thing that changes the pager's
     * data structure is via deleting food items. This should be fine enough for our purposes
     */
    private boolean mAllowRecreatingFragments = true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_POSITION_INT, mCurrentPosition);
        outState.putBoolean(KEY_INITIALIZED_BOOL, mInitialized);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(DetailActivity.class.getSimpleName());

        // Set the ad view accordingly
        // https://stackoverflow.com/questions/13323097/in-app-purchase-remove-ads
        if (AdStrategy.areAdsEnabled(this)) {
            setContentView(R.layout.activity_detail_ads);
        } else {
            setContentView(R.layout.activity_detail);
        }
        ButterKnife.bind(this);

        // Set up the ad
        if (mAdView != null) {
            AdStrategy.loadAds(mAdView);
        }

        Intent intent = getIntent();
        if (intent != null) {
            mLaunchedItemId = intent.getLongExtra(ARG_ITEM_ID_LONG, 0);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mLaunchedExternally = extras.containsKey(EXTRA_LAUNCHED_EXTERNALLY);
            }
        }

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION_INT);
            mInitialized = savedInstanceState.getBoolean(KEY_INITIALIZED_BOOL);
        }

        mViewModel = obtainViewModel(this);
        mViewModel.getAllFoods(false).observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                if (foods != null) {
                    // Don't do anything if foods is null
                    if (mAllowRecreatingFragments) {
                        List<Food> foodsList = new ArrayList<>(foods); // required for reversing list
                        if (foodsList.size() == 0) {
                            // If there are no more foods, close Details
                            finish();
                        }
                        if (!Toolbox.isLeftToRightLayout()) {
                            // Reverse foods list for RTL layout
                            Collections.reverse(foodsList);
                        }
                        mPagerAdapter.setFoodsList(foodsList);
                        // needs to be called here, and not in adapter, to invalidate views
                        mPagerAdapter.notifyDataSetChanged();
                        if (!mInitialized) {
                            mViewPager.setCurrentItem(
                                    DataToolbox.getFoodPositionFromId(foodsList, mLaunchedItemId),
                                    false);
                            mInitialized = true;
                        } else {
                            mViewPager.setCurrentItem(mCurrentPosition, false);
                        }
                        // There is a jitter bug in scrolling if there is only one page. This should fix
                        mViewPager.setPagingEnabled(foodsList.size() > 1);
                        mFoodsList = foodsList;

                        mAllowRecreatingFragments = false;
                    }
                }
            }
        });

        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.detail_pager_page_margin));
        mViewPager.setAdapter(mPagerAdapter);

        // Keep track of the user's current page
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Edit fab
        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity(mFoodsList.get(mViewPager.getCurrentItem()).get_id());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(true, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(false, this);
        }
    }

    public void startEditActivity(long itemId) {
        Intent intent = new Intent(DetailActivity.this, EditActivity.class);
        intent.putExtra(EditFragment.ARG_ITEM_ID_LONG, itemId);
        startActivityForResult(intent, RC_EDIT);
    }

    @Override
    public void onEditItem(long itemId) {
        startEditActivity(itemId);
    }

    /**
     * For sharing the ViewModel across child fragments. This prevents child fragments from having
     * to create their own ViewModels, avoiding redundancy
     *
     * @param activity
     * @return
     */
    public static FoodViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(FoodViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Emulate the back button when pressing the up button, to prevent parent activity from
            // getting recreated
            // https://stackoverflow.com/questions/22947713/make-the-up-button-behave-like-the-back-button-on-android
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLaunchedExternally) {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Keep focus on the current food item whose date was modified
     *
     * @param foodId
     */
    @Override
    public void onDateChanged(long foodId) {
        mAllowRecreatingFragments = true;
        mInitialized = false;
        mLaunchedItemId = foodId;
    }

    /**
     * For removing old callbacks
     */
    private Snackbar mCurrentSnackbar;

    /**
     * For replacing the current Snackbar's callback, ensuring callbacks do not stack
     */
    private Snackbar.Callback mDismissCallback;

    /**
     * Deletes the food item at the current position of the adapter
     */
    @Override
    public void onDeleteItem() {
        if (mSoftDeletedItem != null) {
            // Remove the cache, there can only be one soft deleted item at most
            deleteSoftDeleted();
        }
        mSoftDeletedItem = mFoodsList.get(mCurrentPosition);
        // Don't delete the images from Storage just yet
        mViewModel.delete(true, false, mSoftDeletedItem);
        mAllowRecreatingFragments = true;
        mCurrentSnackbar = Snackbar.make(mRootView, getString(R.string.message_item_removed,
                mSoftDeletedItem.getFoodName()), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreSoftDeleted();
                        mAllowRecreatingFragments = true;
                    }
                })
                .addCallback(mDismissCallback = new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            // No action = delete confirmed. Remove images on Firebase Storage
                            deleteSoftDeleted();
                        }
                    }
                });
        mCurrentSnackbar.show();
    }

    /**
     * Restores the soft deleted item
     */
    private void restoreSoftDeleted() {
        mViewModel.insert(true, mSoftDeletedItem);
        mSoftDeletedItem = null;
    }

    /**
     * Permanently deletes the soft deleted item and removes the cache and the existing Snackbar
     * callback
     */
    private void deleteSoftDeleted() {
        mViewModel.deleteImages(true,
                mSoftDeletedItem.getImages(), mSoftDeletedItem.get_id());
        mSoftDeletedItem = null;
        // Ensure there are no outstanding Snackbar dismiss callbacks
        mCurrentSnackbar.removeCallback(mDismissCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_EDIT && resultCode == RESULT_DELETED) {
            mAllowRecreatingFragments = true;
        }
    }
}
