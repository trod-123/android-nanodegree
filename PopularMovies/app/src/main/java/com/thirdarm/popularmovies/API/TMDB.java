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

package com.thirdarm.popularmovies.API;

import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.constant.VALUES;
import com.thirdarm.popularmovies.model.Images;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResult;
import com.thirdarm.popularmovies.model.Results;
import com.thirdarm.popularmovies.model.Reviews;
import com.thirdarm.popularmovies.model.ReviewsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by TROD on 20150910.
 *
 * Class used to call APIService with specified information
 *
 * BIG EDIT: This should be meant to be used as a single instance, now that local databases
 *  have been implemented. Previously, movie objects needed to be persistent through this
 *  class, but now that content providers have been used, a single instance of this class
 *  just for fetching movie data should be sufficient. Anyway, information that is saved in
 *  this class as fields will be thrown away when the app closes. So, if we were to store the
 *  ids for each of the movie objects fetched online, they would be wiped when the app closes.
 * Also, because saving will not be done here, this class will be used primarily as an
 *  interface with interacting with the TMDB API. As such, gathering results (i.e. lists of
 *  TMDB movie ids) and gathering specific movie data will remain separate, and their
 *  conjoined use will need to be done through an Async Task.
 */
public class TMDB {

    private final String LOG_TAG = "Movies/TMDB";

    // API information
    public final String APPENDS_MOVIEDB =
            VALUES.APPENDS.RELEASES +
                    VALUES.APPENDS.TRAILERS +
                    VALUES.APPENDS.CREDITS;

    private String mApiKey;
    private String mLanguage;
    private static final int NUMBER_MOVIES_PER_CATEGORY = 20;
    private APIService api;

    public TMDB(String key, String language_code) {
        mApiKey = key;
        mLanguage = language_code;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(APIService.class);
    }

    /**
     * Initialize database upon first run. Load the first NUMBER_MOVES_PER_CATEGORY movies of
     *  each category into the db
     *
     * BUG: Note that in the end, there may be a different number of movies showing up for each
     *  category because a single movie may be part of multiple categories. At the end,
     *  NUMBER_MOVIES_PER_CATEGORY is the minimum number of movies that will be shown for each
     *  category.
     *
     * FIX: STANDARDIZE THE NUMBER OF MOVIES SHOWN THROUGH THE DB.QUERY()FUNCTION!
     *
     * @return a long list of MovieDBResult objects encompassing all of the categories
     */
    public ArrayList<MovieDBResult> initialize() {
        // Use an ArrayList so results can continue to be added when needed
        ArrayList<MovieDBResult> movieDBResults = new ArrayList<>();
        String[] categories = new String[] {PARAMS.CATEGORY.POPULAR, PARAMS.CATEGORY.PLAYING,
                PARAMS.CATEGORY.TOP, PARAMS.CATEGORY.UPCOMING};
        for (int i = 0; i < categories.length; i++) {
            for (int j = 1; movieDBResults.size() < NUMBER_MOVIES_PER_CATEGORY * (i+1); j++) {
                List<MovieDBResult> results = getResults(categories[i], j);
                for (int k = 0; movieDBResults.size() < NUMBER_MOVIES_PER_CATEGORY * (i+1) && k < results.size(); k++) {
                    if (!movieDBResults.contains(results.get(k)))
                        movieDBResults.add(results.get(k));
                }
            }
        }
        return movieDBResults;
    }

//    /**
//     * Preserve this method for gathering movie id lists for single categories. This method would
//     *  be called by initialize() x number of times, where x is the number of categories for which
//     *  to search.
//     *
//     * BUG: This will not account for movies that belong in multiple categories.
//     *
//     * @param category category of movies for which to retrieve
//     * @return list of movie ids
//     */
//    public int[] refresh(String category) {
//        // Use an ArrayList so ids can continue to be added when needed
//        ArrayList<Integer> movieIds = new ArrayList<>();
//        for (int i = 1; movieIds.size() < NUMBER_MOVIES_PER_CATEGORY; i++) {
//            setPage(i);
//            int[] results = getResults(category);
//            for (int j = 0; movieIds.size() < NUMBER_MOVIES_PER_CATEGORY && j < results.length; j++) {
//                if (!movieIds.contains(results[j]))
//                    movieIds.add(results[j]);
//            }
//        }
//        // set page back to 1
//        setPage(1);
//
//        return Conversions.convertIntegers(movieIds);
//    }

    /**
     * Generates results for /discover
     *
     * TODO: Implement this feature later.
     *
     * @param sort method to sort discover results
     * @return list of movies
     */
    public List<MovieDBResult> discover(String sort, int page) {
        return getResults(api.discover(mApiKey, sort, mLanguage, page));
    }

    /**
     * Generates results for /movie/{category}
     *
     * @param category the category of movies to display
     * @return a list of MovieDB ids
     */
    public List<MovieDBResult> getResults(String category, int page) {
        return getResults(api.getResults(category, mApiKey, mLanguage, page));
    }

    /**
     * Fetches results whose original language is mLanguage.
     *
     * @param response Callback response from APIService
     * @return a list of MovieDB ids
     */
    public List<MovieDBResult> getResults(Call<Results> response) {
        try {
            List<MovieDBResult> results = response.execute().body().getMovieDBResults();

            // Drop movies from the results list whose original language isn't mLanguage
            Iterator<MovieDBResult> iterator = results.iterator();
            while (iterator.hasNext()) {
                MovieDBResult current = iterator.next();
                if (!current.getOriginalLanguage().equals(mLanguage)) {
                    iterator.remove();
                }
            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates specific MovieDB object
     *
     * @param id the TMDB movie id
     * @return a movie object
     */
    public MovieDB getMovieDetails(int id) {
        Call<MovieDB> response = api.getMovieDetails(id, mApiKey, APPENDS_MOVIEDB);
        try {
            MovieDB movie = response.execute().body();
            if (movie != null) {
                return movie;
            } else {
                // Re-run method until movie object is received
                return getMovieDetails(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates images for a specific movie id
     *
     * @param id the TMDB movie id
     * @return an images object
     */
    public Images getMovieImages(int id) {
        Call<Images> response = api.getMovieImages(id, mApiKey);
        try {
            Images images = response.execute().body();
            if (images != null) {
                return images;
            } else {
                return getMovieImages(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates reviews for a specific movie id
     *
     * @param id the TMDB movie id
     * @return a list of reviews
     */
    public List<Reviews> getMovieReviews(int id) {
        Call<ReviewsResult> response = api.getMovieReviews(id, mApiKey);
        try {
            ReviewsResult reviewsResult = response.execute().body();
            if (reviewsResult != null) {
                return reviewsResult.getResults();
            } else {
                return getMovieReviews(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}