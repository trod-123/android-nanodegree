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

package com.thirdarm.popularmovies.data;

/**
 * Created by TROD on 20151010.
 */
public interface MovieProjections {

    // For the grid of posters
    class Results {
        // A list of the names of columns in the movie database table to be queried for the cursor.
        //  Only includes columns containing information that the posters would display
        // This is for leveraging projections
        public static final String[] PROJECTION = new String[]{
                MovieColumns._ID,
                MovieColumns.TMDB_ID,
                MovieColumns.TITLE,
                MovieColumns.RELEASE_DATE,
                MovieColumns.VOTE_AVERAGE,
                MovieColumns.VOTE_COUNT,
                MovieColumns.POPULARITY,
                MovieColumns.POSTER_PATH,
                MovieColumns.FAVORITE
        };

        // These indices are tied to PROJECTION. If PROJECTION changes, these must change.
        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TMDB_ID = 1;
        public static final int COL_MOVIE_TITLE = 2;
        public static final int COL_MOVIE_RELEASE_DATE = 3;
        public static final int COL_MOVIE_VOTE_AVERAGE = 4;
        public static final int COL_MOVIE_VOTE_COUNT = 5;
        public static final int COL_MOVIE_POPULARITY = 6;
        public static final int COL_MOVIE_POSTER_PATH = 7;
        public static final int COL_MOVIE_FAVORITE = 8;
    }

    // For the movie details activity
    class Details {
        public static final String[] PROJECTION = new String[]{
                MovieColumns._ID,
                MovieColumns.TMDB_ID,
                MovieColumns.TITLE,
                MovieColumns.RELEASE_DATE,
                MovieColumns.VOTE_AVERAGE,
                MovieColumns.VOTE_COUNT,
                MovieColumns.OVERVIEW,
                MovieColumns.BACKDROP_PATH,
                MovieColumns.POSTER_PATH,
                MovieColumns.IMDB_ID,
                MovieColumns.COLLECTION,
                MovieColumns.RUNTIME,
                MovieColumns.GENRES,
                MovieColumns.TAGLINE,
                MovieColumns.HOMEPAGE,
                MovieColumns.BUDGET,
                MovieColumns.REVENUE,
                MovieColumns.PRODUCTION_COMPANIES,
                MovieColumns.PRODUCTION_COUNTRIES,
                MovieColumns.SPOKEN_LANGUAGES,
                MovieColumns.IMAGES,
                MovieColumns.RELEASES,
                MovieColumns.TRAILERS,
                MovieColumns.REVIEWS,
                MovieColumns.CREDITS,
                MovieColumns.FAVORITE
        };

        // These indices are tied to PROJECTION. If PROJECTION changes, these must change.
        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TMDB_ID = 1;
        public static final int COL_MOVIE_TITLE = 2;
        public static final int COL_MOVIE_RELEASE_DATE = 3;
        public static final int COL_MOVIE_VOTE_AVERAGE = 4;
        public static final int COL_MOVIE_VOTE_COUNT = 5;
        public static final int COL_MOVIE_OVERVIEW = 6;
        public static final int COL_MOVIE_BACKDROP_PATH = 7;
        public static final int COL_MOVIE_POSTER_PATH = 8;
        public static final int COL_MOVIE_IMDB_ID = 9;
        public static final int COL_MOVIE_COLLECTION = 10;
        public static final int COL_MOVIE_RUNTIME = 11;
        public static final int COL_MOVIE_GENRES = 12;
        public static final int COL_MOVIE_TAGLINE = 13;
        public static final int COL_MOVIE_HOMEPAGE = 14;
        public static final int COL_MOVIE_BUDGET = 15;
        public static final int COL_MOVIE_REVENUE = 16;
        public static final int COL_MOVIE_PRODUCTION_COMPANIES = 17;
        public static final int COL_MOVIE_PRODUCTION_COUNTRIES = 18;
        public static final int COL_MOVIE_SPOKEN_LANGUAGES = 19;
        public static final int COL_MOVIE_IMAGES = 20;
        public static final int COL_MOVIE_RELEASES = 21;
        public static final int COL_MOVIE_TRAILERS = 22;
        public static final int COL_MOVIE_REVIEWS = 23;
        public static final int COL_MOVIE_CREDITS = 24;
        public static final int COL_MOVIE_FAVORITE = 25;
    }
}