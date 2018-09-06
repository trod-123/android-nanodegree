package com.zn.expirytracker.data;

import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.data.model.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGen {

    private static final int NUM_CHART_ENTRIES = 7;
    private static final int COUNT_BOUNDS = 10;

    private static final int NUM_FOOD_DATA = 25;
    private static final int DATE_BOUNDS = 1000 * 60 * 60 * 24 * 21; // 20 days

    private static final Random randomizer = new Random();

    private static final String[] foods = new String[]{
            "dates",
            "celery seeds",
            "ginger ale",
            "won ton skins",
            "asiago cheese",
            "bruschetta",
            "sushi",
            "Irish cream liqueur",
            "acorn squash",
            "jelly beans",
            "provolone",
            "scallops",
            "snapper",
            "vanilla",
            "sour cream",
            "marmalade",
            "buttermilk",
            "lemon juice",
            "tomato paste",
            "melons",
            "bourbon",
            "baking soda",
            "bok choy",
            "potatoes",
            "curry leaves",
            "sausages",
            "graham crackers",
            "huckleberries",
            "asparagus",
            "croutons",
            "green beans",
            "kiwi",
            "maraschino cherries",
            "geese",
            "baking powder",
            "octopus",
            "aioli",
            "red snapper",
            "strawberries",
            "coffee",
            "sesame seeds",
            "colby cheese",
            "chives",
            "catfish",
            "pico de gallo",
            "sea cucumbers",
            "ginger",
            "lemons",
            "haddock",
            "coconut oil",
            "garlic",
            "squid",
            "cactus",
            "limes",
            "oatmeal",
            "heavy cream",
            "grapefruits",
            "flax seed",
            "berries",
            "flounder",
            "kale",
            "soymilk",
            "chocolate",
            "macaroni",
            "raisins",
            "tomatoes",
            "veal",
            "quail",
            "pistachios",
            "parsnips",
            "portabella mushrooms",
            "bean sprouts",
            "hoisin sauce",
            "aquavit",
            "cherries",
            "cookies",
            "sunflower seeds",
            "vegemite",
            "ice cream",
            "cremini mushrooms",
            "marshmallows",
            "dill",
            "red chile powder",
            "borscht",
            "peanut butter",
            "water",
            "peas",
            "jack cheese",
            "okra",
            "beef",
            "chicken",
            "pancetta",
            "chili sauce",
            "tomato juice",
            "lamb",
            "brandy",
            "pumpkins",
            "cumin",
            "water chestnuts",
            "zest"
    };

    public static List<BarEntry> getTestChartValues() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < NUM_CHART_ENTRIES; i++) {
            entries.add(new BarEntry(i, randomizer.nextInt(COUNT_BOUNDS)));
        }
        return entries;
    }

    public static String[] getFoodNames() {
        String[] foodNames = new String[NUM_FOOD_DATA];
        int numFoods = foods.length;
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            foodNames[i] = foods[randomizer.nextInt(numFoods)];
        }
        return foodNames;
    }

    public static long[] getExpiryDates() {
        long[] dates = new long[NUM_FOOD_DATA];
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            dates[i] = System.currentTimeMillis() + randomizer.nextInt(DATE_BOUNDS);
        }
        return dates;
    }

    public static int[] getCounts() {
        int[] counts = new int[NUM_FOOD_DATA];
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            counts[i] = randomizer.nextInt(COUNT_BOUNDS);
        }
        return counts;
    }

    public static Storage[] getStorageLocs() {
        Storage[] locs = new Storage[NUM_FOOD_DATA];
        Storage[] storageValues = Storage.values();
        int numStorageValues = storageValues.length;
        for (int i = 0; i < NUM_FOOD_DATA; i++) {
            locs[i] = storageValues[randomizer.nextInt(numStorageValues)];
        }
        return locs;
    }
}
