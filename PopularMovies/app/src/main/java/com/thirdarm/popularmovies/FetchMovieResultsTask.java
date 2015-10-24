/*
 *  Copyright (C) 2015 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P1-2: Popular Movies
 *
 *  Currently for educational purposes only.
 */

package com.thirdarm.popularmovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.data.*;
import com.thirdarm.popularmovies.model.Credits;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.data.MovieProvider.Movies;
import com.thirdarm.popularmovies.utilities.Network;

/**
 * Collects and parses JSON data from the TMDB servers via API calls and
 * fills the main UI with posters in a grid view.
 *
 */
public class FetchMovieResultsTask extends AsyncTask<String, Void, Void> {

    public static final String LOG_TAG = "FetchMovieResultsTask";

    private Activity mActivity;
    private TMDB mTmdb;
    private boolean mInitialize;
    private String mSort;
    private String mCategory;

    // references to PostersFragment views
    private RelativeLayout mProgressContainer;
    private View mRootView;

    // allow TMDB to modify the loading status
    public static TextView sProgressStatus;
    public static ProgressBar sProgressBar;


    public FetchMovieResultsTask(Activity activity, TMDB tmdb, boolean initialize,
                                 String sort, String category, View view) {
        mActivity = activity;
        mTmdb = tmdb;
        mInitialize = initialize;
        mSort = sort;
        mCategory = category;
        mRootView = view;

        //mRootView = mActivity.findViewById(android.R.id.content).getRootView();
        mProgressContainer = (RelativeLayout) mRootView.findViewById(R.id.progress_container);
        sProgressStatus = (TextView) mRootView.findViewById(R.id.progress);
        sProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
    }

