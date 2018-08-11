package com.example.xyzreader.ui;

import android.app.Activity;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link MainActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";

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
    @BindView(R.id.app_bar_detail)
    AppBarLayout mAppBar;
    @BindView(R.id.ctoolbar_detail)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.gap_status_bar)
    View mStatusBarGap;
    @BindView(R.id.toolbar_detail)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title_details)
    TextView mTv_toolbarTitle;
    @BindView(R.id.ib_action_up)
    ImageButton mUpButton;
    @BindView(R.id.ib_action_menu)
    ImageButton mOverflowButton;
    @BindView(R.id.fab_share)
    FloatingActionButton mShareFab;

    // Details views
    @BindView(R.id.iv_photo_details)
    ImageView mPhotoView;
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

    private boolean mIsCard = false;

    // For knowing when to show or hide the app bar
    boolean mAppBarShowing;

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
     * @return
     */
    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        // The point of setting this is so the fragment knows which id to load into the cursor
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(ArticleDetailFragment.class.getSimpleName());

        mHostActivity = getActivity();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);

        setupStatusAppBar();

        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since the detail cursor contains only the article, pass in 0 for position
                Toolbox.shareArticle(mHostActivity, mCursor, 0);
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
        int statusBarHeight = Toolbox.getStatusBarHeight(mHostActivity);
        int statusAppBarHeight = Toolbox.getStatusBarWithActionBarHeight(mHostActivity);
        CollapsingToolbarLayout.LayoutParams params =
                (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
        mStatusBarGap.getLayoutParams().height = statusBarHeight;
        params.setMargins(0, statusBarHeight, 0, 0);
        mToolbar.setLayoutParams(params);

        // Adjust the scrim trigger to activate when the height is on the verge of matching the
        // combined status bar and app bar height, since view is rendered from where the status
        // bar is, downward. If we did not have translucent status bar, then this would just be
        // the app bar height instead
        mCToolbar.setScrimVisibleHeightTrigger(statusAppBarHeight +
                (int) getResources().getDimension(R.dimen.collapsing_toolbar_scrim_buffer_height));

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
                Toolbox.showArticleActionsMenuPopup(mHostActivity, v, mCursor,
                        0);
            }
        });
    }

    /**
     * Helper method that sets up the UI details
     */
    private void setupDetailsUI() {
        if (mRootView == null) {
            return;
        }

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            final String title = mCursor.getString(ArticleLoader.Query.TITLE);
            mTitleView.setText(title);
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

            mAuthorView.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));

            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                mDateView.setText(DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL));
            } else {
                // If date is before 1902, just show the string
                mDateView.setText(outputFormat.format(publishedDate));
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
                    (getActivity(),
                            mCursor.getString(ArticleLoader.Query.BODY)
                                    .replaceAll("(\r\n|\n)", "<br />"),
                            "Rosario-Regular.ttf",
                            getResources().getDimension(R.dimen.detail_body_text_size), "left",
                            0, 0,
                            Toolbox.getHexColorString(getActivity(), R.color.textColorMedium));
            mBodyWebView.loadDataWithBaseURL("file:///android_asset/", htmlString,
                    "text/html", "UTF-8", null);

            // Set up the image and the appbar background color
            mPhotoView.setTransitionName("image" + mItemId);
            Toolbox.loadThumbnailFromUrl(getActivity(), mCursor.getString(ArticleLoader.Query.PHOTO_URL),
                    mPhotoView, new RequestListener<Bitmap>() {
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
                                    getResources().getColor(R.color.colorPrimary)));
                            return false;
                        }
                    });
        } else {
            mRootView.setVisibility(View.GONE);
            mTitleView.setText(getString(R.string.null_data));
            mAuthorView.setText(getString(R.string.null_data));
            mBodyWebView.loadData(getString(R.string.null_data), "text/html", "UTF-8");
        }
    }

    /**
     * Helper for setting the visibility of app bar contents, as well as the scrolling titles
     *
     * @param show
     */
    private void showAppBar(boolean show) {
        if (show) {
            Toolbox.showView(mTv_toolbarTitle, true, false);
            Toolbox.showView(mContainerMetaDetails, false, false);
            Toolbox.showView(mOverflowButton, true, true);
        } else {
            Toolbox.showView(mTv_toolbarTitle, false, false);
            Toolbox.showView(mContainerMetaDetails, true, false);
            Toolbox.showView(mOverflowButton, false, true);

        }
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
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
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
