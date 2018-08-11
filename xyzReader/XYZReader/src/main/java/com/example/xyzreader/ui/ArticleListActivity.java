package com.example.xyzreader.ui;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.util.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BOOL_INITIATED = "com.example.xyzreader.ui.initiated";
    public static final String KEY_CURRENT_POSITION = "com.example.xyzreader.ui.adapter_position";
    public static final String SHARED_PREFERENCES = "com.example.xyzreader.ui.sp";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    ArticleListAdapter mListAdapter;

    // Only do certain things once (entrance animations)
    private boolean mInitiated = false;

    SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);

        mSp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        final View toolbarContainerView = findViewById(R.id.toolbar_container);

        getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            // TODO: Disable this for now
            //refresh();
            mSp.edit()
                    .remove(KEY_CURRENT_POSITION)
                    .apply();
        } else {
            if (savedInstanceState.containsKey(BOOL_INITIATED)) {
                mInitiated = savedInstanceState.getBoolean(BOOL_INITIATED);
            }
        }

//        // TODO: This is just an option fade-in and fade-out from this activity (from guide)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Fade fade = new Fade();
//            fade.setDuration(500);
//
//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);
//        }

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
        prepareSharedElementsTransition();
        supportPostponeEnterTransition();
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
        mSp.edit()
                .remove(KEY_CURRENT_POSITION)
                .apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
        scrollToPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private void scrollToPosition() {
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mRecyclerView.removeOnLayoutChangeListener(this);
                final int position = mSp.getInt(KEY_CURRENT_POSITION, 0);
                final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(position);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            layoutManager.scrollToPosition(position);
                        }
                    });
                }
            }
        });
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Communicates with the update service to show the loading icon during updates
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BOOL_INITIATED, mInitiated);
        super.onSaveInstanceState(outState);
    }

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private void prepareSharedElementsTransition() {
//        setExitSharedElementCallback(new SharedElementCallback() {
//            // This is called when exiting and when re-entering, and will remap the shared view
//            // and adjust the transition for when view is changed after paging the images
//            @Override
//            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                // Get the viewholder for the current position
//                int position = mSp.getInt(KEY_CURRENT_POSITION, 0);
//                RecyclerView.ViewHolder selectedViewHolder = mRecyclerView.findViewHolderForAdapterPosition(position);
//                if (selectedViewHolder == null || selectedViewHolder.itemView == null) return;
//
//                // We're only interested in one view transition, so we just need to adjust mapping
//                // for the first shared element name
//                // TODO: This is only called once... when I leave, but it is not called when I come
//                // back
//                sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.article_thumbnail));
//            }
//        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // TROD: Reminder, this is only called when the loader doesn't already exist. Loaders
        // persist across rotation changes, and even through home and recents screen
        return ArticleLoader.newAllArticlesInstance(this);
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
        Interpolator interpolator = AnimationUtils.loadInterpolator(this,
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
