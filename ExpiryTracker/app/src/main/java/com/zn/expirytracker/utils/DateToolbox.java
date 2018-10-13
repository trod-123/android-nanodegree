package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.res.Resources;

import com.zn.expirytracker.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class DateToolbox {


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
     * @param start
     * @param end
     * @return
     */
    public static String getFormattedExpiryDateString(Context context,
                                                      DateTime start, DateTime end) {

        int diffDays = getNumDaysBetweenDates(start, end);
        int diffMonths = getNumMonthsBetweenDates(start, end);
        int diffYears = getNumYearsBetweenDates(start, end);

        Resources res = context.getResources();

        if (diffDays == 0) {
            // Today (expect Expires today!)
            return res.getString(R.string.expiry_msg_today);
        }

        // expires in the future
        if (diffDays > 0) {
            if (diffDays == 1) {
                // Tomorrow (expect Expires tomorrow!)
                return res.getString(R.string.expiry_msg_tomorrow);
            } else if (diffDays < 7) {
                // 2-6 days after today
                // (Expect Expires soon on DOW)
                return res.getString(R.string.expiry_msg_soon, end.dayOfWeek().getAsText());
            } else if (diffDays < 14 && diffMonths == 0 && diffYears == 0) {
                // 7-13 days after today, and within the current month and year
                // (expect Expires next DOW)
                return res.getString(R.string.expiry_msg_next_dow, end.dayOfWeek().getAsText());
            } else if (diffMonths == 0 && diffYears == 0) {
                // At least 14 days after today, and within the current month and year
                // (expect Expires on the ORDINAL)
                return res.getString(R.string.expiry_msg_ordinal,
                        convertIntToOrdinal(end.dayOfMonth().get()));
            } else if (diffMonths > 0 && diffYears == 0) {
                // 14 days after today, but in the next month, but current year
                // (expect Expires on MONTH DAY)
                return res.getString(R.string.expiry_msg_month_day,
                        end.monthOfYear().getAsShortText(), end.dayOfMonth().get());
            } else {
                // 14 days after today, but in the next month and next year
                // (expect Expires on MONTH DAY YEAR)
                return res.getString(R.string.expiry_msg_month_day_year,
                        end.monthOfYear().getAsShortText(), end.dayOfMonth().get(),
                        end.year().get());
            }
        }

        // already expired
        else {
            if (diffDays == -1) {
                // Yesterday (expect Expired yesterday)
                return res.getString(R.string.expiry_msg_past_yesterday);
            } else if (diffDays > -7) {
                // 2-6 days before today (Expired on DOW)
                return res.getString(R.string.expiry_msg_past_this, end.dayOfWeek().getAsText());
            } else if (diffDays > -14 && diffMonths == 0 && diffYears == 0) {
                // 7-13 days before today, and within the current month and year
                // (expect Expired last DOW)
                return res.getString(R.string.expiry_msg_past_last, end.dayOfWeek().getAsText());
            } else if (diffMonths == 0 && diffYears == 0) {
                // At least 14 days before today, and within the current month and year
                // (expect Expired on the ORDINAL)
                return res.getString(R.string.expiry_msg_past_ordinal,
                        convertIntToOrdinal(end.dayOfMonth().get()));
            } else if (diffMonths < 0 && diffYears == 0) {
                // 14 days before today, but in the previous month, but current year
                // (expect Expired on MONTH DAY)
                return res.getString(R.string.expiry_msg_past_month_day,
                        end.monthOfYear().getAsShortText(), end.dayOfMonth().get());
            } else {
                // 14 days before today, but in the next month and next year
                // (expect Expired on MONTH DAY YEAR)
                return res.getString(R.string.expiry_msg_past_month_day_year,
                        end.monthOfYear().getAsShortText(), end.dayOfMonth().get(),
                        end.year().get());
            }
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
    static int[] getNumDaysBetweenDatesArray(long[] dates, long baseDateInMillis) {
        int[] numDaysFromCurrent = new int[dates.length];
        for (int i = 0; i < dates.length; i++) {
            numDaysFromCurrent[i] = getNumDaysBetweenDates(baseDateInMillis, dates[i]);
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
     * Helper for getting the number of days between two dates
     *
     * @param start
     * @param end
     * @return
     */
    private static int getNumDaysBetweenDates(DateTime start, DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }

    /**
     * Helper for getting the number of months between two dates
     *
     * @param start
     * @param end
     * @return
     */
    private static int getNumMonthsBetweenDates(DateTime start, DateTime end) {
        return end.getMonthOfYear() - start.getMonthOfYear();
    }

    /**
     * Helper for getting the number of years between two dates
     *
     * @param start
     * @param end
     * @return
     */
    private static int getNumYearsBetweenDates(DateTime start, DateTime end) {
        return end.getYear() - start.getYear();
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
     * <p>
     * TODO: Currently supports only English.
     *
     * @param baseDateStartOfDay
     * @param plusDays
     * @return
     */
    public static long getDateBounds(long baseDateStartOfDay, int plusDays) {
        return getDateBounds(new DateTime(baseDateStartOfDay), plusDays);
    }

    /**
     * Converts a date string into a {@link DateTime} object.
     *
     * @param dateString
     * @return
     * @throws IllegalArgumentException failed to parse {@code dateString}
     */
    public static DateTime parseDateFromString(String dateString) throws IllegalArgumentException {
        Timber.d("DateToolbox/Parsing date: %s", dateString);
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime fakeMutableDateTime = new DateTime(); // for just setting specific fields

        // (0) Clean up date string
        dateString = dateString.toLowerCase();
        String expires = "expires ";
        String on = "on ";
        String in = "in ";
        String the = "the ";
        if (dateString.startsWith(expires)) {
            dateString = dateString.substring(expires.length());
        }
        if (dateString.startsWith(on)) {
            dateString = dateString.substring(on.length());
        }
        if (dateString.startsWith(in)) {
            dateString = dateString.substring(in.length());
        }
        if (dateString.startsWith(the)) {
            dateString = dateString.substring(the.length());
        }

        // (1) Try parsing provided count (days or weeks)

        // days

        if (dateString.matches("\\d+ days?")) {
            // Arabic number
            String num = dateString.substring(0, dateString.indexOf("day"));
            return today.plusDays(Integer.parseInt(num.trim()));
        }
        if (dateString.matches("\\w+ days?")) {
            // Worded number
            String num = dateString.substring(0, dateString.indexOf("day"));
            return today.plusDays(Integer.parseInt(NumberUtils.replaceNumbers(num.trim())));
        }

        // weeks (with fractions)

        if (dateString.matches("\\d+ weeks?") ||
                dateString.matches("\\d+.\\d+ weeks?") ||
                dateString.matches("\\d+ and a half weeks?") ||
                dateString.matches("\\d+ and a 1/2 weeks?")) {
            String num = dateString.substring(0, dateString.indexOf("week"));
            if (num.contains("and a half") || num.contains("and a 1/2")) {
                num = num.substring(0, num.indexOf("and a"));
                int weeks = Integer.parseInt(num.trim()) * 7;
                return today.plusDays((int) Math.ceil(weeks + 3.5));
            } else {
                return today.plusDays((int) Math.ceil(Double.parseDouble(num.trim()) * 7));
            }
        }
        if (dateString.matches("\\w+ weeks?") ||
                dateString.matches("\\w+ point \\w+ weeks?") ||
                dateString.matches("\\w+ and a half weeks?") ||
                dateString.matches("\\w+ and a 1/2 weeks?")) {
            String num = dateString.substring(0, dateString.indexOf("week"));
            if (num.contains("and a half") || num.contains("and a 1/2")) {
                num = num.substring(0, num.indexOf("and a"));
                int weeks = Integer.parseInt(NumberUtils.replaceNumbers(num.trim())) * 7;
                return today.plusDays((int) Math.ceil(weeks + 3.5));
            }
            return today.plusDays((int) Math.ceil(
                    Double.parseDouble(NumberUtils.replaceNumbers(num.trim())) * 7));
        }
        if (dateString.equals("half a week") || dateString.equals("1/2 a week")) {
            return today.plusDays(4);
        }

        // (2) Try converting date from date format

        // https://stackoverflow.com/questions/9945072/convert-string-to-date-in-java
        SimpleDateFormat[] dateFormats = new SimpleDateFormat[4];
        dateFormats[0] = new SimpleDateFormat("MMMM dd yyyy");
        dateFormats[1] = new SimpleDateFormat("MMMM dd");
        dateFormats[2] = new SimpleDateFormat("MMM dd");
        dateFormats[3] = new SimpleDateFormat("dd");

        Date date;
        // https://stackoverflow.com/questions/13239972/how-do-you-implement-a-re-try-catch
        int count = 0;
        while (true) {
            try {
                // https://stackoverflow.com/questions/28514346/parsing-a-date-s-ordinal-indicator-st-nd-rd-th-in-a-date-time-string/28514476
                date = dateFormats[count].parse(dateString
                        .replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                if (count != 0)
                    date.setYear((new Date()).getYear()); // set current year if not provided
                if (count == 3)
                    date.setMonth(today.getMonthOfYear() - 1); // set current month if not provided
                return new DateTime(date.getTime());
            } catch (ParseException e) {
                if (++count == dateFormats.length) {
                    Timber.d(e, "DateToolbox/The date string did not fall in any of the date formats provided");
                    break;
                }
            }
        }

        // (3) Try converting from date nouns
        if (dateString.equals("today")) {
            return today;
        } else if (dateString.equals("tomorrow")) {
            return today.plusDays(1);
        } else if (dateString.equals("yesterday")) {
            return today.minusDays(1);
        }

        // (4) Try converting from day of week
        int[] dows = new int[]{
                DateTimeConstants.MONDAY, DateTimeConstants.TUESDAY, DateTimeConstants.WEDNESDAY,
                DateTimeConstants.THURSDAY, DateTimeConstants.FRIDAY, DateTimeConstants.SATURDAY,
                DateTimeConstants.SUNDAY};
        for (int dow : dows) {
            if (dateString.equals(fakeMutableDateTime.withDayOfWeek(dow).dayOfWeek().getAsText()
                    .toLowerCase())) {
                // Get the first DOW from the current day
                return getFirstDayOfWeekDateFromStartDate(dow, System.currentTimeMillis());
            }
        }

        // Failed to parse date after trying all cases, so just throw an exception
        throw new IllegalArgumentException("DateToolbox/The date string failed to be parsed");
    }

    /**
     * Gets the first occurrence of {@code dow} from {@code startDate}
     *
     * @param dow       Must be a value between 1 and 7, used by {@link DateTimeConstants}
     * @param startDate The base date, from which we start searching for the first instance of
     *                  the {@code dow}
     * @return
     */
    public static DateTime getFirstDayOfWeekDateFromStartDate(int dow, long startDate) {
        DateTime start = new DateTime(startDate).withTimeAtStartOfDay();
        int startDow = start.getDayOfWeek();
        int diffDow = dow - startDow;
        return start.plusDays(diffDow <= 0 ? diffDow + 7 : diffDow); // offset by week if negative
    }

    /**
     * Converts a date string into a {@link DateTime} object. If there is an error, then return
     * the fallback date provided and display a message to the user
     *
     * @param dateString
     * @param context
     * @param dateFallback
     * @return
     */
    public static DateTime parseDateFromString(String dateString, Context context, long dateFallback) {
        try {
            return parseDateFromString(dateString);
        } catch (IllegalArgumentException e) {
            // Return current date if error
            Timber.e(e);
            Toolbox.showToast(context, context.getString(R.string.message_error_parse_date));
            return new DateTime(dateFallback);
        }
    }
}
