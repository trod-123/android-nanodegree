package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseIntArray;

import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.WeeklyDateFilter;
import com.zn.expirytracker.data.model.Storage;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Set of helper methods for returning data
 */
public class DataToolbox {

    public static final int[] DEFAULT_ALERT_THRESHOLD = new int[]{0, 1, 2};

    private static final Random mRandomizer = new Random();

    private static final int NO_MAPPING_FOUND = -65536;
    private static final double GREETING_GENERIC_THRESHOLD = 0.7; // above = TOD greet, below = generic
    private static final int GREETING_EVENING_BOUNDS = 17; // 24 hours
    private static final int GREETING_MORNING_BOUNDS = 5;
    private static final int GREETING_AFTERNOON_BOUNDS = 12;

    /**
     * Returns a customized greeting based on the time of day
     *
     * @param context
     * @param name
     * @param currentTimeInMillis
     * @return
     */
    public static String getGreeting(Context context, String name, long currentTimeInMillis) {
        String greeting;
        if (mRandomizer.nextDouble() >= GREETING_GENERIC_THRESHOLD) {
            // if above, set the TOD greeting
            int currentHour = new DateTime(currentTimeInMillis).getHourOfDay();
            if (currentHour >= GREETING_MORNING_BOUNDS && currentHour < GREETING_AFTERNOON_BOUNDS) {
                // morning greeting
                greeting = context.getString(R.string.greeting_morning);
            } else if (currentHour >= GREETING_AFTERNOON_BOUNDS && currentHour < GREETING_EVENING_BOUNDS) {
                // afternoon greeting
                String[] dayGreetings = context.getResources().getStringArray(R.array.greeting_noon_day);
                greeting = dayGreetings[mRandomizer.nextInt(dayGreetings.length)];
            } else {
                // evening greeting
                greeting = context.getString(R.string.greeting_evening);
            }
        } else {
            // otherwise, set the generic greeting
            String[] genericGreetings = context.getResources().getStringArray(R.array.greeting_generic);
            greeting = genericGreetings[mRandomizer.nextInt(genericGreetings.length)];
        }
        if (name != null) {
            return context.getString(R.string.complete_greeting, greeting, name);
        } else {
            return context.getString(R.string.greeting_no_name, greeting);
        }
    }

    /**
     * Returns the full summary based on the filtered food counts, and counts for the current day
     * and the next
     *
     * @param context
     * @param filter
     * @param totalFoodsCountFromFilter
     * @param foodsCountCurrent
     * @param foodsCountNextDay
     * @return
     */
    public static String getFullSummary(Context context, WeeklyDateFilter filter,
                                        int totalFoodsCountFromFilter, int foodsCountCurrent,
                                        int foodsCountNextDay) {
        String filterString = getWeeklyDateFilterString(context, filter).toLowerCase();
        StringBuilder builder = new StringBuilder();
        if (totalFoodsCountFromFilter != 0) {
            builder.append(context.getResources().getQuantityString(R.plurals.at_a_glance_summary,
                    totalFoodsCountFromFilter, totalFoodsCountFromFilter, filterString));
        } else {
            builder.append(context.getString(R.string.at_a_glance_summary_none,
                    null, filterString));
        }
        if (foodsCountCurrent != 0) {
            builder.append(context.getResources().getQuantityString(
                    R.plurals.at_a_glance_summary_postfix, foodsCountCurrent,
                    foodsCountCurrent, context.getString(R.string.date_today)));
        } else {
            builder.append(context.getString(R.string.at_a_glance_summary_postfix_none,
                    null, context.getString(R.string.date_today)));
        }
        if (foodsCountNextDay != 0) {
            builder.append(context.getResources().getQuantityString(
                    R.plurals.at_a_glance_summary_super_postfix, foodsCountNextDay,
                    foodsCountNextDay, context.getString(R.string.date_tomorrow)));
        } else {
            builder.append(context.getString(R.string.at_a_glance_summary_super_postfix_none,
                    null, context.getString(R.string.date_tomorrow)));
        }
        builder.append(".");
        return builder.toString();
    }

