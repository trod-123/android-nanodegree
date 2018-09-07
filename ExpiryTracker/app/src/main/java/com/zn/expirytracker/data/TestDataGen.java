package com.zn.expirytracker.data;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.data.model.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random test data
 */
public class TestDataGen {

    private static TestDataGen INSTANCE;

    public static final int DEFAULT_NUM_CHART_ENTRIES = 7;
    public static final int DEFAULT_NUM_FOOD_DATA = 25;
    public static final int DEFAULT_DATE_BOUNDS = 1000 * 60 * 60 * 24 * 21; // 20 days
    public static final int DEFAULT_COUNT_BOUNDS = 10;

    private final Random randomizer = new Random();
    private final String[] foods = new String[]{
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

    private List<BarEntry> mBarEntries;
    private String[] mFoodNames;
    private long[] mExpiryDates;
    private int[] mCounts;
    private Storage[] mLocs;
    private int[] mColors;

    private TestDataGen(int numChartEntries, int numFoodData, int dateBounds, int countBounds) {
        mBarEntries = generateTestChartValues(numChartEntries, countBounds);
        mFoodNames = generateFoodNames(numFoodData);
        mExpiryDates = generateExpiryDates(numFoodData, dateBounds);
        mCounts = generateCounts(numFoodData, countBounds);
        mLocs = generateStorageLocs(numFoodData);
        mColors = generateColors(numFoodData);
    }

    /**
     * Generates an instance of this class with the passed attributes and returns the created
     * instance. If an instance already exists, then this method returns the original instance,
     * unmodified. If the instance needs to be recreated with new attributes, then call
     * {@link TestDataGen#recreateInstance(int, int, int, int)} to replace the current instance
     *
     * @param numChartEntries
     * @param numFoodData
     * @param dateBounds
     * @param countBounds
     */
    public static TestDataGen generateInstance(int numChartEntries, int numFoodData, int dateBounds,
                                               int countBounds) {
        if (INSTANCE == null) {
            INSTANCE = new TestDataGen(numChartEntries, numFoodData, dateBounds, countBounds);
        }
        return INSTANCE;
    }

    /**
     * Recreates the instance of this class with the passed attributes and returns it. Whether
     * or not the instance already exists, this will always return the created instance.
     * <p>
     * Warning, calling this method may set the data in your app out of sync across all places that
     * are using the instance
     *
     * @param numChartEntries
     * @param numFoodData
     * @param dateBounds
     * @param countBounds
     * @return
     */
    public static TestDataGen recreateInstance(int numChartEntries, int numFoodData, int dateBounds,
                                               int countBounds) {
        INSTANCE = new TestDataGen(numChartEntries, numFoodData, dateBounds, countBounds);
        return INSTANCE;
    }

    /**
     * Gets the instance of this class. If the instance does not already exist, throws an
     * {@link IllegalStateException} with a message that an instance needs to be generated first
     *
     * @return
     * @throws IllegalStateException
     */
    public static TestDataGen getInstance() throws IllegalStateException {
        if (INSTANCE == null) {
            // if the user forgot to generate instance, throw an error
            throw new IllegalStateException("Before you can get an instance, you need to generate " +
                    "an instance first by calling generateInstance() with the required arguments");
        } else {
            return INSTANCE;
        }
    }

    /**
     * Returns the list of generated {@link BarEntry} values
     *
     * @return
     */
    public List<BarEntry> getBarEntries() {
        return mBarEntries;
    }

    /**
     * Returns the array of randomly generated food names
     *
     * @return
     */
    public String[] getFoodNames() {
        return mFoodNames;
    }

    /**
     * Returns the array of randomly generated expiry dates
     *
     * @return
     */
    public long[] getExpiryDates() {
        return mExpiryDates;
    }

    /**
     * Returns the array of randomly generated item counts
     *
     * @return
     */
    public int[] getCounts() {
        return mCounts;
    }

    /**
     * Returns the array of randomly selected {@link Storage} locations
     *
     * @return
     */
    public Storage[] getLocs() {
        return mLocs;
    }

    /**
     * Returns the array of randomly generated color values. Note these are color values and can
     * be handled directly, without having to go through {@code Context.getResources().getColor()}
     * @return
     */
    public int[] getColors() { return mColors; }

    /**
     * Generates random values for a bar chart with {@code numChartEntries} data points and a
     * y-axis limit of {@code countBounds}
     *
     * @param numChartEntries
     * @param countBounds
     * @return
     */
    private List<BarEntry> generateTestChartValues(int numChartEntries, int countBounds) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < numChartEntries; i++) {
            entries.add(new BarEntry(i, randomizer.nextInt(countBounds)));
        }
        return entries;
    }

    /**
     * Generates a list of random food names of length {@code numFoodData}
     *
     * @param numFoodData
     * @return
     */
    private String[] generateFoodNames(int numFoodData) {
        String[] foodNames = new String[numFoodData];
        int numFoods = foods.length;
        for (int i = 0; i < numFoodData; i++) {
            foodNames[i] = foods[randomizer.nextInt(numFoods)];
        }
        return foodNames;
    }

    /**
     * Generates a list of random dates of length {@code numFoodData}, bounded by {@code dateBounds}
     *
     * @param numFoodData
     * @param dateBounds
     * @return
     */
    private long[] generateExpiryDates(int numFoodData, int dateBounds) {
        long[] dates = new long[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            dates[i] = System.currentTimeMillis() + randomizer.nextInt(dateBounds);
        }
        return dates;
    }

    /**
     * Generates a list of random quantities of length {@code numFoodData}, bounded by
     * {@code countBounds}
     *
     * @param numFoodData
     * @param countBounds
     * @return
     */
    private int[] generateCounts(int numFoodData, int countBounds) {
        int[] counts = new int[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            counts[i] = randomizer.nextInt(countBounds);
        }
        return counts;
    }

    /**
     * Generates a list of random {@link Storage} locations of length {@code numFoodData}
     *
     * @param numFoodData
     * @return
     */
    private Storage[] generateStorageLocs(int numFoodData) {
        Storage[] locs = new Storage[numFoodData];
        Storage[] storageValues = Storage.values();
        int numStorageValues = storageValues.length;
        for (int i = 0; i < numFoodData; i++) {
            locs[i] = storageValues[randomizer.nextInt(numStorageValues)];
        }
        return locs;
    }

    /**
     * Generates a list of random color values. Note these are actual colors, not resources
     * @param numFoodData
     * @return
     */
    private int[] generateColors(int numFoodData) {
        int[] colors = new int[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            colors[i] = Color.argb(255, randomizer.nextInt(256),
                    randomizer.nextInt(256), randomizer.nextInt(256));
        }
        return colors;
    }
}
