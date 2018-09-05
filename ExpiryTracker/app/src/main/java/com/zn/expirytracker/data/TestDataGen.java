package com.zn.expirytracker.data;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TestDataGen {

    private static final int NUM_CHART_ENTRIES = 7;
    private static final int CHART_Y_BOUNDS = 10;

    private static final int NUM_FOOD_DATA = 10;
    private static final int DATE_LONG_BOUNDS = 1000000;

    public static List<BarEntry> getTestChartValues() {
        List<BarEntry> entries = new ArrayList<>();
        Random randomizer = new Random();
        for (int i = 0; i < NUM_CHART_ENTRIES; i++) {
            entries.add(new BarEntry(i, randomizer.nextInt(CHART_Y_BOUNDS)));
        }
        return entries;
    }

    public static String[] getFoodNames() {
        return new String[]{"Lettuce", "Tomatoes", "Tuna", "Cheese", "Spinach",
                "Sausage", "Beef", "Chicken", "Spaghetti", "Cake"};
    }

    public static Date[] getExpiryDates() {
        Date[] dates = new Date[NUM_FOOD_DATA];
        Random randomizer = new Random();
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            dates[i] = new Date(System.currentTimeMillis() - randomizer.nextInt(DATE_LONG_BOUNDS));
        }
        return dates;
    }

    public static int[] getCounts() {
        int[] counts = new int[NUM_FOOD_DATA];
        Random randomizer = new Random();
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            counts[i] = randomizer.nextInt(CHART_Y_BOUNDS);
        }
        return counts;
    }
}
