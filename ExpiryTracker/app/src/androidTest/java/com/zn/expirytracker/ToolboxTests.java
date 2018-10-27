package com.zn.expirytracker;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.DateToolbox;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ToolboxTests {

    private static final String TAG = ToolboxTests.class.getSimpleName();

    private Context mInstContext;
    private List<Food> mTestFoods;
    private long mCurrentDateTimeStartOfDay;

    @Before
    public void setup() {
        // .getContext() and .getTargetContext() provide different Contexts. Accessing resources
        // requires .getTargetContext(). Using the former will result in a Resource not found error
        mInstContext = InstrumentationRegistry.getTargetContext();

        TestDataGen foodDataGen = TestDataGen.generateInstance(TestDataGen.DEFAULT_NUM_CHART_ENTRIES,
                TestDataGen.DEFAULT_NUM_FOOD_DATA, TestDataGen.DEFAULT_DATE_BOUNDS,
                TestDataGen.DEFAULT_GOOD_THRU_DATE_BOUNDS, TestDataGen.DEFAULT_COUNT_BOUNDS,
                TestDataGen.DEFAULT_SIZE_FORMAT, TestDataGen.DEFAULT_SIZE_BOUNDS,
                TestDataGen.DEFAULT_WEIGHT_FORMAT, TestDataGen.DEFAULT_IMAGE_COUNT_BOUNDS, true);
        mTestFoods = foodDataGen.getAllFoods();
        mCurrentDateTimeStartOfDay = DateToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
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

        String today = DateToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
        Assert.assertEquals(today, "Expires today!");

        for (String expected : expectedStrings) {
            tested = tested.plusDays(1);
            String actual = DateToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
            Assert.assertEquals(actual, expected);
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
            String actual = DateToolbox.getFormattedExpiryDateString(mInstContext, initial, tested);
            Assert.assertEquals(actual, expected);
        }
    }

    @Test
    public void testGetFoodDateMap_NoFillGaps() {
        testGetFoodDateMap(false);
    }

    @Test
    public void testGetFoodDateMap_FillGaps() {
        testGetFoodDateMap(true);
    }

    private void testGetFoodDateMap(boolean fillGaps) {
        int MAX_SIZE = 7;
        SparseArray<List<Food>> map = DataToolbox.getFoodDateMap(mTestFoods,
                mCurrentDateTimeStartOfDay, fillGaps, MAX_SIZE);
        SparseIntArray frequencies = DataToolbox.getIntFrequencies(mTestFoods,
                mCurrentDateTimeStartOfDay, fillGaps, MAX_SIZE);

        // STEP 1: Verify size
        Assert.assertEquals(map.size(), frequencies.size());

        List<Integer> countDays = new ArrayList<>();
        for (int i = 0; i < mTestFoods.size(); i++) {
            countDays.add(DateToolbox.getNumDaysBetweenDates(mCurrentDateTimeStartOfDay, mTestFoods.get(i).getDateExpiry()));
        }

        for (int i = 0; i < frequencies.size(); i++) {
            // STEP 2: Verify count
            List<Food> foodsAtKey = map.valueAt(i);
            int keyValueSize = frequencies.valueAt(i);
            Assert.assertEquals(foodsAtKey.size(), keyValueSize);
            for (int j = 0; j < keyValueSize; j++) {
                // STEP 3: Verify dates
                int numDaysUntilExpiryDate = DateToolbox.getNumDaysBetweenDates(
                        mCurrentDateTimeStartOfDay, foodsAtKey.get(j).getDateExpiry());
                Assert.assertEquals(numDaysUntilExpiryDate, frequencies.keyAt(i));
            }
        }
    }
}
