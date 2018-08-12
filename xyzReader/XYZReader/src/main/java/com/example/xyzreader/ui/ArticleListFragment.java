package com.example.xyzreader.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.util.Toolbox;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a list of Articles. This fragment has different presentations for
 * handset and tablet-size devices. On handsets, the fragment presents a list of items, which when
 * touched, lead to a {@link ArticleDetailFragment} representing item details. On tablets, the
 * fragment presents a grid of items as cards.
 */
public class ArticleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BOOL_INITIATED = "com.example.xyzreader.ui.initiated";
    private static final String OFFSET = "com.example.xyzreader.ui.offset";

    @BindView(R.id.details_gap_status_bar)
    View mStatusBarGap;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.coordinator_list)
    CoordinatorLayout mCoordinator;
    @BindView(R.id.list_appbar)
    AppBarLayout mToolbar;
    ArticleListAdapter mListAdapter;

    Activity mHostActivity;

    // For the updating process TODO do we really need this
    private boolean mIsRefreshing = false;
    // Only do certain things once (entrance animations)
    private boolean mInitiated = false;
    // for keeping track of the toolbar offset
    private int offset = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
        Timber.tag(ArticleListFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);
        mHostActivity = getActivity();

        if (savedInstanceState == null) {
            // TODO: Disable this for now
            //refresh();
        } else {
            if (savedInstanceState.containsKey(BOOL_INITIATED)) {
                mInitiated = savedInstanceState.getBoolean(BOOL_INITIATED);
            }
            if (savedInstanceState.containsKey(OFFSET)) {
                offset = savedInstanceState.getInt(OFFSET);
            }
        }

        // fill-in the empty view space from the translucent status bar
        mStatusBarGap.getLayoutParams().height = Toolbox.getStatusBarHeight(mHostActivity);

        mToolbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                offset = verticalOffset;
            }
        });

        setupRecyclerView();

        prepareTransition();
        postponeEnterTransition();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHostActivity.registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
        scrollToPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHostActivity.unregisterReceiver(mRefreshingReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BOOL_INITIATED, mInitiated);
        outState.putInt(OFFSET, offset);
        super.onSaveInstanceState(outState);
    }

    /**
     * Helper that sets up the recycler view and adapter
     */
    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        // TODO: Make sure this works fine across differing screen sizes
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.setHasFixedSize(true);
        mListAdapter = new ArticleListAdapter(this);
        mListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mListAdapter);
        // TODO: We can smooth scroll here, but we need to account for app bar current size
        //mRecyclerView.smoothScrollToPosition(1000);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void scrollToPosition() {
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mRecyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.sCurrentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            layoutManager.scrollToPosition(MainActivity.sCurrentPosition);
                        }
                    });
                }
            }
        });
        mToolbar.setExpanded(!mInitiated);
    }

    /**
     * For communicating with the update service to show the loading icon during updates
     */
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    /**
     * Refreshes the recycler view
     */
    private void refresh() {
        mHostActivity.startService(new Intent(getContext(), UpdaterService.class));
    }

    /**
     * Show the loading icon in the swipe refresh layout
     */
    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private void prepareTransition() {
        // Create transition programmatically - treat all view elements the same
        Transition exitTransition = new Slide(Gravity.TOP)
                .setDuration(600)
                .setInterpolator(new FastOutLinearInInterpolator());
        setExitTransition(new Fade());
        // Create transition statically - treat key views differently
        Transition reenterTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.list_reenter_transition);
        setReenterTransition(reenterTransition);

        setExitSharedElementCallback(new SharedElementCallback() {
            // This is called when exiting and when re-entering, and will remap the shared view
            // and adjust the transition for when view is changed after paging the images
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Get the viewholder for the current position
                RecyclerView.ViewHolder selectedViewHolder =
                        mRecyclerView.findViewHolderForAdapterPosition(MainActivity.sCurrentPosition);
                if (selectedViewHolder == null || selectedViewHolder.itemView == null) return;

                // We're only interested in one view transition, so we just need to adjust mapping
                // for the first shared element name
                // TODO: This is only called once... when I leave, but it is not called when I come
                // back
                sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.article_thumbnail));
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // TROD: Reminder, this is only called when the loader doesn't already exist. Loaders
        // persist across rotation changes, and even through home and recents screen
        return ArticleLoader.newAllArticlesInstance(mHostActivity);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
        // TROD: However, this is called every time the activity comes back into focus, such as
        // after configuration changes, etc.
        mListAdapter.swapCursor(cursor);

        // Animate the views in only if the activity has loaded the first time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !mInitiated)
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Animate the cards once the recyclerview had laid down its views, otherwise
                    // RecyclerView.getChildAt() will always return null
                    // https://stackoverflow.com/questions/30397460/how-to-know-when-the-recyclerview-has-finished-laying-down-the-items
                    animateCardsIn();

                    // Make sure to remove the listener to ensure this only gets called once
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mInitiated = true;
                }
            });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }

    /**
     * Animates cards into the view from the bottom
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateCardsIn() {
        int size = mRecyclerView.getAdapter().getItemCount();
        float offset = getResources()
                .getDimensionPixelSize(R.dimen.card_entry_animation_start_offset);
        Interpolator interpolator = AnimationUtils.loadInterpolator(mHostActivity,
                android.R.interpolator.linear_out_slow_in);

        for (int i = 0; i < size; i++) {
            View view = mRecyclerView.getChildAt(i);
            if (view != null) {
                // Only animate views the recycler view already laid down
                view.setTranslationY(offset);
                view.setAlpha(Toolbox.getFloatFromResources(
                        getResources(), R.dimen.card_entry_animation_start_alpha));

                view.animate()
                        .translationY(getResources()
                                .getDimensionPixelSize(R.dimen.card_entry_animation_end_offset))
                        .alpha(Toolbox.getFloatFromResources(
                                getResources(), R.dimen.card_entry_animation_end_alpha))
                        .setInterpolator(interpolator)
                        .setDuration((long) getResources().getInteger(
                                R.integer.card_entry_animation_duration))
                        .start();

                offset *= Toolbox.getFloatFromResources(
                        getResources(), R.dimen.card_entry_animation_offset_shift);
            }
        }
    }


}