    /**
     * Displays overlay containing loading icon and status, resets horizontal progress bar, and
     *  disables touch events.
     */
    public void showProgressBar() {
        // TODO: Figure out how to fade the view in and out instead of having it just appear
        //  right away
        mProgressContainer.bringToFront();
        mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.VISIBLE);
        sProgressBar.setProgress(0);
        enableDisableViewGroup((ViewGroup) mRootView, false);
    }

    /** Hides overlay and enables touch events */
    public void hideProgressBar() {
        mProgressContainer.setVisibility(View.GONE);
        mRootView.setClickable(true);
        enableDisableViewGroup((ViewGroup) mRootView, true);
    }

    /**
     * Enables/Disables all child views in a view group.
     * From http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
     *
     * @param viewGroup the view group
     * @param enabled <code>true</code> to enable, <code>false</code> to disable
     * the views.
     */
    public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    /**
     * Sets the title of the activity based on sort category
     *
     * @param category the category used to sort movies
     */
    public void setTitle(String category) {
        switch (category) {
            case PARAMS.CATEGORY.DISCOVER:
                mActivity.setTitle(mActivity.getString(R.string.title_discover));
                break;

            case PARAMS.CATEGORY.PLAYING:
                mActivity.setTitle(mActivity.getString(R.string.title_playing));
                break;

            case PARAMS.CATEGORY.POPULAR:
                mActivity.setTitle(mActivity.getString(R.string.title_popular));
                break;

            case PARAMS.CATEGORY.TOP:
                mActivity.setTitle(mActivity.getString(R.string.title_top_rated));
                break;

            case PARAMS.CATEGORY.UPCOMING:
                mActivity.setTitle(mActivity.getString(R.string.title_upcoming));
                break;
        }
    }

    /**
     * Adds a movie to the local database
     *
     * BUG: Currently includes grabbing and storing credits objects, which makes this process really
     *  slow.
     *
     * @param movie the movie to be added
     * @return the long value of the row where the movie was inserted
     */
    private long addMovie(final MovieDB movie) {
        ContentResolver cr = mActivity.getContentResolver();
        long locationId;

        Cursor cursor = cr.query(
                Movies.CONTENT_URI,
                new String[] {MovieColumns.TMDB_ID},
                MovieColumns.TMDB_ID + " = ? ",
                new String[] {Integer.toString(movie.getId())},
                null
        );

        Credits credits = mTmdb.getMovieCredits(movie.getId());

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieColumns.TMDB_ID, movie.getId());
        movieValues.put(MovieColumns.TITLE, movie.getTitle());
        movieValues.put(MovieColumns.RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MovieColumns.VOTE_AVERAGE, movie.getVoteAverage());
        movieValues.put(MovieColumns.VOTE_COUNT, movie.getVoteCount());
        movieValues.put(MovieColumns.POPULARITY, movie.getPopularity());
        movieValues.put(MovieColumns.TAGLINE, movie.getTagline());
        movieValues.put(MovieColumns.OVERVIEW, movie.getOverview());
        movieValues.put(MovieColumns.HOMEPAGE, movie.getHomepage());
        movieValues.put(MovieColumns.BACKDROP_PATH, movie.getBackdropPath());
        movieValues.put(MovieColumns.POSTER_PATH, movie.getPosterPath());

        movieValues.put(MovieColumns.BUDGET, movie.getBudget());
        movieValues.put(MovieColumns.REVENUE, movie.getRevenue());

        // Method of storing and retrieving objects attributed to user3208981 from SO
        //  link: http://stackoverflow.com/questions/3142285/saving-arraylist-in-sqlite-database-in-android
        Gson gson = new Gson();
        String genres = gson.toJson(movie.getGenres());
        movieValues.put(MovieColumns.GENRES, genres);

        String productionCompanies = gson.toJson(movie.getProductionCompanies());
        movieValues.put(MovieColumns.PRODUCTION_COMPANIES, productionCompanies);

        String productionCountries = gson.toJson(movie.getProductionCountries());
        movieValues.put(MovieColumns.PRODUCTION_COUNTRIES, productionCountries);

        String spokenLanguages = gson.toJson(movie.getSpokenLanguages());
        movieValues.put(MovieColumns.SPOKEN_LANGUAGES, spokenLanguages);

        String outCredits = gson.toJson(credits);
        movieValues.put(MovieColumns.CREDITS, outCredits);


        if (cursor.moveToFirst()) {
            // Update movie information if it already exists in local db
            locationId = cr.update(Movies.CONTENT_URI,
                    movieValues,
                    MovieColumns.TMDB_ID + " = ? ",
                    new String[] {Integer.toString(movie.getId())}
            );
            Log.d(LOG_TAG, "Movie already in database. Updated.");
            sProgressStatus.post(new Runnable() {
                @Override
                public void run() {
                    sProgressStatus.setText("Updated " + movie.getTitle());
                }
            });
        } else {
            // Otherwise add the new movie
            Uri contentUri = cr.insert(Movies.CONTENT_URI, movieValues);
            locationId = ContentUris.parseId(contentUri);
            Log.d(LOG_TAG, "Movie added to database");
            sProgressStatus.post(new Runnable() {
                @Override
                public void run() {
                    sProgressStatus.setText("Added " + movie.getTitle());
                }
            });
        }
        cursor.close();
        return locationId;
    }

    /**
     * Helper method to handle insertion of list of movies in the local movie db
     *
     * Currently, initialization of
     *
     * @param movieIds list of movie ids
     */
    private void addMovies(final int[] movieIds) {
        Log.d(LOG_TAG, "There are " + movieIds.length + " movies in the list.");
        long start = System.currentTimeMillis();
        for (final int id : movieIds) {
            addMovie(mTmdb.getMovieDetails(id));
            sProgressStatus.post(new Runnable() {
                @Override
                public void run() {
                    sProgressBar.incrementProgressBy(sProgressBar.getMax() / (movieIds.length));
                }
            });
        }
        Log.d(LOG_TAG, "Finished loading movies.");
        long now = System.currentTimeMillis();
        Log.d(LOG_TAG, "Elapsed time: " + movieIds.length + " movies in " + ((now - start) / 1000.0) + " seconds.");
        Log.d(LOG_TAG, "That is around " + ((now - start) / 1000.0)/movieIds.length + " seconds per movie.");
    }


    /*
        AsyncTask methods
     */

    @Override protected void onPreExecute() {
        // Check for internet connection
        // TODO: Make internet connection checks persistent while grabbing data from server
        if (!Network.isNetworkAvailable(mActivity)) {
            //sProgressStatus.setText(mActivity.getString(R.string.status_no_internet));
            //showProgressBar();
            //mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            cancel(true);
            return;
        }

        if (isCancelled()) { return; }

        // Display the progress overlay and disable screen touches only if the database is currently empty
        if (mInitialize) {
            showProgressBar();
            sProgressStatus.setText(mActivity.getString(R.string.status_loading));
        }
        // Set the title of the activity
        setTitle(mCategory);
    }

    @Override protected Void doInBackground(String... category) {
        // Get the movies. All movies are first fetched from TMDB and stored in an ArrayList, then
        //  are inserted into the local database via a quasi bulk insert after all the movies are
        //  fetched
        int[] results;
        if (mInitialize) {
            results = mTmdb.initialize();
        } else {
            if (category[0].equals(PARAMS.CATEGORY.DISCOVER)) {
                results = mTmdb.discover(mSort);
            } else {
                results = mTmdb.getResults(category[0]);
            }
        }
        addMovies(results);

        return null;
    }

    @Override protected void onPostExecute(Void result) {
        PostersFragment.mLoadGuard = true;
        hideProgressBar();
    }
}