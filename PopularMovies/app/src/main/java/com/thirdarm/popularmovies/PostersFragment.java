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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.data.MovieColumns;
import com.thirdarm.popularmovies.data.MovieProvider;
import com.thirdarm.popularmovies.data.MovieProjections.Results;
import com.thirdarm.popularmovies.utilities.ReleaseDates;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.data.MovieProvider.Movies;

import java.util.ArrayList;

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

    // For launching the movie detail activity
    private static final String INTENT_DATA = "myData";

    // For saving the current activity state into a bundle
    private static final String DATA_MOVIES = "movies";
    private static final String DATA_TITLE = "title";
    private static final String DATA_POSITION = "position";

    private Context mContext;
    private Callback mCallback;
    private static final int MOVIE_LOADER_ID = 0;

    // views
    private View mRootView;
    private GridView mGridView;
    private PostersAdapter mPostersAdapter;

    // for loading and displaying movies
    public static TMDB mTmdb; // allow detail activity to access the TMDB
    private ArrayList<MovieDB> mMovies;
    private final String mPosterSize = IMAGE.SIZE.POSTER.w500;
    private String mCategory = PARAMS.CATEGORY.POPULAR;
    private String mSort = PARAMS.RESULTS.SORT.POPULARITY_DESC;
    private String mLanguage = "en";
    public static boolean mLoadGuard = true; // allow FetchMovieResultsTask to read guard
    private int mPosition;

    // thresholds
    private int THRESHOLD_DATE_LOWER = -35;
    private int THRESHOLD_DATE_MIDDLE = 0;
    private int THRESHOLD_DATE_UPPER = 28;
    private String THRESHOLD_RATING_LOWER = "7";
    private String THRESHOLD_VOTES_LOWER = "50";
    private String THRESHOLD_POPULARITY_LOWER = "5";


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
        inflater.inflate(R.menu.movie_posters_fragment, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_discover:
                fetchMovies(mCategory = PARAMS.CATEGORY.DISCOVER);
                return true;

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        // Save and reuse information upon activity destroy
        if (mLoadGuard && mMovies != null) {
            outState.putParcelableArrayList(DATA_MOVIES, mMovies);
            outState.putString(DATA_TITLE, mCategory);
            outState.putInt(DATA_POSITION, mGridView.getFirstVisiblePosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get a reference to UI elements
        mRootView = inflater.inflate(R.layout.fragment_posters, container, false);
        mGridView = (GridView) mRootView.findViewById(R.id.posters_grid);

        // Create PostersAdapter. Loaders will swap the currently null cursor once the cursor has
        //  been loaded.
        mPostersAdapter = new PostersAdapter(mContext, mPosterSize, null);
        mGridView.setAdapter(mPostersAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    mCallback.onItemSelected(MovieProvider.Movies.withId(
                                    cursor.getInt(cursor.getColumnIndex(MovieColumns.TMDB_ID))));
//                    // UPDATE: Now, if ui is single-pane, detail activity will be launched through
//                    //  the main activity
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(MovieProvider.Movies.withId(cursor.getInt(cursor.getColumnIndex(MovieColumns.TMDB_ID))));
//                    startActivity(intent);
                }
                mPosition = position;
            }
        });

        // Create TMDB API
        mTmdb = new TMDB(getString(R.string.movie_api_key), mLanguage);

//        // Recycle information from last destroy, if applicable
//        if(savedInstanceState != null &&
//                savedInstanceState.containsKey(DATA_MOVIES) &&
//                savedInstanceState.containsKey(DATA_TITLE) &&
//                savedInstanceState.containsKey(DATA_POSITION)) {
//            setGridView(mMovies = savedInstanceState.getParcelableArrayList(DATA_MOVIES));
//            setTitle(mCategory = savedInstanceState.getString(DATA_TITLE));
//            mGridView.smoothScrollToPosition(savedInstanceState.getInt(DATA_POSITION));
//            hideProgressBar();
//        } else {
//            // Otherwise, populate with popular movies by default
//            fetchMovies(mCategory);
//        }
        //fetchMovies(mCategory);

        // Initialize the local database upon running app for the first time
        if (checkIfEmptyLocalMovieDb()) createMovieDb(mCategory);

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
     * Creates and fills a local database with movies
     *
     * @param category
     */
    public void createMovieDb(String category) {
        new FetchMovieResultsTask(getActivity(), mTmdb, true, mSort, category, mRootView).execute(category);
        mLoadGuard = false;
    }

    /**
     * Checks to see if the local movie database is empty.
     *
     * @return true if the db is empty
     */
    private boolean checkIfEmptyLocalMovieDb() {
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(
                Movies.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        boolean empty = !cursor.moveToFirst();
        cursor.close();
        return (empty);
    }

    /**
     * Fetches the movies online
     *
     * @param category category of movies to fetch
     */
    public void fetchMovies(String category) {
        // Check if sort buttons have been clicked before results have been loaded
        if (!mLoadGuard) {
            Toast.makeText(mContext, getString(R.string.status_still_loading), Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Disable reloading while grid is being populated
            mLoadGuard = false;
        }

        mGridView.smoothScrollToPosition(0);
        new FetchMovieResultsTask(getActivity(), mTmdb, false, mSort, category, mRootView).execute(category);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }


    /*
        Loader methods
     */

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Declare fields which would be used in querying the cursor
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        // For defining thresholds for NOW_PLAYING and UPCOMING search queries
        String dateRange[];

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
        Log.d(LOG_TAG, "onLoadFinished");
    }

    @Override public void onLoaderReset(Loader loader) {
        mPostersAdapter.swapCursor(null);
    }

}
