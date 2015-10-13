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

package com.thirdarm.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.thirdarm.popularmovies.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TROD on 20150917.
 *
 * For formatting dates
 */
public class ReleaseDates {

    private static final String LOG_TAG = "Utilities/ReleaseDates";

    /**
     * Sets movie release date
     *
     * @param c the activity context
     * @param date the release date
     * @return the release date prepended with the correct tense of "release"
     */
    public static String setReleaseDate(Context c, String date) {
        Date releaseDate;
        String releaseTense = c.getString(R.string.detail_release_date);
        try {
            releaseDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(date);
            if(new Date().before(releaseDate)) {
                releaseTense += "s on: ";
            } else {
                releaseTense += "d on: ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return releaseTense + convertDateFormat(date);
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

    /**
     * Calculates a minimum and maximum date, specifying a range of dates relative to the
     *  current date.
     *
     * @param minimum_threshold the minimum number of days from the current date (lower bound)
     *                          can be negative
     * @param maximum_threshold the maximum number of days from the current date (upper bound)
     *                          can be negative
     * @return a string array containing the minimum and maximum dates
     */
    public static String[] getDateRangeFromToday(int minimum_threshold, int maximum_threshold) {
        String minimum, maximum;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, minimum_threshold - 1);
        minimum = c.get(Calendar.YEAR) + "-" +
                String.format("%02d", (c.get(Calendar.MONTH) + 1)) + "-" +
                String.format("%02d", (c.get(Calendar.DAY_OF_MONTH)));

        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, maximum_threshold - 1);
        maximum = c.get(Calendar.YEAR) + "-" +
                String.format("%02d", (c.get(Calendar.MONTH) + 1)) + "-" +
                String.format("%02d", (c.get(Calendar.DAY_OF_MONTH)));

        return new String[] {minimum, maximum};
    }
}
