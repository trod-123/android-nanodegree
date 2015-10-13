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
import static net.simonvt.schematic.annotation.DataType.Type.BLOB;


/**
 * Created by TROD on 20151005.
 *
 * Movies contract containing names and data types for each column in the local movies database
 */
public interface MovieColumns {

    // TODO: Make a small version of a database table containing only primitive and String objects
    //
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String TMDB_ID = "tmdb_id";
    @DataType(TEXT) String TITLE = "title";
    @DataType(TEXT) String RELEASE_DATE = "release_date";
    @DataType(REAL) String VOTE_AVERAGE = "vote_average";
    @DataType(INTEGER) String VOTE_COUNT = "vote_count";
    @DataType(REAL) String POPULARITY = "popularity";
    @DataType(TEXT) String TAGLINE = "tagline";
    @DataType(TEXT) String OVERVIEW = "overview";
    @DataType(TEXT) String HOMEPAGE = "homepage";
    @DataType(TEXT) String BACKDROP_PATH = "backdrop_path";
    @DataType(TEXT) String POSTER_PATH = "poster_path";

    @DataType(INTEGER) String BUDGET = "budget";
    @DataType(INTEGER) String REVENUE = "revenue";
    @DataType(TEXT) String GENRES = "genres";
    @DataType(TEXT) String PRODUCTION_COMPANIES = "production_companies";
    @DataType(TEXT) String PRODUCTION_COUNTRIES = "production_countries";
    @DataType(TEXT) String SPOKEN_LANGUAGES = "spoken_languages";
    @DataType(BLOB) String CREDITS = "credits";



}
