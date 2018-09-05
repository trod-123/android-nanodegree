package com.zn.expirytracker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.zn.expirytracker.utils.StringsToolbox;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StringsToolboxTests {

    private Context mInstContext;

    @Before
    public void setup() {
        // .getContext() and .getTargetContext() provide different Contexts. Accessing resources
        // requires .getTargetContext(). Using the former will result in a Resource not found error
        mInstContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testGetFormattedExpiryDateString() {
        DateTime initial = new DateTime(2018, 1, 1, 0,
                0, 0, 0);
        DateTime tested = new DateTime(2018, 1, 1, 0,
                0, 0, 0);

        String[] expectedStrings = new String[]{
                "Expires tomorrow!",
                "Expires soon on Wednesday",
                "Expires soon on Thursday",
                "Expires soon on Friday",
                "Expires soon on Saturday",
                "Expires soon on Sunday",
                "Expires next Monday",
                "Expires next Tuesday",
                "Expires next Wednesday",
                "Expires next Thursday",
                "Expires next Friday",
                "Expires next Saturday",
                "Expires next Sunday",
                "Expires on the 15th",
                "Expires on the 16th",
                "Expires on the 17th",
                "Expires on the 18th",
                "Expires on the 19th",
                "Expires on the 20th",
                "Expires on the 21st",
                "Expires on the 22nd",
                "Expires on the 23rd",
                "Expires on the 24th",
                "Expires on the 25th",
                "Expires on the 26th",
                "Expires on the 27th",
                "Expires on the 28th",
                "Expires on the 29th",
                "Expires on the 30th",
                "Expires on the 31st",
                "Expires on Feb 1",
                "Expires on Feb 2",
                "Expires on Feb 3"
        };

        String today = StringsToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
        Assert.assertTrue(today.equals("Expires today!"));

        for (String expected : expectedStrings) {
            tested = tested.plusDays(1);
            String actual = StringsToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
            Assert.assertTrue(actual.equals(expected));
        }

        String[] expectedStringsYearJump = new String[]{
                "Expires on Dec 29",
                "Expires on Dec 30",
                "Expires on Dec 31",
                "Expires on Jan 1, 2019",
                "Expires on Jan 2, 2019",
                "Expires on Jan 3, 2019"
        };

        tested = new DateTime(2018, 12, 28, 0,
                0, 0, 0);
        for (String expected : expectedStringsYearJump) {
            tested = tested.plusDays(1);
            String actual = StringsToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
            Assert.assertTrue(actual.equals(expected));
        }
    }
}
