package com.example.xyzreader.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.util.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private int mCurrentPagerPosition;

    @BindView(R.id.pager)
    ViewPager mPager;
    private DetailPagerAdapter mPagerAdapter;
    @BindView(R.id.appbar_container_detail)
    Toolbar mAppBarContainer;
    @BindView(R.id.gap_status_bar)
    View mStatusBarGap;
    @BindView(R.id.ib_action_up)
    ImageButton mUpButton;
    @BindView(R.id.ib_action_menu)
    ImageButton mOverflowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);
        Timber.tag(ArticleDetailActivity.class.getSimpleName());
        setUpStatusAppBar();

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // TROD: This allows the up button to fade in and out as the page changes
                // (up button still works even while it's faded out for the time being)
                Toolbox.showView(mUpButton, state == ViewPager.SCROLL_STATE_IDLE, false);

                // TODO: This is not being called at the right time...
                boolean isShowing = ((ArticleDetailFragment) mPagerAdapter.getItem(mCurrentPagerPosition))
                        .mAppBarShowing;
                // TODO: Need an additional indicator for when to hide this. Cuz it hides even if
                // app bar is visible
                Toolbox.showView(mOverflowButton,
                        state == ViewPager.SCROLL_STATE_IDLE && isShowing
                        , true);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                    mCurrentPagerPosition = position;
//                    updateUpButtonPosition();
                } else {
                    Timber.e("Cursor is null when paging");
                }
            }
        });

        mAppBarContainer = findViewById(R.id.appbar_container_detail);

        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
        mOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbox.showArticleActionsMenuPopup(ArticleDetailActivity.this, v,
                        mCursor, mCurrentPagerPosition);
            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    /**
     * Helper method for interfacing with ArticleDetailFragment
     */
    public void shareArticle() {
        Toolbox.shareArticle(ArticleDetailActivity.this,
                mCursor, mCurrentPagerPosition);
    }

    /**
     * Helper for making the status bar translucent and setting the action bar layout
     */
    private void setUpStatusAppBar() {
        // Makes the status bar translucent (you can also set the status bar color in this way)
        // Source: https://stackoverflow.com/questions/26702000/change-status-bar-color-with-appcompat-actionbaractivity
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // By doing the above, this means the toolbar will get drawn behind the status bar. The
        // below code fixes that, but it results in no views behind the status bar, so the status
        // bar could be black or white background before the views scroll up behind it
//        mAppBar.setPadding(0, Toolbox.getStatusBarHeight(getResources()), 0, 0);

        // fill-in the empty view space from the translucent status bar
        mStatusBarGap.getLayoutParams().height = Toolbox.getStatusBarHeight(this);
        setSupportActionBar(mAppBarContainer);
        // From https://stackoverflow.com/questions/7655874/how-do-you-remove-the-title-text-from-the-android-actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Helper function for showing and hiding the action overflow button
     *
     * @param show
     */
    public void showOverflowButton(boolean show) {
        Toolbox.showView(mOverflowButton, show, true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    // TODO: This method seems to throw errors
//    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
//        if (itemId == mSelectedItemId) {
//            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//            updateUpButtonPosition();
//        }
//    }
//
//    private void updateUpButtonPosition() {
//        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
//        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
//    }

    private class DetailPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public DetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
//                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//                updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
