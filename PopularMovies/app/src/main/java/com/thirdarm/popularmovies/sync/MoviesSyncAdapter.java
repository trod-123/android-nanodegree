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

package com.thirdarm.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.R;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.MovieColumns;
import com.thirdarm.popularmovies.data.MovieProjections;
import com.thirdarm.popularmovies.data.MovieProvider;
import com.thirdarm.popularmovies.model.Images;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResult;
import com.thirdarm.popularmovies.model.Reviews;

import java.util.List;

/**
 * The Movies SyncAdapter
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    // Constants for communicating with main UI
    public static final String SYNC_IN_PROGRESS = "syncInProgress";
    public static final String MOVIE_DETAILS_ADDED = "movieDetailsAdded";
    public static final String INTENT_EXTRA_MOVIE_ID = "movieId";
    public static final String INTENT_EXTRA_MOVIE_TITLE = "movieTitle";
    public static final String SYNC_FINISHED = "syncFinished";

    // Interval at which to sync with movie data online, in form <# s/min> * <# min> (this is done in
    //  seconds, not milliseconds)
    private static final int SYNC_INTERVAL = 60 * 24 * 60; // currently 24 hours
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private static boolean sInitialize = false;
    private static String sCategory = null;
    private static int sPage = -1;
    private static int sMovieId = -1;


    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    // Place async task content here
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // Create TMDB object
        String language = "en"; // TODO: Change this to fetch value from SharedPreferences
        TMDB tmdb = new TMDB(getContext().getString(R.string.movie_api_key), language);

        // If sMovieId has not been specified, then fetch movie results. All movies are first
        //  fetched from TMDB and stored in an ArrayList, then are inserted into the local database
        //  via a quasi bulk insert after all the movies are fetched.
        if (sMovieId == -1) {
            List<MovieDBResult> results;
            if (sInitialize || sPage == -1 || sCategory == null) {
                // Initialize the db upon first use
                results = tmdb.initialize();
                sInitialize = false;
            } else {
                // TODO: Implement discover feature later.
                // Otherwise grab the movies
//            if (sCategory.equals(PARAMS.CATEGORY.DISCOVER)) {
//                results = tmdb.discover(mSort);
//            } else {
                results = tmdb.getResults(sCategory, sPage);
//            }
            }
            addMovieList(results);
            // Grab details of individual movies within results
            for (MovieDBResult movie : results) {
                addMovieDetails(
                        tmdb.getMovieDetails(movie.getId()),
                        tmdb.getMovieImages(movie.getId()),
                        tmdb.getMovieReviews(movie.getId())
                );
            }
        } else {
            // Grab details of individual movies
            addMovieDetails(
                    tmdb.getMovieDetails(sMovieId),
                    tmdb.getMovieImages(sMovieId),
                    tmdb.getMovieReviews(sMovieId)
            );
        }
    }

    /**
     * Adds a movie to the local database
     *
     * @param movie the movie to be added
     * @return the long value of the row where the movie was inserted
     */
    private long addMovie(final MovieDBResult movie) {
        ContentResolver cr = getContext().getContentResolver();
        long locationId;

        Cursor cursor = cr.query(
                MovieProvider.Movies.CONTENT_URI,
                new String[]{MovieColumns.TMDB_ID},
                MovieColumns.TMDB_ID + " = ? ",
                new String[]{Integer.toString(movie.getId())},
                null
        );

        // Store movie info from results object into db, fill in remaining fields not in
        //  results with null to create column, and initially set "false" (0) for favorites
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieColumns.TMDB_ID, movie.getId());
        movieValues.put(MovieColumns.TITLE, movie.getTitle());
        movieValues.put(MovieColumns.RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MovieColumns.VOTE_AVERAGE, movie.getVoteAverage());
        movieValues.put(MovieColumns.VOTE_COUNT, movie.getVoteCount());
        movieValues.put(MovieColumns.POPULARITY, movie.getPopularity());
        movieValues.put(MovieColumns.OVERVIEW, movie.getOverview());
        movieValues.put(MovieColumns.BACKDROP_PATH, movie.getBackdropPath());
        movieValues.put(MovieColumns.POSTER_PATH, movie.getPosterPath());

        if (cursor.moveToFirst()) {
            // Update movie information if it already exists in local db
            locationId = cr.update(MovieProvider.Movies.CONTENT_URI,
                    movieValues,
                    MovieColumns.TMDB_ID + " = ? ",
                    new String[] {Integer.toString(movie.getId())}
            );
        } else {
            // Otherwise add the new movie, nullifying out detail fields
            movieValues.putNull(MovieColumns.IMDB_ID);
            movieValues.putNull(MovieColumns.COLLECTION);
            movieValues.putNull(MovieColumns.RUNTIME);
            movieValues.putNull(MovieColumns.GENRES);
            movieValues.putNull(MovieColumns.TAGLINE);
            movieValues.putNull(MovieColumns.HOMEPAGE);
            movieValues.putNull(MovieColumns.BUDGET);
            movieValues.putNull(MovieColumns.REVENUE);
            movieValues.putNull(MovieColumns.PRODUCTION_COMPANIES);
            movieValues.putNull(MovieColumns.PRODUCTION_COUNTRIES);
            movieValues.putNull(MovieColumns.SPOKEN_LANGUAGES);
            movieValues.putNull(MovieColumns.IMAGES);
            movieValues.putNull(MovieColumns.RELEASES);
            movieValues.putNull(MovieColumns.TRAILERS);
            movieValues.putNull(MovieColumns.REVIEWS);
            movieValues.putNull(MovieColumns.CREDITS);

            movieValues.put(MovieColumns.FAVORITE, 0);

            Uri contentUri = cr.insert(MovieProvider.Movies.CONTENT_URI, movieValues);
            locationId = ContentUris.parseId(contentUri);
        }
        // Prefetch the images
        preloadPicassoBackdrop(movie.getBackdropPath());
        preloadPicassoPoster(movie.getPosterPath());

        cursor.close();
        return locationId;
    }

    /**
     * Helper method to handle insertion of list of movies in the local movie db
     *
     * @param movieDBResults list of movie ids
     */
    private void addMovieList(final List<MovieDBResult> movieDBResults) {
//        Log.d(LOG_TAG, "There are " + movieDBResults.size() + " movies in the list.");
        long start = System.currentTimeMillis();
        for (final MovieDBResult result : movieDBResults) {
            addMovie(result);
        }
//        Log.d(LOG_TAG, "Finished loading movies.");
        long now = System.currentTimeMillis();
//        Log.d(LOG_TAG, "Elapsed time: " + movieDBResults.size() + " movies in " + ((now - start) / 1000.0) + " seconds.");
//        Log.d(LOG_TAG, "That is around " + ((now - start) / 1000.0) / movieDBResults.size() + " seconds per movie.");
    }

    /**
     * Updates movie in local db with additional movie details. Notifies main thread if process
     *  is complete.
     *
     * @param movie the movie to be added
     * @return the long value of the row where the movie was inserted
     */
    private long addMovieDetails(MovieDB movie, Images images, List<Reviews> reviews) {
        ContentResolver cr = getContext().getContentResolver();
        long locationId = -1;

        Cursor cursor = cr.query(
                MovieProvider.Movies.CONTENT_URI,
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

        movieValues.put(MovieColumns.IMAGES, gson.toJson(images));
        movieValues.put(MovieColumns.RELEASES, gson.toJson(movie.getReleases()));
        movieValues.put(MovieColumns.TRAILERS, gson.toJson(movie.getTrailers()));
        movieValues.put(MovieColumns.REVIEWS, gson.toJson(reviews));
        movieValues.put(MovieColumns.CREDITS, gson.toJson(movie.getCredits()));

        if (cursor.moveToFirst()) {
            // Update movie information if it already exists in local db. Otherwise, do nothing.
            //  The movie should already be in the db
            locationId = cr.update(MovieProvider.Movies.CONTENT_URI,
                    movieValues,
                    MovieColumns.TMDB_ID + " = ? ",
                    new String[]{Integer.toString(movie.getId())}
            );
            // Notify main UI movie details have been added to db
            Intent intent = new Intent(MOVIE_DETAILS_ADDED)
                    .putExtra(INTENT_EXTRA_MOVIE_ID, movie.getId())
                    .putExtra(INTENT_EXTRA_MOVIE_TITLE, movie.getTitle());
            getContext().sendBroadcast(intent);
        }
        cursor.close();
        return locationId;
    }

    /**
     * Prefetches main movie backdrop
     *
     * @param imagePath path to the backdrop
     */
    private void preloadPicassoBackdrop(String imagePath) {
        Picasso.with(getContext())
                .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 + imagePath)
                .fetch();
    }

    /**
     * Prefetches main movie poster
     *
     * @param imagePath path to the poster
     */
    private void preloadPicassoPoster(String imagePath) {
        for (String size : new String[] {IMAGE.SIZE.POSTER.w342, IMAGE.SIZE.POSTER.w500}) {
            Picasso.with(getContext())
                    .load(URL.IMAGE_BASE + size + imagePath)
                    .fetch();
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately. The kind of sync will depend on
     *  the parameters provided during the call.
     *
     * @param context The context used to access the account service
     * @param category The category of movies which to fetch. Used pretty much only if the
     *                 method were called directly (i.e. not from initializeSyncAdapter()). Use
     *                 'null' if not used.
     * @param page The page of results to query. Used pretty much only if the method were called
     *             directly (i.e. not from initializeSyncAdapter()). Use '-1' if not used.
     * @param movieId The id of the specific movie whose details to fetch. Used only if specific
     *                movie details would need to be fetched rather than entire results. Use '-1'
     *                if not used.
     */
    public static void syncImmediately(Context context, String category, int page, int movieId) {
        sCategory = category;
        sPage = page;
        sMovieId = movieId;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things. If this were called from
     * initializeSyncAdapter(), return the account and do nothing else. If this were called from
     * syncImmediately(), then return the account for the requestSync() method and proceed with
     * the initialization.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            // It is here through which the sync happens
            sInitialize = true;
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Configure sync settings here and start the sync
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started. Here, not needed to define category since
         *  upon initialization, movies of all categories will be loaded. During initialization,
         *  a fixed number of pages of results will be queried, and specific movie details will not
         *  be fetched, so their values do not really matter.
         */
        syncImmediately(context, sCategory, sPage, sMovieId);
    }

    /**
     * Initialize by getting the account, and then if it exists, start sync. This method is called
     *  by the MainActivity, and everything happens here (calls chain of helper methods ultimately
     *  leading to syncImmediately())
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
