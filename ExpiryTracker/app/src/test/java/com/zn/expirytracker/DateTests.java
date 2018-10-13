package com.zn.expirytracker;

import com.zn.expirytracker.utils.DateToolbox;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTests {

    /**
     * Assumes {@link DateToolbox#getFirstDayOfWeekDateFromStartDate(int, long)} is correct!
     */
    @Test
    public void testParseDateStringIntoDateTime() {
        // region cases
        final DateTime DATE_TIME = new DateTime().withTimeAtStartOfDay();

        final String[] TEST_CASES = new String[]{
                "Today",
                "Tomorrow",
                "October 14th",
                "October 14th 2019",
                "October 14",
                "October 14 2019",
                "1st",
                "2nd",
                "3rd",
                "4th",
                "20th",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday",
                "1 day",
                "2 days",
                "10 days",
                "15 days",
                "One day",
                "Two days",
                "Ten days",
                "Fifteen days",
                "1 week",
                "2 weeks",
                "10 weeks",
                "15 weeks",
                "One week",
                "Two weeks",
                "Ten weeks",
                "Fifteen weeks",
                "1.5 weeks",
                "One point five weeks",
                "1 and a half weeks",
                "One and a half weeks",
                "Half a week",
                "1 and a 1/2 weeks",
                "One and a 1/2 weeks",
                "1/2 a week"
        };
        final String[] TEST_CASE_PREFIXES = new String[]{
                "",
                "Expires on ",
                "On ",
                "In ",
                "The ",
                "On the ",
                "In the "
        };
        final DateTime[] TEST_CASE_EXPECTED = new DateTime[]{
                DATE_TIME,
                DATE_TIME.plusDays(1),
                new DateTime(DATE_TIME.getYear(), 10, 14, 0, 0),
                new DateTime(2019, 10, 14, 0, 0),
                new DateTime(DATE_TIME.getYear(), 10, 14, 0, 0),
                new DateTime(2019, 10, 14, 0, 0),
                DATE_TIME.withDayOfMonth(1),
                DATE_TIME.withDayOfMonth(2),
                DATE_TIME.withDayOfMonth(3),
                DATE_TIME.withDayOfMonth(4),
                DATE_TIME.withDayOfMonth(20),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.MONDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.TUESDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.WEDNESDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.THURSDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.FRIDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.SATURDAY, DATE_TIME.getMillis()),
                DateToolbox.getFirstDayOfWeekDateFromStartDate(
                        DateTimeConstants.SUNDAY, DATE_TIME.getMillis()),
                DATE_TIME.plusDays(1),
                DATE_TIME.plusDays(2),
                DATE_TIME.plusDays(10),
                DATE_TIME.plusDays(15),
                DATE_TIME.plusDays(1),
                DATE_TIME.plusDays(2),
                DATE_TIME.plusDays(10),
                DATE_TIME.plusDays(15),
                DATE_TIME.plusDays(7),
                DATE_TIME.plusDays(7 * 2),
                DATE_TIME.plusDays(7 * 10),
                DATE_TIME.plusDays(7 * 15),
                DATE_TIME.plusDays(7),
                DATE_TIME.plusDays(7 * 2),
                DATE_TIME.plusDays(7 * 10),
                DATE_TIME.plusDays(7 * 15),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(4),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(11),
                DATE_TIME.plusDays(4)
        };
        // endregion

        Map<String, String> resultsMap = new HashMap<>();
        List<String> failedTestCases = new ArrayList<>();

        for (String prefix : TEST_CASE_PREFIXES) {
            for (int i = 0; i < TEST_CASES.length; i++) {
                String testCase = TEST_CASES[i];
                String fullCase = prefix + testCase;
                try {
                    DateTime result = DateToolbox.parseDateFromString(prefix + testCase);
                    if (result.isEqual(TEST_CASE_EXPECTED[i])) {
                        resultsMap.put(fullCase, DateToolbox.getFormattedFullDateString(result));
                    } else {
                        failedTestCases.add(fullCase);
                        resultsMap.put(fullCase, String.format("Failed: %s | Expected: %s",
                                DateToolbox.getFormattedFullDateString(result),
                                DateToolbox.getFormattedFullDateString(TEST_CASE_EXPECTED[i])));
                    }
                } catch (IllegalArgumentException e) {
                    failedTestCases.add(fullCase);
                    resultsMap.put(fullCase, "");
                }
            }
            for (String testCase : resultsMap.keySet()) {
                System.out.println(String.format("%s => %s", testCase, resultsMap.get(testCase)));
            }
            if (!failedTestCases.isEmpty()) {
                Assert.fail("Failed cases: " + Arrays.toString(failedTestCases.toArray()));
            }
        }
    }
}
