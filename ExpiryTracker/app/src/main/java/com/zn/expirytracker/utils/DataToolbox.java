package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.WeeklyDateFilter;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.Storage;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.zn.expirytracker.utils.Constants.GREETING_AFTERNOON_BOUNDS;
import static com.zn.expirytracker.utils.Constants.GREETING_EVENING_BOUNDS;
import static com.zn.expirytracker.utils.Constants.GREETING_GENERIC_THRESHOLD;
import static com.zn.expirytracker.utils.Constants.GREETING_MORNING_BOUNDS;

/**
 * Set of helper methods for returning data
 */
public class DataToolbox {

    public static final int[] DEFAULT_ALERT_THRESHOLD = new int[]{0, 1, 2};

    private static final Random mRandomizer = new Random();

    private static final int NO_MAPPING_FOUND = -65536;

    public static final int POSITION_NO_FOOD = -1;
    public static final int NO_STORAGE_ICON_RESOURCE = -1;

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
        if (name != null && !name.trim().isEmpty()) {
            return context.getString(R.string.complete_greeting, greeting, name);
        } else {
            return context.getString(R.string.greeting_no_name, greeting);
        }
    }

    /**
     * Returns a partial summary based on only the filtered food counts for only the current
     * and next day
     * <p>
     * For example: You have 2 foods expiring today, and no foods expiring tomorrow.
     *
     * @param context
     * @param numDaysFilter
     * @param foodsCountCurrent
     * @param foodsCountNextDay
     * @return
     */
    public static String getPartialSummary(Context context, int numDaysFilter,
                                           int foodsCountCurrent, int foodsCountNextDay) {
        String emptyCurrentDayString = context.getString(R.string.at_a_glance_summary_none_no_within,
                null, context.getString(R.string.date_today).toLowerCase());
        String notEmptyCurrentDayString = context.getResources().getQuantityString(
                R.plurals.at_a_glance_summary_no_within, foodsCountCurrent, foodsCountCurrent,
                context.getString(R.string.date_today).toLowerCase());
        if (numDaysFilter == 0) {
            // Only foods expiring on the current date. foodsCountNextDay irrelevant here
            if (foodsCountCurrent != 0) {
                return notEmptyCurrentDayString;
            } else {
                return emptyCurrentDayString;
            }
        } else {
            // Only foods expiring on the current or next date
            String emptyNextDayString = context.getString(
                    R.string.at_a_glance_summary_super_postfix_none, null,
                    context.getString(R.string.date_tomorrow).toLowerCase());
            String notEmptyNextDayString = context.getResources().getQuantityString(
                    R.plurals.at_a_glance_summary_super_postfix, foodsCountNextDay,
                    foodsCountNextDay, context.getString(R.string.date_tomorrow).toLowerCase());
            if (foodsCountCurrent == 0 && foodsCountNextDay == 0) {
                // No foods either date
                return context.getString(R.string.at_a_glance_summary_none_no_within, null,
                        context.getString(R.string.date_today_or_tomorrow).toLowerCase());
            } else if (foodsCountCurrent == 0) {
                // No foods current date, but yes foods next date
                return String.format("%s%s", emptyCurrentDayString, notEmptyNextDayString);
            } else if (foodsCountNextDay == 0) {
                // Foods current date, but no foods next date
                return String.format("%s%s", notEmptyCurrentDayString, emptyNextDayString);
            } else {
                // Foods on both current and next dates, append the tw0
                return String.format("%s%s", notEmptyCurrentDayString, notEmptyNextDayString);
            }
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
        return getFullSummaryHelper(context,
                getWeeklyDateFilterString(context, filter).toLowerCase(),
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay);
    }

    /**
     * Returns the full summary based on the filtered food counts, and counts for the current day
     * and the next
     *
     * @param context
     * @param numDaysFilter
     * @param totalFoodsCountFromFilter
     * @param foodsCountCurrent
     * @param foodsCountNextDay
     * @return
     */
    public static String getFullSummary(Context context, int numDaysFilter,
                                        int totalFoodsCountFromFilter, int foodsCountCurrent,
                                        int foodsCountNextDay) {
        String filterString;
        if (numDaysFilter == 6) {
            filterString = context.getString(R.string.date_weekly_filter_7_days_text);
        } else {
            filterString = context.getString(R.string.date_filter_x_days, numDaysFilter);
        }
        return getFullSummaryHelper(context, filterString.toLowerCase(),
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay);
    }

    /**
     * Helper to generate the full summary
     *
     * @param context
     * @param filterString
     * @param totalFoodsCountFromFilter
     * @param foodsCountCurrent
     * @param foodsCountNextDay
     * @return
     */
    private static String getFullSummaryHelper(Context context, String filterString,
                                               int totalFoodsCountFromFilter, int foodsCountCurrent,
                                               int foodsCountNextDay) {
        // Plurals don't have any difference for 0 quantities
        // https://stackoverflow.com/questions/5651902/android-plurals-treatment-of-zero
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
                    foodsCountCurrent, context.getString(R.string.date_today).toLowerCase()));
        } else {
            builder.append(context.getString(R.string.at_a_glance_summary_postfix_none,
                    null, context.getString(R.string.date_today).toLowerCase()));
        }
        if (foodsCountNextDay != 0) {
            builder.append(context.getResources().getQuantityString(
                    R.plurals.at_a_glance_summary_super_postfix, foodsCountNextDay,
                    foodsCountNextDay, context.getString(R.string.date_tomorrow).toLowerCase()));
        } else {
            builder.append(context.getString(R.string.at_a_glance_summary_super_postfix_none,
                    null, context.getString(R.string.date_tomorrow).toLowerCase()));
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
                return context.getString(R.string.date_weekly_filter_7_days_text);
            case NEXT_14:
                return context.getString(R.string.date_weekly_filter_14_days_text);
            case NEXT_21:
                return context.getString(R.string.date_weekly_filter_21_days_text);
            default:
                // We shouldn't come here
                return context.getString(R.string.undefined_generic);
        }
    }

    public static int getFoodPositionFromId(List<Food> foodsList, long id) {
        if (foodsList != null) {
            for (int i = 0; i < foodsList.size(); i++) {
                if (foodsList.get(i).get_id() == id) return i;
            }
        }
        return POSITION_NO_FOOD;
    }

    /**
     * Gets the date in millis of the {@code filter} added to the {@code currentDateTimeStartOfDay}
     *
     * @param filter
     * @param currentDateTimeStartOfDay
     * @return
     */
    public static long getDateBoundsFromFilter(WeeklyDateFilter filter,
                                               DateTime currentDateTimeStartOfDay) {
        int plusDays;
        switch (filter) {
            case NEXT_7:
                plusDays = 6;
                break;
            case NEXT_14:
                plusDays = 13;
                break;
            case NEXT_21:
                plusDays = 20;
                break;
            default:
                plusDays = 0;
        }
        return getDateBounds(currentDateTimeStartOfDay, plusDays);
    }

    /**
     * Gets the date in millis after the number of {@code plusDays} provided
     *
     * @param currentDateTimeStartOfDay
     * @param plusDays
     * @return
     */
    public static long getDateBounds(DateTime currentDateTimeStartOfDay, int plusDays) {
        return currentDateTimeStartOfDay.plusDays(plusDays).getMillis();
    }

    /**
     * Gets the date in millis after the number of {@code plusDays} provided
     *
     * @param baseDateStartOfDay
     * @param plusDays
     * @return
     */
    public static long getDateBounds(long baseDateStartOfDay, int plusDays) {
        return getDateBounds(new DateTime(baseDateStartOfDay), plusDays);
    }

    /**
     * Returns the total number of food expiring within the given {@code filter}, based on the
     * {@code barChartEntries}. {@code barChartEntries} need not be pre-filtered; the full scope
     * of entries can be passed
     *
     * @param filter
     * @param barChartEntries
     * @return
     */
    public static int getTotalFoodsCountFromFilter(WeeklyDateFilter filter,
                                                   @NonNull List<BarEntry> barChartEntries,
                                                   boolean includeNegativeXValues) {
        int limit;
        switch (filter) {
            case NEXT_7:
                limit = 7;
                break;
            case NEXT_14:
                limit = 14;
                break;
            case NEXT_21:
            default:
                limit = 21;
        }
        int index = includeNegativeXValues ? 0 : getStartingPositiveIndex(barChartEntries, limit);
        limit += index; // offset limit so it's based off where x=0
        return getTotalFrequencyCounts(barChartEntries, index, limit);
    }

    /**
     * Used to disable the max index used for finding starting indices and total value counts
     */
    public static final int NO_INDEX_LIMIT = Integer.MAX_VALUE;

    /**
     * Gets the index of the first non-negative X-value. Assumes {@code barChartEntries} x-values
     * are sorted in increasing order
     *
     * @param barChartEntries List sorted in increasing order
     * @param limit
     * @return
     */
    public static int getStartingPositiveIndex(List<BarEntry> barChartEntries, int limit) {
        int entriesSize = barChartEntries.size();
        int index = 0;
        while (index < limit && index < entriesSize - 1) {
            if (barChartEntries.get(index).getX() >= 0) break;
            index++;
            if (limit != NO_INDEX_LIMIT)
                limit++; // offset limit since we haven't reached point where x = 0
        }
        return index;
    }

    /**
     * Gets the index of the first non-negative key-value. Assumes {@code data} keys are sorted in
     * increasing order
     *
     * @param data
     * @param limit
     * @return
     */
    public static int getStartingPositiveIndex(SparseIntArray data, int limit) {
        int entriesSize = data.size();
        int index = 0;
        while (index < limit && index < entriesSize - 1) {
            if (data.get(index) >= 0) break;
            index++;
            if (limit != NO_INDEX_LIMIT)
                limit++; // offset limit since we haven't reached point where key is 0
        }
        return index;
    }

    /**
     * Returns the total sum of y-values, starting from a provided {@code startIndex} and iterating
     * through the end of the list, or a provided {@code limit}, whichever comes first
     * <p>
     * Pass {@link DataToolbox#NO_INDEX_LIMIT} for {@code limit} to disable the limit and stop
     * iterating at the end of the list
     *
     * @param barChartEntries
     * @param startIndex
     * @param limit
     * @return
     */
    public static int getTotalFrequencyCounts(List<BarEntry> barChartEntries,
                                              int startIndex, int limit) {
        int entriesSize = barChartEntries.size();
        int totalFoodsCountFromFilter = 0;
        for (int i = startIndex; i < limit && i < entriesSize; i++) {
            totalFoodsCountFromFilter += barChartEntries.get(i).getY();
        }
        return totalFoodsCountFromFilter;
    }

    /**
     * Returns the total sum of values, starting from a provided {@code startIndex} and iterating
     * through the end of the array, or a provided {@code limit}, whichever comes first
     * <p>
     * Pass {@link DataToolbox#NO_INDEX_LIMIT} for {@code limit} to disable the limit and stop
     * iterating at the end of the array
     *
     * @param data
     * @param startIndex
     * @param limit
     * @return
     */
    public static int getTotalFrequencyCounts(SparseIntArray data, int startIndex, int limit) {
        int entriesSize = data.size();
        int totalFoodsCountFromFilter = 0;
        for (int i = startIndex; i < limit && i < entriesSize; i++) {
            totalFoodsCountFromFilter += data.get(i);
        }
        return totalFoodsCountFromFilter;
    }

    private static final int NO_MAX_SIZE_INT_FREQUENCIES = 0;

    /**
     * Gets a list of {@link BarEntry} xy mappings, where x is numDays, and y is the frequency of
     * each numDays
     *
     * @param foods
     * @param baseDateInMillis
     * @param filter
     * @return
     */
    public static List<BarEntry> getBarEntries(List<Food> foods, long baseDateInMillis,
                                               WeeklyDateFilter filter) {
        int maxSize;
        switch (filter) {
            case NEXT_7:
                maxSize = 7;
                break;
            case NEXT_14:
                maxSize = 14;
                break;
            case NEXT_21:
                maxSize = 21;
                break;
            default:
                maxSize = NO_MAX_SIZE_INT_FREQUENCIES;
        }
        long[] dates = getAllExpiryDatesFromFood(foods);
        int[] numDaysUntilCurrent = getNumDaysBetweenDatesArray(dates, baseDateInMillis);
        SparseIntArray data = getIntFrequencies(numDaysUntilCurrent, true, maxSize);
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(data.keyAt(i), data.valueAt(i)));
        }
        return entries;
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
     * @param maxSize    Provides a guaranteed size for the array, if {@code fillInGaps == true}
     * @return
     */
    private static SparseIntArray getIntFrequencies(int[] data, boolean fillInGaps, int maxSize) {
        int limit = maxSize == NO_MAX_SIZE_INT_FREQUENCIES ? data.length : maxSize;
        if (data.length > 0) {
            Arrays.sort(data);
            SparseIntArray array = new SparseIntArray();
            if (fillInGaps) {
                for (int i = 0; i < limit; i++) {
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
        } else {
            return new SparseIntArray();
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
     * @param foods
     * @param baseDateInMillis
     * @param fillInGaps
     * @return
     */
    public static SparseIntArray getIntFrequencies(List<Food> foods, long baseDateInMillis,
                                                   boolean fillInGaps, int maxSize) {
        long[] dates = getAllExpiryDatesFromFood(foods);
        int[] numDaysUntilCurrent = getNumDaysBetweenDatesArray(dates, baseDateInMillis);
        return getIntFrequencies(numDaysUntilCurrent, fillInGaps, maxSize);
    }

    /**
     * Returns the highest number of items expiring in a single day from a {@link List} of
     * {@link Food}
     *
     * @param foods
     * @param baseDateInMillis
     * @return
     */
    private static int getHighestDailyFrequency(List<Food> foods, long baseDateInMillis,
                                                boolean fillInGaps, boolean includeNegativeKeys) {
        SparseIntArray frequencies = getIntFrequencies(foods, baseDateInMillis, fillInGaps,
                NO_MAX_SIZE_INT_FREQUENCIES);
        int highest = 0;
        int startIndex = includeNegativeKeys ? 0 :
                getStartingPositiveIndex(frequencies, NO_INDEX_LIMIT);
        for (int i = startIndex; i < frequencies.size(); i++) {
            int current = frequencies.get(i);
            if (current > highest) {
                highest = current;
            }
        }
        return highest;
    }

    /**
     * Returns the highest number of items expiring in a single day from a {@link List} of
     * {@link Food}
     *
     * @param foods
     * @param baseDateInMillis
     * @return
     */
    public static int getHighestDailyFrequency(List<Food> foods, long baseDateInMillis,
                                               boolean includeNegativeKeys) {
        return getHighestDailyFrequency(foods, baseDateInMillis, true, includeNegativeKeys);
    }

    /**
     * Returns an array of just the dates from the provided food list
     *
     * @return
     */
    private static long[] getAllExpiryDatesFromFood(List<Food> foods) {
        long[] dates = new long[foods.size()];
        for (int i = 0; i < foods.size(); i++) {
            dates[i] = foods.get(i).getDateExpiry();
        }
        return dates;
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
                return R.drawable.ic_add_black_24dp;
            default:
                return NO_STORAGE_ICON_RESOURCE;
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
                return context.getString(R.string.storage_location_other_label);
            default:
                return context.getString(R.string.storage_location_none_label);
        }
    }

    /**
     * Returns a readable date string formatted based on number of days between 2 dates, with
     * dates provided in {@code MILLISECONDS}. Assumes the second date is equal to or greater
     * than the first
     * <p>
     * Example result: Expires soon on Tuesday
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
     * Returns a full date string, in the form MON DAY, YEAR (Jan 4, 2018)
     *
     * @param dateInMillis
     * @return
     */
    public static String getFormattedFullDateString(long dateInMillis) {
        DateTime date = new DateTime(dateInMillis);
        return getFormattedFullDateString(date);
    }

    /**
     * Returns a full date string, in the form MON DAY, YEAR (Jan 4, 2018)
     *
     * @param date
     * @return
     */
    public static String getFormattedFullDateString(DateTime date) {
        return String.format("%s %s, %s", date.monthOfYear().getAsShortText(),
                date.dayOfMonth().get(), date.year().get());
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
            return context.getString(R.string.date_today).toLowerCase();
        } else if (numDaysFromStartDate == 1) {
            // tomorrow
            return context.getString(R.string.date_tomorrow).toLowerCase();
        } else if (numDaysFromStartDate >= 2 && numDaysFromStartDate < 7) {
            // full DOW
            return currentDate.dayOfWeek().getAsText();
        } else if (numDaysFromStartDate >= 7 && numDaysFromStartDate < 14) {
            // next DOW
            return context.getString(R.string.date_next, currentDate.dayOfWeek().getAsText());
        } else {
            // on this day
            return context.getString(R.string.date_generic).toLowerCase();
        }
    }

    /**
     * Returns a readable date string formatted based on number of days between 2 dates, with
     * dates provided as {@code JodaTime.DateTime} objects. Assumes the second date is equal to or
     * greater than the first.
     * <p>
     * Example result: Expires soon on Tuesday
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
     * Helper that gets a field-friendly date string in the form "M/d/yyyy"
     *
     * @param dateTime
     * @return
     */
    public static String getFieldFormattedDate(DateTime dateTime) {
        return dateTime.toString("M/d/yyyy", null);
    }

    /**
     * Helper that gets a field-friendly date string in the form "M/d/yyyy"
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

    /**
     * Returns true if the selected date is equal to or greater than the current date
     *
     * @param selectedDateInMillis
     * @param currentDateInMillis
     * @return
     */
    public static boolean compareTwoDates(long selectedDateInMillis, long currentDateInMillis) {
        return selectedDateInMillis >= currentDateInMillis;
    }

    /**
     * Does what it says
     *
     * @return {@code int} - a random animal drawable resource id
     */
    public static int getRandomAnimalDrawableId() {
        int[] drawableIds = new int[]{
                R.drawable.ic_cat_black_24dp,
                R.drawable.ic_dog_black_24dp,
                R.drawable.ic_duck_black_24dp,
                R.drawable.ic_easter_black_24dp,
                R.drawable.ic_fish_black_24dp,
                R.drawable.ic_poop_black_24dp,
        };

        return drawableIds[mRandomizer.nextInt(drawableIds.length - 1)];
    }

    /**
     * Does what it says
     *
     * @param id
     * @return
     */
    public static String getAnimalContentDescriptionById(int id) {
        switch (id) {
            case R.drawable.ic_cat_black_24dp:
                return "meow";
            case R.drawable.ic_dog_black_24dp:
                return "woof";
            case R.drawable.ic_duck_black_24dp:
                return "quack";
            case R.drawable.ic_easter_black_24dp:
                return "crack";
            case R.drawable.ic_fish_black_24dp:
                return "oop oop oop";
            case R.drawable.ic_poop_black_24dp:
                return "poop";
            default:
                return "null";
        }
    }
}
