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

package com.thirdarm.popularmovies.function;

import android.content.Context;

import com.thirdarm.popularmovies.R;
import com.thirdarm.popularmovies.model.MovieDB;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TROD on 20150917.
 *
 * For formatting dates
 */
public class ReleaseDates {

    /**
     * Sets movie release date
     *
     * @param c the activity context
     * @param movie the movie
     * @return the release date prepended with the correct tense of "release"
     */
    public static String setReleaseDate(Context c, MovieDB movie) {
        Date releaseDate;
        String releaseTense = c.getString(R.string.detail_release_date);
        try {
            releaseDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(movie.getReleaseDate());
            if(new Date().before(releaseDate)) {
                releaseTense += "s on: ";
            } else {
                releaseTense += "d on: ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return releaseTense + convertDateFormat(movie.getReleaseDate());
    }

    /**
     * Converts JSON date format (yyyy-mm-dd) into readable format (MMMM dd, yyyy)
     *
     * @param date the date string
     * @return reformatted date string
     */
    public static String convertDateFormat(String date) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        Date oldDate;
        try {
            oldDate = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return targetFormat.format(oldDate);
    }
}
