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
public class FetchMovieDetailsTask extends AsyncTask<String, Void, Void> {

    public interface Callback {
        void onLoadFinished();
    }

    public static final String LOG_TAG = "FetchMovieDetailsTask";

    private Activity mActivity;
    private TMDB mTmdb;
    private int mMovieId;
    private Callback mCallback;

    // references to PostersFragment views
    private RelativeLayout mProgressContainer;
    private View mRootView;

    // allow TMDB to modify the loading status
    public static TextView sProgressStatus;
    public static ProgressBar sProgressBar;


    public FetchMovieDetailsTask(Activity activity, TMDB tmdb, int movieId, View view) {
        mActivity = activity;
        mTmdb = tmdb;
        mMovieId = movieId;
        mRootView = view;
        mCallback = (Callback) activity;

        //mRootView = mActivity.findViewById(android.R.id.content).getRootView();
//        mProgressContainer = (RelativeLayout) mRootView.findViewById(R.id.progress_container);
//        sProgressStatus = (TextView) mRootView.findViewById(R.id.progress);
//        sProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
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
     * Updates movie in local db with additional movie details

     * @param movie the movie to be added
     * @return the long value of the row where the movie was inserted
     */
    private long addMovieDetails(MovieDB movie) {
        ContentResolver cr = mActivity.getContentResolver();
        long locationId = -1;

        Cursor cursor = cr.query(
                Movies.CONTENT_URI,
                new String[]{MovieColumns.TMDB_ID},
                MovieColumns.TMDB_ID + " = ? ",
                new String[]{Integer.toString(movie.getId())},
                null
        );

        ContentValues movieValues = new ContentValues();
        // Method of storing and retrieving objects attributed to user3208981 from SO
        //  link: http://stackoverflow.com/questions/3142285/saving-arraylist-in-sqlite-database-in-android
        Gson gson = new Gson();
        movieValues.put(MovieColumns.IMDB_ID, movie.getImdbId());
        movieValues.put(MovieColumns.COLLECTION, gson.toJson(movie.getBelongsToCollection()));
        movieValues.put(MovieColumns.RUNTIME, movie.getRuntime());
        movieValues.put(MovieColumns.GENRES, gson.toJson(movie.getGenres()));
        movieValues.put(MovieColumns.TAGLINE, movie.getTagline());
        movieValues.put(MovieColumns.HOMEPAGE, movie.getHomepage());
        movieValues.put(MovieColumns.BUDGET, movie.getBudget());
        movieValues.put(MovieColumns.REVENUE, movie.getRevenue());
        movieValues.put(MovieColumns.PRODUCTION_COMPANIES,  gson.toJson(movie.getProductionCompanies()));
        movieValues.put(MovieColumns.PRODUCTION_COUNTRIES, gson.toJson(movie.getProductionCountries()));
        movieValues.put(MovieColumns.SPOKEN_LANGUAGES, gson.toJson(movie.getSpokenLanguages()));
        movieValues.put(MovieColumns.IMAGES, gson.toJson(movie.getImages()));

        movieValues.put(MovieColumns.RELEASES, gson.toJson(movie.getReleases()));
        movieValues.put(MovieColumns.TRAILERS, gson.toJson(movie.getTrailers()));
        movieValues.put(MovieColumns.REVIEWS, gson.toJson(movie.getReviewsResult()));
        movieValues.put(MovieColumns.CREDITS, gson.toJson(movie.getCredits()));


        if (cursor.moveToFirst()) {
            // Update movie information if it already exists in local db. Otherwise, do nothing.
            //  The movie should already be in the db
            locationId = cr.update(Movies.CONTENT_URI,
                    movieValues,
                    MovieColumns.TMDB_ID + " = ? ",
                    new String[]{Integer.toString(movie.getId())}
            );
            Log.d(LOG_TAG, "Movie info updated.");
        }
        cursor.close();
        return locationId;
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
    }

    @Override protected Void doInBackground(String... category) {
        addMovieDetails(mTmdb.getMovieDetails(mMovieId));
        return null;
    }

    @Override protected void onPostExecute(Void result) {
        mCallback.onLoadFinished();
//        hideProgressBar();
    }
}