    /**
     * Gets the list header for the {@link com.zn.expirytracker.ui.AtAGlanceFragment} class.
     * <p>
     * String returned is in the form "Foods expiring in the next {@code filter}"
     *
     * @param context
     * @param filter
     * @return
     */
    public static String getAtAGlanceListHeader(Context context, WeeklyDateFilter filter) {
        String filterString = getWeeklyDateFilterString(context, filter).toLowerCase();
        return context.getString(R.string.at_a_glance_list_header, filterString);
    }

    /**
     * Get the string message corresponding to the passed {@link WeeklyDateFilter}
     *
     * @param context
     * @param filter
     * @return
     */
    private static String getWeeklyDateFilterString(Context context, WeeklyDateFilter filter) {
        switch (filter) {
            case NEXT_7:
                return context.getString(R.string.date_weekly_filter_7_days);
            case NEXT_14:
                return context.getString(R.string.date_weekly_filter_14_days);
            case NEXT_21:
                return context.getString(R.string.date_weekly_filter_21_days);
            default:
                // We shouldn't come here
                return context.getString(R.string.undefined_generic);
        }
    }

    /**
     * Gets a list of {@link BarEntry} xy mappings, where x is numDays, and y is the frequency of
     * each numDays
     *
     * @param dates
     * @return
     */
    public static List<BarEntry> getTestChartValues(long[] dates, long baseDateInMillis) {
        int[] numDaysUntilCurrent = getNumDaysBetweenDatesArray(dates, baseDateInMillis);
        SparseIntArray data = getIntFrequencies(numDaysUntilCurrent, true);
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(data.keyAt(i), data.valueAt(i)));
        }

        return entries;
    }

    /**
     * For testing. Appends a "current date" to the array that would then be passed to
     * {@link DataToolbox#getIntFrequencies(int[], boolean)}. If the "current date" already exists,
     * then this passes the original array instead
     * <p>
     * Combining two integer arrays source: https://stackoverflow.com/questions/4697255/combine-two-integer-arrays
     *
     * @param data
     * @return
     */
    private static SparseIntArray getDateFrequenciesFromCurrentDate(int[] data) {
        Arrays.sort(data);
        if (Arrays.binarySearch(data, 0) < 0) {
            // Add the 0
            int[] appendedData = new int[data.length + 1];
            System.arraycopy(new int[]{0}, 0, appendedData, 0, 1);
            System.arraycopy(data, 0, appendedData, 1, data.length);
            return getIntFrequencies(appendedData, true);
        } else {
            // 0 already is in there, just pass in the data (sorting is harmless here)
            return getIntFrequencies(data, true);
        }
    }

    /**
     * General utility function that returns the frequencies of each element of a dataset as a
     * {@link SparseIntArray}, where the {@code key} is the element, and the {@code value} is that
     * element's frequency in the dataset
     * <p>
     * If {@code fillInGaps} is true, then all gaps in the dataset will be filled in and set to 0
     * More on SparseArray: https://stackoverflow.com/questions/25560629/sparsearray-vs-hashmap
     *
     * @param data
     * @param fillInGaps
     * @return
     */
    private static SparseIntArray getIntFrequencies(int[] data, boolean fillInGaps) {
        Arrays.sort(data);
        SparseIntArray array = new SparseIntArray();
        if (fillInGaps) {
            for (int i = 0; i < data[data.length - 1]; i++) {
                // create a key for every element in data, including for those elements that
                // don't exist. initialize each value to 0
                array.put(i, 0);
            }
        }
        for (int element : data) {
            if (array.get(element, NO_MAPPING_FOUND) == NO_MAPPING_FOUND) {
                // Add the key if it doesn't already exist, setting its initial value to 1
                // Note this won't be called if fillInGaps is true, as all keys will already exist
                array.put(element, 1);
            } else {
                // If the key already exists, then add 1 to its value by taking it out then putting
                // it back in
                int previousValue = array.get(element);
                array.put(element, ++previousValue);
            }
        }
        return array;
    }

    /**
     * Returns an array of just the dates from the provided food list
     *
     * @return
     */
    private static long[] getAllDatesFromFood() {
        // TODO: Implement
        return null;
    }

    /**
     * Returns the color resource based on the number of days from the current date
     * {@code alertThreshold} holds the edge values used for determining the color resource.
     * Default is {@link DataToolbox#DEFAULT_ALERT_THRESHOLD}
     *
     * @param numDaysFromCurrentDate
     * @param alertThreshold         Must contain 3 unique elements sorted in increasing order
     * @return
     * @throws IllegalArgumentException alertThreshold must contain 3 unique elements sorted
     *                                  in increasing order
     */
    public static int getAlertColorResource(int numDaysFromCurrentDate, int[] alertThreshold)
            throws IllegalArgumentException {
        if (alertThreshold.length != 3) {
            // array must contains 3 elements
            throw new IllegalArgumentException(String.format(
                    "alertThreshold must only contain 3 elements. Found %d elements.",
                    alertThreshold.length));
        }
        if (!(alertThreshold[0] < alertThreshold[1] && alertThreshold[1] < alertThreshold[2] && alertThreshold[0] < alertThreshold[2])) {
            // array must be sorted
            throw new IllegalArgumentException("Elements in alertThreshold must consist of unique " +
                    "values sorted in increasing order");
        }

        if (numDaysFromCurrentDate <= alertThreshold[0]) {
            return R.color.expires_alert_high;
        } else if (numDaysFromCurrentDate > alertThreshold[0] &&
                numDaysFromCurrentDate <= alertThreshold[1]) {
            return R.color.expires_alert_med;
        } else if (numDaysFromCurrentDate > alertThreshold[1] &&
                numDaysFromCurrentDate <= alertThreshold[2]) {
            return R.color.expires_alert_low;
        } else {
            return R.color.expires_alert_none;
        }
    }

    /**
     * Returns the resource id corresponding to the provided Storage location
     *
     * @param storage
     * @return
     */
    public static int getStorageIconResource(Storage storage) {
        switch (storage) {
            case FRIDGE:
                return R.drawable.ic_fridge_black_24dp;
            case FREEZER:
                return R.drawable.ic_cube_black_24dp;
            case PANTRY:
                return R.drawable.ic_door_closed_black_24dp;
            case COUNTER:
                return R.drawable.ic_tabletop_black_24dp;
            case CUSTOM:
            default:
                return R.drawable.ic_add_black_24dp;
        }
    }

    /**
     * Returns the string label for the provided Storage location
     *
     * @param storage
     * @param context
     * @return
     */
    public static String getStorageIconString(Storage storage, Context context) {
        switch (storage) {
            case FRIDGE:
                return context.getString(R.string.storage_location_fridge_label);
            case FREEZER:
                return context.getString(R.string.storage_location_freezer_label);
            case PANTRY:
                return context.getString(R.string.storage_location_pantry_label);
            case COUNTER:
                return context.getString(R.string.storage_location_counter_label);
            case CUSTOM:
            default:
                return context.getString(R.string.storage_location_other_label);
        }
    }

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
     * Returns either the short form of DOW (Sun, Mon, Tue...) if the day is within the current
     * week. Otherwise, returns the short form MONTH DAY (Jan 1)
     *
     * @param startDate
     * @param numDaysFromStartDate
     * @return
     */
    public static String getFormattedShortDateString(DateTime startDate, int numDaysFromStartDate) {
        DateTime currentDate = startDate.plusDays(numDaysFromStartDate);
        if (numDaysFromStartDate < 7) {
            // Sun Mon Tues...
            return currentDate.dayOfWeek().getAsShortText();
        } else {
            // Jan 1, Mar 12, Dec 13...
            return String.format("%s %s", currentDate.monthOfYear().getAsShortText(),
                    currentDate.dayOfMonth().get());
        }
    }

    /**
     * Returns a less short date string, in the form DOW, MON DAY (Wed, Aug 29)
     *
     * @param startDate
     * @param numDaysFromStartDate
     * @return
     */
    public static String getFormattedLessShortDateString(DateTime startDate,
                                                         int numDaysFromStartDate) {
        DateTime currentDate = startDate.plusDays(numDaysFromStartDate);
        return String.format("%s, %s %s", currentDate.dayOfWeek().getAsShortText(),
                currentDate.monthOfYear().getAsShortText(), currentDate.dayOfMonth().get());
    }

    /**
     * Returns a relative date string, that is either "Today", "Tomorrow", or the DOW (or next DOW)
     *
     * @param context
     * @param startDate
     * @param numDaysFromStartDate
     * @return
     */
    public static String getFormattedRelativeDateString(Context context, DateTime startDate,
                                                        int numDaysFromStartDate) {
        DateTime currentDate = startDate.plusDays(numDaysFromStartDate);
        if (numDaysFromStartDate == 0) {
            // today
            return context.getString(R.string.date_today);
        } else if (numDaysFromStartDate == 1) {
            // tomorrow
            return context.getString(R.string.date_tomorrow);
        } else if (numDaysFromStartDate >= 2 && numDaysFromStartDate < 7) {
            // full DOW
            return currentDate.dayOfWeek().getAsText();
        } else if (numDaysFromStartDate >= 7 && numDaysFromStartDate < 14) {
            // next DOW
            return context.getString(R.string.date_next, currentDate.dayOfWeek().getAsText());
        } else {
            // on this day
            return context.getString(R.string.date_generic);
        }
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
     * Helper that gets a field-friendly date string
     *
     * @param dateTime
     * @return
     */
    public static String getFieldFormattedDate(DateTime dateTime) {
        return dateTime.toString("M/d/yyyy", null);
    }

    /**
     * Helper that gets a field-friendly date string
     *
     * @param dateInMillis
     * @return
     */
    public static String getFieldFormattedDate(long dateInMillis) {
        DateTime dateTime = new DateTime(dateInMillis);
        return getFieldFormattedDate(dateTime);
    }

    /**
     * General utility function that returns the passed integer into its ordinal representation
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
     * Returns an array of the number of days between 2 dates, with the reference
     * date provided as {@code baseDateInMillis}. All dates are provided in {@code MILLISECONDS}.
     * Assumes all dates are equal to or greater than {@code baseDateInMillis}
     *
     * @param dates
     * @param baseDateInMillis
     * @return
     */
    private static int[] getNumDaysBetweenDatesArray(long[] dates, long baseDateInMillis) {
        int[] numDaysFromCurrent = new int[dates.length];
        for (int i = 0; i < dates.length; i++) {
            numDaysFromCurrent[i] = DataToolbox.getNumDaysBetweenDates(baseDateInMillis, dates[i]);
        }
        return numDaysFromCurrent;
    }

    /**
     * Returns the number of days between 2 dates, with dates provided in {@code MILLISECONDS}.
     * Assumes the second date is equal to or greater than the first
     *
     * @param dateInMillis1
     * @param dateInMillis2
     * @return
     */
    public static int getNumDaysBetweenDates(long dateInMillis1, long dateInMillis2) {
        DateTime current = getDateTimeStartOfDay(dateInMillis1);
        DateTime compared = getDateTimeStartOfDay(dateInMillis2);

        return getNumDaysBetweenDates(current, compared);
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
    public static DateTime getDateTimeStartOfDay(long timeInMillis) {
        return new DateTime(timeInMillis).withTimeAtStartOfDay();
    }

    /**
     * Helper for neutralizing hours, minutes, seconds, and milliseconds. The time component
     * otherwise confounds date comparisons and calculating the number of days between dates
     *
     * @param timeInMillis
     * @return
     */
    public static long getTimeInMillisStartOfDay(long timeInMillis) {
        return getDateTimeStartOfDay(timeInMillis).getMillis();
    }
}
