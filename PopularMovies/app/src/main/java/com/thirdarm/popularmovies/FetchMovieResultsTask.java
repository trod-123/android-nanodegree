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

import android.os.AsyncTask;

import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.model.MovieDB;

import java.util.ArrayList;

/**
 * Collects and parses JSON data from the TMDB servers via API calls and
 * fills the main UI with posters in a grid view.
 *
 * TODO: Add addMovie function to add movie to database. Call it after MovieDB list has been
 *   populated
 */
public class FetchMovieResultsTask extends AsyncTask<String, Void, ArrayList<MovieDB>> {

    TMDB mTmdb;
    String mSort;

    public FetchMovieResultsTask(TMDB tmdb, String sort) {
        mTmdb = tmdb;
        mSort = sort;
    }

    // Helper method to handle insertion of list of movies in the local movie db
    private void addMovies(ArrayList<MovieDB> movies) {

    }

    // TODO: Refactor this so as to store the movies in the database, and return null
    //  instead
    @Override protected ArrayList<MovieDB> doInBackground(String... category) {
        if (category[0].equals(PARAMS.CATEGORY.DISCOVER)) {
            return mTmdb.discover(mSort);
        } else {
            return mTmdb.getResults(category[0]);
        }

        // method here to store movies in db
    }
}