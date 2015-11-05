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

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by TROD on 20151005.
 *
 * Movies contract containing names and data types for each column in the local movies database
 */
public interface MovieColumns {

    // Columns loaded from broad movie results
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String TMDB_ID = "tmdb_id";
    @DataType(TEXT) String TITLE = "title";
    @DataType(TEXT) String RELEASE_DATE = "release_date";
    @DataType(REAL) String VOTE_AVERAGE = "vote_average";
    @DataType(INTEGER) String VOTE_COUNT = "vote_count";
    @DataType(REAL) String POPULARITY = "popularity";
    @DataType(TEXT) String OVERVIEW = "overview";
    @DataType(TEXT) String BACKDROP_PATH = "backdrop_path";
    @DataType(TEXT) String POSTER_PATH = "poster_path";

    // Columns loaded from specific movie details (no appends)
    @DataType(TEXT) String IMDB_ID = "imdb_id";
    @DataType(TEXT) String COLLECTION = "collection"; // gson
    @DataType(INTEGER) String RUNTIME = "runtime";
    @DataType(TEXT) String GENRES = "genres"; // gson
    @DataType(TEXT) String TAGLINE = "tagline";
    @DataType(TEXT) String HOMEPAGE = "homepage";
    @DataType(INTEGER) String BUDGET = "budget";
    @DataType(INTEGER) String REVENUE = "revenue";
    @DataType(TEXT) String PRODUCTION_COMPANIES = "production_companies"; // gson
    @DataType(TEXT) String PRODUCTION_COUNTRIES = "production_countries"; // gson
    @DataType(TEXT) String SPOKEN_LANGUAGES = "spoken_languages"; // gson

    // Columns loaded from appends (all stored as gson in db)
    @DataType(TEXT) String IMAGES = "images";
    @DataType(TEXT) String RELEASES = "releases";
    @DataType(TEXT) String TRAILERS = "trailers";
    @DataType(TEXT) String REVIEWS = "reviews";
    @DataType(TEXT) String CREDITS = "credits";

    // Favorites column
    @DataType(INTEGER) String FAVORITE = "favorite";


}
