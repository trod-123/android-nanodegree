package com.example.xyzreader.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_HAS_SHARED_ELEMENTS = "has_shared_elements";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    Activity mHostActivity;
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;

    @BindView(R.id.scrollview_details)
    NestedScrollView mScrollView;
    @BindView(R.id.detail_appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.detail_collapse_toolbar)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.details_gap_status_bar)
    View mStatusBarGap;
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_toolbar_title)
    TextView mTv_toolbarTitle;
    @BindView(R.id.ib_action_up)
    ImageButton mUpButton;
    @BindView(R.id.ib_action_menu)
    ImageButton mOverflowButton;
    @BindView(R.id.fab_share)
    FloatingActionButton mShareFab;

    // Details views
    @BindView(R.id.detail_photo)
    ImageView mPhotoView;
    @BindView(R.id.detail_photo_scrim)
    View mPhotoScrim;
    @BindView(R.id.container_details_meta)
    LinearLayout mContainerMetaDetails;
    @BindView(R.id.details_article_title)
    TextView mTitleView;
    @BindView(R.id.details_article_author)
    TextView mAuthorView;
    @BindView(R.id.details_article_date)
    TextView mDateView;
    @BindView(R.id.article_body)
    WebView mBodyWebView;
    @BindView(R.id.pb_article_body)
    ProgressBar mBodyPb;

    // Temp details views (for animations)
    @BindView(R.id.detail_temp_container)
    FrameLayout mTempDetailsContainer;
    @BindView(R.id.details_temp_gap_status_bar)
    View mTempStatusBarGap;
    @BindView(R.id.detail_temp_photo)
    ImageView mTempPhotoView;
    @BindView(R.id.details_temp_article_title)
    TextView mTempTitleView;
    @BindView(R.id.details_temp_article_author)
    TextView mTempAuthorView;
    @BindView(R.id.details_temp_article_date)
    TextView mTempDateView;
    @BindView(R.id.ib_temp_action_up)
    ImageButton mTempUpButton;

    private boolean mIsCard = false;

    // For knowing when to show or hide the app bar
    boolean mAppBarShowing;

    // For knowing whether this fragment has shared elements - when the fragment is loaded,
    // hide the "temp" container if false
    boolean mLaunchedWithSharedElements = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    /**
     * TROD: Note the new fragment is created here, and then arguments are set
     * But this just returns the fragment, it doesn't start it
     *
     * @param itemId
     * @param hasSharedElements
     * @return
     */
    public static ArticleDetailFragment newInstance(long itemId, boolean hasSharedElements) {
        Bundle arguments = new Bundle();
        // The point of setting this is so the fragment knows which id to load into the cursor
        arguments.putLong(ARG_ITEM_ID, itemId);
        arguments.putBoolean(ARG_HAS_SHARED_ELEMENTS, hasSharedElements);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(ArticleDetailFragment.class.getSimpleName());

        mHostActivity = getActivity();

        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_ITEM_ID)) {
                mItemId = getArguments().getLong(ARG_ITEM_ID);
            }
            if (getArguments().containsKey(ARG_HAS_SHARED_ELEMENTS)) {
                mLaunchedWithSharedElements = getArguments().getBoolean(ARG_HAS_SHARED_ELEMENTS);
            }
        }

        mIsCard = mHostActivity.getResources().getBoolean(R.bool.detail_is_card);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);

        if (savedInstanceState != null) {
            // if user returns to original fragment with shared elements after it's been
            // recycled (e.g. via pagination, or config changes), continue hiding the temp container
            mTempDetailsContainer.setVisibility(View.GONE);

            // In order for shared element transition to work, only one view can have the
            // correct transition name, so set the temp to null since we're not showing it
            mPhotoView.setTransitionName("image" + mItemId);
            mTempPhotoView.setTransitionName(null);
            showFab(true);
        } else {
            if (!mLaunchedWithSharedElements) {
                mTempDetailsContainer.setVisibility(View.GONE);
                // Remap the shared animation to the app bar photo view so source shared animation
                // correctly reflects app bar scroll changes
                mPhotoView.setTransitionName("image" + mItemId);

                // anchor and show the fab in the right place (since this isn't done in xml)
                showFab(true);
            } else {
                // Shared element transition is in progress, expecting the temp photo view
                mTempPhotoView.setTransitionName("image" + mItemId);
            }
        }

        setupStatusAppBar();

        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since the detail cursor contains only the article, pass in 0 for position
                Toolbox.shareArticle(mHostActivity, mCursor, 0, mRootView);
            }
        });

        setupDetailsUI();

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    // TODO: Create a listener between pager and detail so pager isn't calling detail directly

    public void onEnterTransitionStarted() {
        // "Lift" the temp container only for transitions, so transitions are visible. This is
        // required since app bar is elevated a bit. Setting elevation here instead of in xml
        // avoids blocking toolbar click handling  for other fragments
        mTempDetailsContainer.setElevation(mHostActivity.getResources()
                .getDimensionPixelSize(R.dimen.app_bar_elevation));

        // These will already be populated when transitions start, so hide them for now so the
        // temp views can create the animation illusion
        mPhotoScrim.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mContainerMetaDetails.setVisibility(View.GONE);

        // Hide the fab upon entry. Done here instead of in xml to keep fab showing in other pages
        mShareFab.setVisibility(View.GONE);
        showFab(false);
    }

    /**
     * Hide the temp views and make their real counterparts visible
     */
    public void onEnterTransitionFinished() {
        mTempDetailsContainer.setVisibility(View.GONE);

        // Display these once the temp container finished its animations, so that the user can
        // see them respond to scroll events
        mPhotoScrim.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
        mContainerMetaDetails.setVisibility(View.VISIBLE);

        showFab(true);
    }

    /**
     * Hide the real photo view while the temp is animating
     */
    public void onSharedElementEnterTransitionStarted() {
        mPhotoView.setVisibility(View.GONE);
    }

    /**
     * "Hide" the animated photo and show the real photo
     */
    public void onSharedElementEnterTransitionFinished() {
        mTempPhotoView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));

        // NOTE: The below won't work until all animations are done, including any enter transitions.
        // As a workaround, we're "clearing" the image from the temp view. Saving here for reference
