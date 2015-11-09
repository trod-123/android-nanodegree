/*
 * Copyright (C) 2015 Teddy Rodriguez (TROD)
 *   email: cia.123trod@gmail.com
 *   github: TROD-123
 *
 * For Udacity's Android Developer Nanodegree
 * P1-2: Popular Movies
 *
 * Currently for educational purposes only.
 */

package com.thirdarm.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.data.MovieColumns;
import com.thirdarm.popularmovies.data.MovieProvider;
import com.thirdarm.popularmovies.data.MovieProjections.Results;
import com.thirdarm.popularmovies.sync.MoviesSyncAdapter;
import com.thirdarm.popularmovies.utilities.AutoResizeImageView;
import com.thirdarm.popularmovies.utilities.AutoResizeTextView;
import com.thirdarm.popularmovies.utilities.Network;
import com.thirdarm.popularmovies.utilities.ReleaseDates;
import com.thirdarm.popularmovies.data.MovieProvider.Movies;
import com.thirdarm.popularmovies.utilities.SwapViewContainers;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Fragment consisting of a grid of movie posters
 */
public class PostersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections. How this works is that whenever the method is called here, it is
     * also called in activities that implement the method, e.g. MainActivity. That is, the
     * method in MainActivity is called THROUGH here.
     */
    public interface Callback {
        void onItemSelected(Uri movieUri);
    }

    private static final String LOG_TAG = "PostersFragment";

    // For saving the current activity state into a bundle
    private static final String DATA_CATEGORY = "category";
    private static final String DATA_POSITION = "position";

    private Context mContext;

    // data
    private Callback mCallback;
    private static final int MOVIE_LOADER_ID = 0;

    // views
    private View mRootView;
    private PostersAdapter mPostersAdapter;
    @Bind(R.id.posters_grid) GridView mGridView;
    @Bind(R.id.container_empty_favorites) RelativeLayout mEmptyFavoritesContainer;
    @Bind(R.id.container_empty_movies) RelativeLayout mEmptyMoviesContainer;

    // for loading and displaying movies
    private final String mPosterSize = IMAGE.SIZE.POSTER.w500;
    private String mCategory = PARAMS.CATEGORY.POPULAR;
    private int mPosition;


    public PostersFragment() {
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        // For attaching the activity to the Callback
        mCallback = (Callback) activity;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar
        inflater.inflate(R.menu.menu_fragment_posters, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                refreshMovies();
                return true;

//            case R.id.action_discover:
//                fetchMovies(mCategory = PARAMS.CATEGORY.DISCOVER);
//                return true;

            case R.id.action_get_playing:
                fetchMovies(mCategory = PARAMS.CATEGORY.PLAYING);
                return true;

            case R.id.action_get_popular:
                fetchMovies(mCategory = PARAMS.CATEGORY.POPULAR);
                return true;

            case R.id.action_get_rated:
                fetchMovies(mCategory = PARAMS.CATEGORY.TOP);
                return true;

            case R.id.action_get_upcoming:
                fetchMovies(mCategory = PARAMS.CATEGORY.UPCOMING);
                return true;

            case R.id.action_get_favorites:
                fetchMovies(mCategory = PARAMS.CATEGORY.FAVORITES);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        // Save and reuse information upon activity destroy
        outState.putString(DATA_CATEGORY, mCategory);
        outState.putInt(DATA_POSITION, mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get a reference to UI elements
        mRootView = inflater.inflate(R.layout.fragment_posters, container, false);
        ButterKnife.bind(this, mRootView);

        // Create PostersAdapter. Loaders will swap the currently null cursor once the cursor has
        //  been loaded.
        mPostersAdapter = new PostersAdapter(mContext, mPosterSize, null);
        mGridView.setAdapter(mPostersAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    mCallback.onItemSelected(MovieProvider.Movies.withId(
                                    cursor.getInt(Results.COL_MOVIE_TMDB_ID))
                    );
                }
                mPosition = position;
            }
        });

        // Recycle information from last destroy, if applicable
        if(savedInstanceState != null &&
                savedInstanceState.containsKey(DATA_CATEGORY) &&
                savedInstanceState.containsKey(DATA_POSITION)) {
            setTitle(mCategory = savedInstanceState.getString(DATA_CATEGORY));
            mGridView.smoothScrollToPosition(savedInstanceState.getInt(DATA_POSITION));
        }

        // TODO: Create a preferences activity and load movies according to preferences. Include:
        //  -size of posters: list of dimensions (focus on widths)
        //  -info overlay: true or false
        //  -language
        //  -number of results per page (and functionality in UI to view other pages too)

        return mRootView;
    }

    // TODO: Figure out what the difference is between loading the Loader here or in
    //  onViewCreated(view, bundle) instead
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    /**
     * Refreshes/resyncs the movies online
     */
    private void refreshMovies() {
        if (Network.isNetworkAvailable(mContext)) {
            mGridView.smoothScrollToPosition(0);
            MoviesSyncAdapter.syncImmediately(mContext, null, -1, -1);
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else {
            Toast.makeText(mContext, "There is no internet connection. Did not go through sync.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches the movies online
     *
     * @param category category of movies to fetch
     */
    private void fetchMovies(String category) {
        mGridView.smoothScrollToPosition(0);
        mCategory = category;
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }


    /**
     * Sets the title of the activity based on sort category
     *
     * @param category the category used to sort movies
     */
    private void setTitle(String category) {
        switch (category) {
            case PARAMS.CATEGORY.DISCOVER:
                getActivity().setTitle(getString(R.string.title_discover));
                break;

            case PARAMS.CATEGORY.PLAYING:
                getActivity().setTitle(getString(R.string.title_playing));
                break;

            case PARAMS.CATEGORY.POPULAR:
                getActivity().setTitle(getString(R.string.title_popular));
                break;

            case PARAMS.CATEGORY.TOP:
                getActivity().setTitle(getString(R.string.title_top_rated));
                break;

            case PARAMS.CATEGORY.UPCOMING:
                getActivity().setTitle(getString(R.string.title_upcoming));
                break;

            case PARAMS.CATEGORY.FAVORITES:
                getActivity().setTitle(getString(R.string.title_favorites));
                break;
        }
    }

    private void swapViewContainers() {
        if (mPostersAdapter.getCount() == 0) {
            if (mCategory == PARAMS.CATEGORY.FAVORITES) {
                SwapViewContainers.showViewContainer(mEmptyFavoritesContainer, mRootView);
                SwapViewContainers.hideViewContainer(mEmptyMoviesContainer, mRootView);
            } else {
                SwapViewContainers.showViewContainer(mEmptyMoviesContainer, mRootView);
                SwapViewContainers.hideViewContainer(mEmptyFavoritesContainer, mRootView);
            }
        } else {
            SwapViewContainers.hideViewContainer(mEmptyMoviesContainer, mRootView);
            SwapViewContainers.hideViewContainer(mEmptyFavoritesContainer, mRootView);
        }
    }


    /*
        Loader methods
     */

    // Note: This method does not seem to be called when activity is recreated after rotating device
    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Declare fields which would be used in querying the cursor
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        // thresholds
        int THRESHOLD_DATE_LOWER = -40;
        int THRESHOLD_DATE_MIDDLE = 0;
        int THRESHOLD_DATE_UPPER = 28;
        String THRESHOLD_RATING_LOWER = "7";
        String THRESHOLD_VOTES_LOWER = "50";
        String THRESHOLD_POPULARITY_LOWER = "5";

        // For defining thresholds for NOW_PLAYING and UPCOMING search queries
        String dateRange[];

        // Set title of activity to reflect category
        setTitle(mCategory);

        // Check to see the current category which would be used to query the appropriate movies
        //  by setting the above fields
        // TODO: The issue with ordering solely by sortOrder is that the movies that would
        //  be sorted are those that are already in the local db, not accounting for
        //  movies that are in other pages of the actual {category} api query
        // WHAT ABOUT: In the background, the app queries and holds onto the next page of results
        //  such that, when the user is on page X, in the background the app loads up page X+1 so
        //  that the next page of results will always be ready for when the user needs it.
        switch (mCategory) {
            case PARAMS.CATEGORY.DISCOVER:
                break;

            case PARAMS.CATEGORY.PLAYING:
                // The threshold for now playing movies should be between:
                //  (CURRENT DATE - 35 days, CURRENT DATE + 7 days)
                //  e.g. current: 2015-10-07, minimum: 2015-09-02, maximum: 2015-10-14
               dateRange = ReleaseDates.getDateRangeFromToday(
                        THRESHOLD_DATE_LOWER, THRESHOLD_DATE_MIDDLE
                );
                selection = MovieColumns.RELEASE_DATE + " BETWEEN ? AND ? ";
                selectionArgs = new String[] {dateRange[0], dateRange[1]};
                sortOrder = MovieColumns.RELEASE_DATE + " DESC";
                break;

            case PARAMS.CATEGORY.POPULAR:
                selection = MovieColumns.POPULARITY + " >= ?";
                selectionArgs = new String[] {THRESHOLD_POPULARITY_LOWER};
                sortOrder = MovieColumns.POPULARITY + " DESC";
                break;

            case PARAMS.CATEGORY.TOP:
                selection = MovieColumns.VOTE_AVERAGE + " >= ? AND " +
                        MovieColumns.VOTE_COUNT + " >= ?";
                selectionArgs = new String[] {THRESHOLD_RATING_LOWER, THRESHOLD_VOTES_LOWER};
                sortOrder = MovieColumns.VOTE_AVERAGE + " DESC";
                break;

            case PARAMS.CATEGORY.UPCOMING:
                // The threshold for upcoming movies should be between:
                //  (CURRENT DATE + 7 days, CURRENT DATE + 28 days)
                //  e.g. current: 2015-10-07, minimum: 2015-10-14, maximum: 2015-11-04
                dateRange = ReleaseDates.getDateRangeFromToday(
                        THRESHOLD_DATE_MIDDLE + 1, THRESHOLD_DATE_UPPER
                );
                selection = MovieColumns.RELEASE_DATE + " BETWEEN ? AND ? ";
                selectionArgs = new String[] {dateRange[0], dateRange[1]};
                sortOrder = MovieColumns.RELEASE_DATE + " ASC";
                break;

            case PARAMS.CATEGORY.FAVORITES:
                selection = MovieColumns.FAVORITE + " == ? ";
                selectionArgs = new String[] {"1"};
                break;
        }

        // Create a cursor pointing to the table containing the columns as specified in the
        //  PROJECTION
        return new CursorLoader(mContext, Movies.CONTENT_URI, Results.PROJECTION,
                selection, selectionArgs, sortOrder);
    }

    @Override public void onLoadFinished(Loader loader, Cursor data) {
        // Create a new adapter only if the adapter is null. Otherwise, use the data that's already
        //  available in the cursor
        // The issue with this is that the cursor gets loaded first before all the movies have
        //  finished downloading. So we can't hide and progress bar here.

        // Another issue of this is that each time an entry in the cursored database is updated,
        //  the cursor gets re-loaded and this method is called each time
        mPostersAdapter.swapCursor(data);
        swapViewContainers();

        Log.d(LOG_TAG, "onLoadFinished");
    }

    @Override public void onLoaderReset(Loader loader) {
        mPostersAdapter.swapCursor(null);
    }

}
