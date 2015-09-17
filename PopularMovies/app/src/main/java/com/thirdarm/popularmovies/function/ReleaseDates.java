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
 */
public class ReleaseDates {

    /** Sets movie release date */
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

    /** Converts JSON date format (yyyy-mm-dd) into readable format (MMMM dd, yyyy) */
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