//        mTempPhotoView.setVisibility(View.GONE);

        mPhotoView.setVisibility(View.VISIBLE);

        // Remap the shared animation to the app bar photo view so source shared animation
        // correctly reflects app bar scroll changes.
        // Also remove the transition name for the temp view. If 2 views have the same transition
        // name, then this will not work!
        mPhotoView.setTransitionName("image" + mItemId);
        mTempPhotoView.setTransitionName(null);
    }

    public void onReturnTransitionStarted() {
        // When coming back, it is not necessary to bring the temp photo view back to visibility
        // Smoke and mirrors are used to "share" the images, so the image that we see resizing
        // is actually from the image view in the list fragment. That said we can keep the temp
        // photoview gone
//        mPhotoView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        mTempPhotoView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));

        // TODO: This doesn't seem to work...
        //showFab(false);
    }

    /**
     * Helper for making the status bar translucent and setting the action bar layout
     */
    private void setupStatusAppBar() {
        // Remove title from action bar, and let the toolbar title text view take care of it
        // From https://stackoverflow.com/questions/7655874/how-do-you-remove-the-title-text-from-the-android-actionbar
        ((AppCompatActivity) mHostActivity).setSupportActionBar(mToolbar);
        ((AppCompatActivity) mHostActivity).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set padding and margins programmatically
        // Since we have a status bar "gap view" we just need to set the height for the gap view,
        // as well as the margins for the action bar, to match the system defined status bar height
        // https://stackoverflow.com/questions/12728255/in-android-how-do-i-set-margins-in-dp-programmatically
        int statusBarHeight = Toolbox.getStatusBarHeight(mHostActivity);
        int statusAppBarHeight = Toolbox.getStatusBarWithActionBarHeight(mHostActivity);
        CollapsingToolbarLayout.LayoutParams params =
                (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
        mStatusBarGap.getLayoutParams().height = statusBarHeight;
        mTempStatusBarGap.getLayoutParams().height = statusBarHeight;
        params.setMargins(0, statusBarHeight, 0, 0);
        mToolbar.setLayoutParams(params);

        // Adjust the scrim trigger to activate when the height is on the verge of matching the
        // combined status bar and app bar height, since view is rendered from where the status
        // bar is, downward. If we did not have translucent status bar, then this would just be
        // the app bar height instead
        mCToolbar.setScrimVisibleHeightTrigger(statusAppBarHeight +
                (int) mHostActivity.getResources().getDimension(R.dimen.collapsing_toolbar_scrim_buffer_height));

        // Disable drag callback for app bar, so scrolling is limited to just the scrollview, not
        // the app bar.
        // Source https://stackoverflow.com/questions/40750005/disable-vertical-scroll-in-collapsingtoolbarlayout-appbarlayout/40750707
//        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
//        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
//            @Override
//            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
//                return false;
//            }
//        });
//        ((CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams()).setBehavior(behavior);

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                // fade in and out the meta container depending on app bar size
                float alpha = -1f * verticalOffset / scrollRange;
                mContainerMetaDetails.setAlpha(Toolbox.decelerateInterpolator(alpha));

                // show the appbar title only when toolbar is collapsed
                // source https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed
                if (scrollRange + verticalOffset == 0) {
                    showAppBar(true);
                } else if (mAppBarShowing) {
                    showAppBar(false);
                }
            }
        });

        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is required for preventing previous activity from being recreated needlessly
                // Shared elements transition will also not work unless we call this, instead of
                // onSupportNavigateUp();
                mHostActivity.onBackPressed();
            }
        });
        mOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since the detail cursor contains only the article, pass in 0 for position
                Toolbox.showArticleActionsMenuPopup(mHostActivity, v, mCursor, 0);
            }
        });
    }

    // Not possible to hide fab in xml, so we're doing it here
    // https://stackoverflow.com/questions/36540951/any-way-to-hide-fabfloating-action-button-via-xml
    // https://stackoverflow.com/questions/31269958/floatingactionbutton-doesnt-hide
    public void showFab(boolean show) {
        // Setting fab layout anchor at runtime
        // https://stackoverflow.com/questions/31596589/set-layout-anchor-at-runtime-on-floatingactionbutton
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mShareFab.getLayoutParams();
        params.setAnchorId(View.NO_ID);
        // Get current location of a view
        // https://stackoverflow.com/questions/2224844/how-to-get-the-absolute-coordinates-of-a-view
        int[] location = new int[2];
        mShareFab.getLocationInWindow(location);
        mShareFab.setLayoutParams(params);
        if (show) {
            //mShareFab.setVisibility(View.VISIBLE); // Adding this will break the animation
            mShareFab.show();
            params.setAnchorId(R.id.detail_appbar);
            mShareFab.setLayoutParams(params);
        } else {
            // TODO: This doesn't seem to work
            // Dynamically set position of view
            // https://stackoverflow.com/questions/6535648/how-can-i-dynamically-set-the-position-of-view-in-android
            mShareFab.setTranslationX(location[0]);
            mShareFab.setTranslationY(location[1]);
            mShareFab.hide();
        }
    }

    /**
     * Helper method that sets up the UI details
     */
    private void setupDetailsUI() {
        if (mRootView == null) {
            return;
        }

        if (mCursor != null) {
            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            mTitleView.setText(title);
            mTempTitleView.setText(title);
            mTv_toolbarTitle.setText(title);

            String author = mCursor.getString(ArticleLoader.Query.AUTHOR);
            mAuthorView.setText(author);
            mTempAuthorView.setText(author);

            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                CharSequence date = DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL);
                mDateView.setText(date);
                mTempDateView.setText(date);
            } else {
                // If date is before 1902, just show the string
                String date = outputFormat.format(publishedDate);
                mDateView.setText(date);
                mTempDateView.setText(date);
            }

            // Set up the webview client with a progress bar
            WebViewClient wvClient = new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    view.setVisibility(View.INVISIBLE);
                    mBodyPb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    view.setVisibility(View.VISIBLE);
                    mBodyPb.setVisibility(View.GONE);
                }
            };
            mBodyWebView.setWebViewClient(wvClient);
            String htmlString = Toolbox.getWebViewContent
                    (mHostActivity,
                            Toolbox.formatArticleBodyString(
                                    mCursor.getString(ArticleLoader.Query.BODY), true),
                            "Rosario-Regular.ttf",
                            "left", 0, 0,
                            Toolbox.getHexColorString(mHostActivity, R.color.textColorMedium));
            mBodyWebView.loadDataWithBaseURL("file:///android_asset/", htmlString,
                    "text/html", "UTF-8", null);
            // disable webview horizontal scrolling (this also disables vertical scrolling -
            // the parent scrollview handles this)
            // https://stackoverflow.com/questions/11064014/how-to-disable-horizontal-scrolling-in-android-webview
            mBodyWebView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });

            // Set up the image and the appbar background color

            // TODO: Glide vs Picasso
            int resize = mHostActivity.getResources().getInteger(R.integer.shared_elements_image_size_pixels);

            if (mHostActivity.getResources().getBoolean(R.bool.loadWithGlide)) {
                // Glide
                Toolbox.loadSharedElementsImageFromUrl(mHostActivity,
                        mCursor.getString(ArticleLoader.Query.PHOTO_URL), mTempPhotoView, resize,
                        new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Bitmap> target, boolean isFirstResource) {
                                // begin shared elements transition. needs to be set here as well to
                                // prevent ui from hanging during failure
                                getParentFragment().startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model,
                                                           Target<Bitmap> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                // begin shared elements transition. needs to be set here for once
                                // image is loaded
                                getParentFragment().startPostponedEnterTransition();
                                mCToolbar.setContentScrimColor(Toolbox.getBackgroundColor(resource,
                                        Toolbox.PaletteSwatch.MUTED,
                                        mHostActivity.getResources().getColor(R.color.colorPrimary)));
                                return false;
                            }
                        });
                Toolbox.loadSharedElementsImageFromUrl(mHostActivity,
                        mCursor.getString(ArticleLoader.Query.PHOTO_URL), mPhotoView, resize,
                        null);
            } else {
                // Picasso
                com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mTempPhotoView.setImageBitmap(bitmap);
                        mCToolbar.setContentScrimColor(Toolbox.getBackgroundColor(bitmap,
                                Toolbox.PaletteSwatch.MUTED,
                                mHostActivity.getResources().getColor(R.color.colorPrimary)));
                        getParentFragment().startPostponedEnterTransition();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        getParentFragment().startPostponedEnterTransition();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                // Setting the tag to the Picasso target will help guarantee loading
                // https://stackoverflow.com/questions/24180805/onbitmaploaded-of-target-object-not-called-on-first-load
                mTempPhotoView.setTag(target);
                Toolbox.loadSharedElementsImageFromUrlWithTargetCallbacks(
                        mCursor.getString(ArticleLoader.Query.PHOTO_URL), resize, resize, target);
                Toolbox.loadSharedElementsImageFromUrl(
                        mCursor.getString(ArticleLoader.Query.PHOTO_URL), mPhotoView,
                        resize, resize, null);
            }
        } else {
            mTitleView.setText(getString(R.string.null_data));
            mAuthorView.setText(getString(R.string.null_data));
            mBodyWebView.loadDataWithBaseURL("", getString(R.string.null_data), "text/html",
                    "UTF-8", null);
        }
    }

    /**
     * Helper for setting the visibility of app bar contents, as well as the scrolling titles
     *
     * @param show
     */
    private void showAppBar(boolean show) {
        Toolbox.showView(mTv_toolbarTitle, show, false);
        Toolbox.showView(mOverflowButton, show, true);
        mAppBarShowing = show;
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.e(ex);
            Timber.i("passing today's date");
            return new Date();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(mHostActivity, mItemId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Timber.e("Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        setupDetailsUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
        mCursor = null;
        setupDetailsUI();
    }
}
