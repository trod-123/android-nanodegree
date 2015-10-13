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

package com.thirdarm.popularmovies.utilities;

import android.test.AndroidTestCase;

import java.util.Arrays;

/**
 * Created by TROD on 20151008.
 *
 * JUNIT test assessing correctness and stability of the utilities package
 */
public class TestUtilities extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetDateRangeFromToday() throws Throwable {
        int a = -35;
        int b = 7;

        String[] answer = ReleaseDates.getDateRangeFromToday(a, b);
        // Should be in the form of (expected, actual)
        assertEquals(Arrays.toString(answer), Arrays.toString(new String[]{"2015-9-2", "2015-10-14"}));
    }

    @Override protected void tearDown() throws Exception {
        super.tearDown();
    }
}