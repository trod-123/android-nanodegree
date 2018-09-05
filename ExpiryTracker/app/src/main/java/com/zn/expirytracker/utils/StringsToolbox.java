package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.res.Resources;

import com.zn.expirytracker.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

/**
 * Set of helper methods for Strings
 */
public class StringsToolbox {

    /**
     * Returns a readable date string formatted based on number of days between 2 dates, with
     * dates provided in {@code MILLISECONDS}. Assumes the second date is equal to or greater
     * than the first
     *
     * @param dateInMillis2
     * @return
     */
    public static String getFormattedExpiryDateString(Context context,
                                                      long dateInMillis1, long dateInMillis2) {
        DateTime current = getDateTimeStartOfDay(dateInMillis1);
        DateTime compared = getDateTimeStartOfDay(dateInMillis2);

        return getFormattedExpiryDateString(context, current, compared);
    }

    /**
     * Returns a readable date string formatted based on number of days between 2 dates, with
     * dates provided as {@code JodaTime.DateTime} objects. Assumes the second date is equal to or
     * greater than the first
     *
     * @param context
     * @param date1
     * @param date2
     * @return
     */
    public static String getFormattedExpiryDateString(Context context,
                                                      DateTime date1, DateTime date2) {

        int diffDays = getNumDaysBetweenDates(date1, date2);
        int diffMonths = getNumMonthsBetweenDates(date1, date2);
        int diffYears = getNumYearsBetweenDates(date1, date2);

        Resources res = context.getResources();

        if (diffDays == 0) {
            // Today (expect Today!)
            return res.getString(R.string.expiry_msg_today);
        } else if (diffDays == 1) {
            // Tomorrow (expect Expires tomorrow!)
            return res.getString(R.string.expiry_msg_tomorrow);
        } else if (diffDays >= 2 && diffDays < 7) {
            // 2-6 days after today Expires soon on DOW)
            return res.getString(R.string.expiry_msg_soon, date2.dayOfWeek().getAsText());
        } else if (diffDays >= 7 && diffDays < 14 && diffMonths == 0 && diffYears == 0) {
            // 7-13 days after today, and within the current month and year
            // (expect Expires next DOW)
            return res.getString(R.string.expiry_msg_next_dow, date2.dayOfWeek().getAsText());
        } else if (diffDays >= 14 && diffMonths == 0 && diffYears == 0) {
            // At least 14 days after today, and within the current month and year
            // (expect Expires on the ORDINAL)
            return res.getString(R.string.expiry_msg_ordinal,
                    convertIntToOrdinal(date2.dayOfMonth().get()));
        } else if (diffDays >= 14 && diffMonths > 0 && diffYears == 0) {
            // 14 days after today, but in the next month, but current year
            // (expect Expires on MONTH DAY)
            return res.getString(R.string.expiry_msg_month_day,
                    date2.monthOfYear().getAsShortText(), date2.dayOfMonth().get());
        } else {
            // 14 days after today, but in the next month and next year
            // (expect Expires on MONTH DAY YEAR)
            return res.getString(R.string.expiry_msg_month_day_year,
                    date2.monthOfYear().getAsShortText(), date2.dayOfMonth().get(),
                    date2.year().get());
        }
    }

    /**
     * Returns the passed integer into its ordinal representation
     * From: https://stackoverflow.com/questions/6810336/is-there-a-way-in-java-to-convert-an-integer-to-its-ordinal
     *
     * @param i
     * @return
     */
    private static String convertIntToOrdinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    /**
     * Helper for getting the number of days between two dates, after neutralizing the time
     * component
     *
     * @param date1
     * @param date2
     * @return
     */
    private static int getNumDaysBetweenDates(DateTime date1, DateTime date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    /**
     * Helper for getting the number of months between two dates, after neutralizing the time
     * component
     *
     * @param date1
     * @param date2
     * @return
     */
    private static int getNumMonthsBetweenDates(DateTime date1, DateTime date2) {
        return Months.monthsBetween(date1, date2).getMonths();
    }

    /**
     * Helper for getting the number of years between two dates, after neutralizing the time
     * component
     *
     * @param date1
     * @param date2
     * @return
     */
    private static int getNumYearsBetweenDates(DateTime date1, DateTime date2) {
        return Years.yearsBetween(date1, date2).getYears();
    }

    /**
     * Helper for neutralizing hours, minutes, seconds, and milliseconds into a JodaTime.DateTime
     * object. The time component otherwise confounds date comparisons and calculating the number
     * of days between dates
     *
     * @param timeInMillis
     * @return
     */
    private static DateTime getDateTimeStartOfDay(long timeInMillis) {
        return new DateTime(timeInMillis).withTimeAtStartOfDay();
    }
}
