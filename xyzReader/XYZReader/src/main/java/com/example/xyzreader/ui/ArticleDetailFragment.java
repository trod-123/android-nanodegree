package com.example.xyzreader.ui;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.util.Toolbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;

//    private int mMutedColor = 0xFF333333;

    @BindView(R.id.scrollview_details)
    NestedScrollView mScrollView;
    @BindView(R.id.app_bar_detail)
    AppBarLayout mAppBar;
    @BindView(R.id.ctoolbar_detail)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.toolbar_detail)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title_details)
    TextView mTv_toolbarTitle;
    @BindView(R.id.container_details_meta)
    LinearLayout mContainerMetaDetails;
    @BindView(R.id.fab_share)
    FloatingActionButton mShareFab;

//    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
//    private ColorDrawable mStatusBarColorDrawable;

    //    private int mTopInset;
//    private View mPhotoContainerView;
    @BindView(R.id.iv_photo_details)
    ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
//    private int mStatusBarFullOpacityBottom;

    boolean mAppBarShowing;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    private FragmentActivity mActivity;

    /**
     * TROD: Note the new fragment is created here, and then arguments are set
     * But this just returns the fragment, it doesn't start it
     * @param itemId
     * @return
     */
    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(ArticleDetailFragment.class.getSimpleName());

        mActivity = getActivity();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
//        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
//                R.dimen.detail_card_top_margin);
    }

    /**
     * Returns the hosting ArticleDetailActivity, if available
     *
     * @return
     */
    private ArticleDetailActivity getDetailActivityHost() {
        if (getActivity() instanceof ArticleDetailActivity) {
            return (ArticleDetailActivity) getActivity();
        } else {
            return null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);

        // Set the toolbar's height dynamically to account for varying bar heights across devices
        int statusBarAppBarHeight = Toolbox.getStatusBarWithActionBarHeight(getActivity());
        mToolbar.getLayoutParams().height = statusBarAppBarHeight;
        mCToolbar.setScrimVisibleHeightTrigger(statusBarAppBarHeight + (int) getResources().getDimension(R.dimen.collapsing_toolbar_scrim_buffer_height));

        // TROD: This seems to be manual implementation of image parallax
        // TODO: But this could also be something that updates a read-progress bar somewhere,
        // probably the status bar
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mScrollY = mScrollView.getScrollY();
//                getDetailActivityHost().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this);
//                mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
//                updateStatusBar();
            }
        });

        // Disable drag callback for app bar, so scrolling is limited to just the scrollview, not
        // the app bar.
        // Source https://stackoverflow.com/questions/40750005/disable-vertical-scroll-in-collapsingtoolbarlayout-appbarlayout/40750707
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams()).setBehavior(behavior);

//        mStatusBarColorDrawable = new ColorDrawable(0);

        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Declare an interface between detail activity and detail fragment so
                // we don't need to keep doing this every time
                if (getDetailActivityHost() != null) {
                    getDetailActivityHost().shareArticle();
                }
            }
        });

        bindViews();
//        updateStatusBar();
        return mRootView;
    }

//    private void updateStatusBar() {
//        int color = 0;
//        if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
//            float f = progress(mScrollY,
//                    mStatusBarFullOpacityBottom - mTopInset * 3,
//                    mStatusBarFullOpacityBottom - mTopInset);
//            color = Color.argb((int) (255 * f),
//                    (int) (Color.red(mMutedColor) * 0.9),
//                    (int) (Color.green(mMutedColor) * 0.9),
//                    (int) (Color.blue(mMutedColor) * 0.9));
//        }
//        mStatusBarColorDrawable.setColor(color);
//        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
//    }

    /**
     * Helper for setting the visibility of app bar contents, as well as the scrolling titles
     *
     * @param show
     */
    private void showAppBar(boolean show) {
        if (show) {
            Toolbox.showView(mTv_toolbarTitle, true, false);
            Toolbox.showView(mContainerMetaDetails, false, false);
            if (getDetailActivityHost() != null) {
                getDetailActivityHost().showOverflowButton(true);
            }
        } else {
            Toolbox.showView(mTv_toolbarTitle, false, false);
            Toolbox.showView(mContainerMetaDetails, true, false);
            if (getDetailActivityHost() != null) {
                getDetailActivityHost().showOverflowButton(false);
            }
        }
        mAppBarShowing = show;
    }

    // TROD: Is this probably setting a progress indicator through the status bar? Which probably
    // fills up as the user scrolls towards the bottom of the page?
    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = mRootView.findViewById(R.id.details_article_title);
        TextView authorView = mRootView.findViewById(R.id.details_article_author);
        TextView dateView = mRootView.findViewById(R.id.details_article_date);
        WebView bodyWebView = mRootView.findViewById(R.id.article_body);
        final ProgressBar pb_body = mRootView.findViewById(R.id.pb_article_body);

        //bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            final String title = mCursor.getString(ArticleLoader.Query.TITLE);
            titleView.setText(title);
            mTv_toolbarTitle.setText(title);
            // show the appbar title only when toolbar is collapsed
            // source https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed
            mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        showAppBar(true);
                    } else if (mAppBarShowing) {
                        showAppBar(false);
                    }
                }
            });

            authorView.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));

            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                dateView.setText(DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL));
            } else {
                // If date is before 1902, just show the string
                dateView.setText(outputFormat.format(publishedDate));
            }

            // Set up the webview client with a progress bar
            WebViewClient wvClient = new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    view.setVisibility(View.INVISIBLE);
                    pb_body.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    view.setVisibility(View.VISIBLE);
                    pb_body.setVisibility(View.GONE);
                }
            };
            bodyWebView.setWebViewClient(wvClient);
            String htmlString = Toolbox.getWebViewContent
                    (getActivity(),
                            mCursor.getString(ArticleLoader.Query.BODY)
                                    .replaceAll("(\r\n|\n)", "<br />"),
                            "Rosario-Regular.ttf",
                            getResources().getDimension(R.dimen.detail_body_text_size), "left",
                            0, 0,
                            Toolbox.getHexColorString(getActivity(), R.color.textColorMedium));
            bodyWebView.loadDataWithBaseURL("file:///android_asset/", htmlString,
                    "text/html", "UTF-8", null);

            // Set up the image and the appbar background color
            mPhotoView.setTransitionName("image" + mItemId);
            Toolbox.loadThumbnailFromUrl(getActivity(), mCursor.getString(ArticleLoader.Query.PHOTO_URL),
                    mPhotoView, new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // begin shared elements transition. needs to be set here as well to
                            // prevent ui from hanging during failure
                            mActivity.supportStartPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            // begin shared elements transition. needs to be set here for once
                            // image is loaded
                            mActivity.supportStartPostponedEnterTransition();
                            mCToolbar.setContentScrimColor(Toolbox.getBackgroundColor(resource,
                                    Toolbox.PaletteSwatch.MUTED,
                                    getResources().getColor(R.color.colorPrimary)));
                            // TODO: This probably was another way of setting the app bar color...
//                                mRootView.findViewById(R.id.meta_bar)
//                                        .setBackgroundColor(mMutedColor);
//                                updateStatusBar();
                            return false;
                        }
                    });
        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            authorView.setText("N/A");
            //bodyView.setText("N/A");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

//    public int getUpButtonFloor() {
//        if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
//            return Integer.MAX_VALUE;
//        }

    // account for parallax
//        return mIsCard
//                ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
//                : mPhotoView.getHeight() - mScrollY;
//    }
}
