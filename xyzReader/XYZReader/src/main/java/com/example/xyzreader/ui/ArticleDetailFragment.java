package com.example.xyzreader.ui;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.util.Toolbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private int mMutedColor = 0xFF333333;
    @BindView(R.id.scrollview_details)
    NestedScrollView mScrollView;
    @BindView(R.id.app_bar_detail)
    AppBarLayout mAppBar;
    @BindView(R.id.ctoolbar_detail)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.toolbar_title_details)
    TextView mTv_toolbarTitle;
    @BindView(R.id.container_details_meta)
    LinearLayout mContainerMetaDetails;

//    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
//    private ColorDrawable mStatusBarColorDrawable;

    //    private int mTopInset;
//    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
//    private int mStatusBarFullOpacityBottom;

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

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
//        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
//                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
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

    // A method to find height of the status bar
    // Source: https://stackoverflow.com/questions/27856603/lollipop-draw-behind-statusbar-with-its-color-set-to-transparent
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);

        // Makes the status bar translucent (you can also set the status bar color in this way)
        // Source: https://stackoverflow.com/questions/26702000/change-status-bar-color-with-appcompat-actionbaractivity
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // By doing the above, this means the toolbar will get drawn behind the status bar. The
        // below code fixes that, but it results in no views behind the status bar, so the status
        // bar could be black or white background before the views scroll up behind it
        //mAppBar.setPadding(0, getStatusBarHeight(), 0, 0);

        // TROD: This seems to be manual implementation of image parallax
        // TODO: But this could also be something that updates a read-progress bar somewhere,
        // probably the status bar
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mScrollY = mScrollView.getScrollY();
//                getActivityCast().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this);
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

        mPhotoView = mRootView.findViewById(R.id.iv_photo_details);

//        mStatusBarColorDrawable = new ColorDrawable(0);

        // TODO: Set this
        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
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
                boolean isShow = true;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        mTv_toolbarTitle.setVisibility(View.VISIBLE);
                        ObjectAnimator.ofObject(mTv_toolbarTitle, "alpha", new FloatEvaluator(), 0f, 1f).setDuration(300).start();
                        ObjectAnimator.ofObject(mContainerMetaDetails, "alpha", new FloatEvaluator(), 1f, 0f).setDuration(300).start();
                        isShow = true;
                    } else if (isShow) {
                        ObjectAnimator.ofObject(mTv_toolbarTitle, "alpha", new FloatEvaluator(), 1f, 0f).setDuration(300).start();
                        ObjectAnimator.ofObject(mContainerMetaDetails, "alpha", new FloatEvaluator(), 0f, 1f).setDuration(300).start();
                        isShow = false;
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
            bodyWebView.loadData("<body style=\"margin: 0; padding: 0\">" + mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />") + "</body>", "text/html", "UTF-8");

            // Set up the image and the appbar background color
            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.generate(bitmap, 12);
                                mMutedColor = p.getDarkMutedColor(0xFF333333);
                                mPhotoView.setImageBitmap(bitmap);
                                mCToolbar.setContentScrimColor(Toolbox.getBackgroundColor(bitmap, Toolbox.PaletteSwatch.MUTED, getResources().getColor(R.color.colorPrimary)));
                                // TODO: This probably was another way of setting the app bar color...
//                                mRootView.findViewById(R.id.meta_bar)
//                                        .setBackgroundColor(mMutedColor);
//                                updateStatusBar();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

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
