package com.zn.expirytracker.data;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.model.Storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates random test data.
 * <p>
 * Lists of foods, baby names, corporate cliches, food destinations, and fake credit card numbers
 * gathered from https://www.randomlists.com
 */
public class TestDataGen {

    private static TestDataGen INSTANCE;

    public static final int DEFAULT_NUM_CHART_ENTRIES = 7;
    public static final int DEFAULT_NUM_FOOD_DATA = 25;
    public static final int DEFAULT_DATE_BOUNDS = 1000 * 60 * 60 * 24 * 21; // 21 days
    public static final int DEFAULT_GOOD_THRU_DATE_BOUNDS = 1000 * 60 * 60 * 24 * 10; // 10 days
    public static final int DEFAULT_COUNT_BOUNDS = 3;
    public static final String DEFAULT_SIZE_FORMAT = "%s\" x %s\"";
    public static final String DEFAULT_WEIGHT_FORMAT = "%s kg";
    public static final int DEFAULT_SIZE_BOUNDS = 30;
    public static final int DEFAULT_IMAGE_COUNT_BOUNDS = 5;

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

    // From https://loremflickr.com/800/600
    private final String[] catImageUrls = new String[]{
            "https://loremflickr.com/320/240/dog",
            "https://loremflickr.com/320/240/paris",
            "https://loremflickr.com/320/240/cat",
            "https://loremflickr.com/320/240/computer",
            "https://loremflickr.com/320/240/girl",
            "https://loremflickr.com/320/240/woman",
            "https://loremflickr.com/320/240/beach",
            "https://loremflickr.com/320/240/pokemon",
            "https://loremflickr.com/320/240/america",
            "https://loremflickr.com/320/240/mountain",
            "https://loremflickr.com/320/240/hat",
            "https://loremflickr.com/320/240/keyboard",
            "https://loremflickr.com/320/240/phone",
            "https://loremflickr.com/320/240/speaker",
            "https://loremflickr.com/320/240/table",
    };

    private String[] babyNames = new String[]{
            "Justin",
            "Keyla",
            "Freddy",
            "Cameron",
            "Annika",
            "Marshall",
            "Hallie",
            "Josiah",
            "Taryn",
            "Delaney",
            "Maximus",
            "Lilah",
            "Harold",
            "Brisa",
            "Phoebe",
            "Finn",
            "Micah",
            "Wilson",
            "Elisa",
            "Jamir",
            "",
            "",
            "",
            "",
            ""
    };

    private String[] corporateCliches = new String[]{
            "industry Leader",
            "bird's eye view",
            "transparency",
            "Impactful",
            "hit the ground running",
            "Fail to plan and plan to fail",
            "Web 2.0",
            "Eco-anything",
            "Take the ball and run with it",
            "the 80/20 rule",
            "the scenery only changes for the lead dog",
            "Now More Than Ever",
            "the bottom line",
            "It is what it is",
            "team player",
            "the lion's share",
            "the 800 pound elephant/gorilla",
            "The reward for good work is more work.",
            "Best of Breed",
            "drop in the bucket",
            "",
            "",
            "",
            "",
            ""
    };

    private String[] foodDestinations = new String[]{
            "Italian",
            "Southern/Soul",
            "Cuban",
            "Asian",
            "Donuts",
            "Tex-Mex",
            "Steakhouse",
            "European",
            "Mexican",
            "Tea",
            "Indian",
            "Korean",
            "Modern American",
            "Coffee",
            "Smoothies",
            "Barbecue",
            "Delicatessen",
            "French",
            "Peruvian",
            "Sandwiches/Subs",
            "",
            "",
            "",
            "",
            ""
    };

    private String[] cardNums = new String[]{
            "6011524263509484",
            "6011666781482411",
            "5355496567989405",
            "6011332586438666",
            "6011292412059906",
            "4539538830067410",
            "6011231695646849",
            "374023077053064",
            "348864625649405",
            "378443148681659",
            "5230996801261181",
            "5440317382070451",
            "6011844112404772",
            "376983679503196",
            "6011806845426646",
            "378026702551176",
            "6011833067103059",
            "6011819102506091",
            "5415436791492436",
            "5423692538557093"
    };

    private List<BarEntry> mBarEntries;
    private int[] mColors;

    private String[] mFoodNames;
    private long[] mExpiryDates;
    private long[] mGoodThruDates;
    private int[] mCounts;
    private Storage[] mLocs;
    private String[] mDescriptions;
    private String[] mBrandNames;
    private String[] mSizes;
    private String[] mWeights;
    private String[] mNotes;
    private InputType[] mInputTypes;
    private String[] mBarcodes;
    private List<List<String>> mImages;

    private int mDatabaseSize;
    private List<Food> mFoods;

    private TestDataGen(int numChartEntries, int numFoodData, int dateBounds,
                        int goodThruDateBounds, int countBounds, String sizesFormat, int sizeBounds,
                        String weightsFormat, int imageCountBounds, boolean sorted) {
        mBarEntries = generateTestChartValues(numChartEntries, countBounds);
        mFoodNames = generateFoodNames(numFoodData);
        mExpiryDates = generateExpiryDates(numFoodData, dateBounds, sorted);
        mGoodThruDates = generateGoodThruDates(mExpiryDates, goodThruDateBounds);
        mCounts = generateCounts(numFoodData, countBounds);
        mLocs = generateStorageLocs(numFoodData);
        mDescriptions = generateDescriptions(numFoodData);
        mBrandNames = generateBrandNames(numFoodData);
        mSizes = generateSizes(numFoodData, sizesFormat, sizeBounds);
        mWeights = generateWeights(numFoodData, weightsFormat, sizeBounds);
        mNotes = generateNotes(numFoodData);
        mInputTypes = generateInputTypes(numFoodData);
        mBarcodes = generateBarcodes(mInputTypes);
        mImages = generateImages(numFoodData, imageCountBounds);

        mFoods = generateFoodItems(mFoodNames, mExpiryDates, mGoodThruDates, mCounts, mLocs,
                mDescriptions, mBrandNames, mSizes, mWeights, mNotes, mInputTypes, mBarcodes,
                mImages);

        mDatabaseSize = numFoodData;
    }

    /**
     * Generates an instance of this class with the passed attributes and returns the created
     * instance. If an instance already exists, then this method returns the original instance,
     * unmodified. If the instance needs to be recreated with new attributes, then call
     * {@link TestDataGen#recreateInstance(int, int, int, int, int, String, int, String, int, boolean)}
     * to replace the current instance
     * <p>
     * Pre-set constants are available, e.g. {@link TestDataGen#DEFAULT_COUNT_BOUNDS}, etc
     *
     * @param numChartEntries    Number of entries to include in the At a Glance bar chart
     * @param numFoodData        Number of foods
     * @param dateBounds         Max number of days from the current date for foods to expire
     * @param goodThruDateBounds Max number of days from the current date for foods to go bad
     * @param countBounds        Max number of quantities each food can set
     * @param sizesFormat        String format for sizes
     * @param sizeBounds         Max size each food can set
     * @param weightsFormat      String format for weights
     * @param imageCountBounds   Max number of images each food can set
     * @param sorted             {@code true} if the food should be sorted in ascending date order
     * @return
     */
    public static TestDataGen generateInstance(int numChartEntries, int numFoodData, int dateBounds,
                                               int goodThruDateBounds, int countBounds,
                                               String sizesFormat, int sizeBounds,
                                               String weightsFormat, int imageCountBounds,
                                               boolean sorted) {
        if (INSTANCE == null) {
            INSTANCE = new TestDataGen(numChartEntries, numFoodData, dateBounds, goodThruDateBounds,
                    countBounds, sizesFormat, sizeBounds, weightsFormat, imageCountBounds, sorted);
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
                                               int goodThruDateBounds, int countBounds,
                                               String sizesFormat, int sizeBounds,
                                               String weightsFormat, int imageCountBounds,
                                               boolean sorted) {
        INSTANCE = new TestDataGen(numChartEntries, numFoodData, dateBounds, goodThruDateBounds,
                countBounds, sizesFormat, sizeBounds, weightsFormat, imageCountBounds, sorted);
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
     * Returns the size of the generated database
     *
     * @return
     */
    public int getDatabaseSize() {
        return mDatabaseSize;
    }

    /**
     * Returns all the {@link Food} objects
     *
     * @return
     */
    public List<Food> getAllFoods() {
        return mFoods;
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
     *
     * @return
     */
    public int[] getColors() {
        return mColors;
    }

    /**
     * Gets the food name at the specified position
     *
     * @param position
     * @return
     */
    public String getFoodNameAt(int position) {
        return mFoodNames[position];
    }

    /**
     * Gets the expiry date at the specified position
     *
     * @param position
     * @return
     */
    public long getExpiryDateAt(int position) {
        return mExpiryDates[position];
    }

    /**
     * Gets the count at the specified position
     *
     * @param position
     * @return
     */
    public int getCountAt(int position) {
        return mCounts[position];
    }

    /**
     * Gets the storage location at the specified position
     *
     * @param position
     * @return
     */
    public Storage getStorageLocAt(int position) {
        return mLocs[position];
    }

    /**
     * Gets the color at the specified position
     *
     * @param position
     * @return
     */
    public int getColorAt(int position) {
        return mColors[position];
    }

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
     * Generates a list of random color values. Note these are actual colors, not resources
     * <p>
     * Source: https://stackoverflow.com/questions/5280367/android-generate-random-color-on-click
     *
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
    private long[] generateExpiryDates(int numFoodData, int dateBounds, boolean sorted) {
        long[] dates = new long[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            dates[i] = System.currentTimeMillis() + randomizer.nextInt(dateBounds);
        }
        if (sorted) {
            Arrays.sort(dates);
        }
        return dates;
    }

    /**
     * Generates a list of good thru dates based on the provided list of {@code expiryDates},
     * bounded by {@code dateBounds}
     *
     * @param expiryDates
     * @param dateBounds
     * @return
     */
    private long[] generateGoodThruDates(long[] expiryDates, int dateBounds) {
        long[] dates = new long[expiryDates.length];
        for (int i = 0; i < expiryDates.length; i++) {
            dates[i] = expiryDates[i] + randomizer.nextInt(dateBounds);
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
            counts[i] = randomizer.nextInt(countBounds) + 1;
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
     * Generates a list of random descriptions of length {@code numFoodData}. There is a small
     * chance empty strings are provided per index
     *
     * @param numFoodData
     * @return
     */
    private String[] generateDescriptions(int numFoodData) {
        String[] descriptions = new String[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            descriptions[i] = corporateCliches[randomizer.nextInt(corporateCliches.length)];
        }
        return descriptions;
    }

    /**
     * Generates a list of random brand names of length {@code numFoodData}. There is a small chance
     * empty strings are provided per index
     *
     * @param numFoodData
     * @return
     */
    private String[] generateBrandNames(int numFoodData) {
        String[] names = new String[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            names[i] = babyNames[randomizer.nextInt(babyNames.length)];
        }
        return names;
    }

    /**
     * Generates a list of random size strings of length {@code numFoodData}, in the format
     * {@code format} and bounded by {@code sizeBounds}. There is a 50/50 chance empty strings are
     * provided per index
     * <p>
     * Assumes {@code format} accepts 2 placeholders, each representing x and y
     *
     * @param numFoodData
     * @param sizeBounds
     * @return
     */
    private String[] generateSizes(int numFoodData, String format, int sizeBounds) {
        String[] sizes = new String[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            int x = randomizer.nextInt(sizeBounds);
            int y = randomizer.nextInt(sizeBounds);
            sizes[i] = randomizer.nextBoolean() ? String.format(format, x, y) : "";
        }
        return sizes;
    }

    /**
     * Generates a list of random weight strings of length {@code numFoodData}, in the format
     * {@code format} and bounded by {@code sizeBounds}. There is a 50/50 chance empty strings are
     * provided per index
     * <p>
     * Assumes {@code format} accepts only 1 placeholder, representing the quantity
     *
     * @param numFoodData
     * @param sizeBounds
     * @return
     */
    private String[] generateWeights(int numFoodData, String format, int sizeBounds) {
        String[] weights = new String[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            int x = randomizer.nextInt(sizeBounds);
            weights[i] = randomizer.nextBoolean() ? String.format(format, x) : "";
        }
        return weights;
    }

    /**
     * Generates a list of random notes of length {@code numFoodData}. There is a small chance empty
     * strings are provided per index
     *
     * @param numFoodData
     * @return
     */
    private String[] generateNotes(int numFoodData) {
        String[] notes = new String[numFoodData];
        for (int i = 0; i < numFoodData; i++) {
            notes[i] = foodDestinations[randomizer.nextInt(foodDestinations.length)];
        }
        return notes;
    }

    /**
     * Generates a list of random {@link InputType} locations of length {@code numFoodData}
     *
     * @param numFoodData
     * @return
     */
    private InputType[] generateInputTypes(int numFoodData) {
        InputType[] inputTypes = new InputType[numFoodData];
        InputType[] inputValues = InputType.values();
        int numInputValues = inputValues.length;
        for (int i = 0; i < numFoodData; i++) {
            inputTypes[i] = inputValues[randomizer.nextInt(numInputValues)];
        }
        return inputTypes;
    }

    /**
     * Generates a list of barcodes based on the provided list of {@code inputTypes}. Barcodes are
     * provided for the indices where {@code inputType == InputType.BARCODE}. Empty strings are
     * provided for all other indices
     *
     * @param inputTypes
     * @return
     */
    private String[] generateBarcodes(InputType[] inputTypes) {
        String[] barcodes = new String[inputTypes.length];
        for (int i = 0; i < inputTypes.length; i++) {
            if (inputTypes[i] == InputType.BARCODE) {
                barcodes[i] = cardNums[randomizer.nextInt(cardNums.length)];
            } else {
                barcodes[i] = "";
            }
        }
        return barcodes;
    }

    /**
     * Generates a list of length {@code numFoodData}, that contains lists of image urls of length
     * {@code imageCountBounds}
     *
     * @param numFoodData
     * @param imageCountBounds
     * @return
     */
    private List<List<String>> generateImages(int numFoodData, int imageCountBounds) {
        List<List<String>> imagesLists = new ArrayList<>();
        for (int i = 0; i < numFoodData; i++) {
            List<String> images = new ArrayList<>();
            int limit = randomizer.nextInt(imageCountBounds);
            for (int j = 0; j < limit; j++) {
                images.add(catImageUrls[randomizer.nextInt(catImageUrls.length)]);
            }
            imagesLists.add(images);
        }
        return imagesLists;
    }

    /**
     * Generates a single food item
     *
     * @param foodName
     * @param expiryDate
     * @param goodThruDate
     * @param count
     * @param location
     * @param description
     * @param brandName
     * @param size
     * @param weight
     * @param notes
     * @param inputType
     * @param barcode
     * @param images
     * @return
     */
    private Food generateFoodItem(String foodName, long expiryDate, long goodThruDate, int count,
                                  Storage location, String description, String brandName,
                                  String size, String weight, String notes, InputType inputType,
                                  String barcode, List<String> images) {
        return new Food(foodName, expiryDate, goodThruDate, count, location, description, brandName,
                size, weight, notes, barcode, inputType, images);
    }

    /**
     * Generates a list of food items. Assumes that all arguments passed have the same length and
     * whose indices correspond with each other to form a single food object
     *
     * @param foodNames
     * @param expiryDates
     * @param goodThruDates
     * @param counts
     * @param locations
     * @param descriptions
     * @param brandNames
     * @param sizes
     * @param weights
     * @param notes
     * @param inputTypes
     * @param barcodes
     * @param imagesLists
     * @return
     */
    private List<Food> generateFoodItems(String[] foodNames, long[] expiryDates,
                                         long[] goodThruDates, int[] counts, Storage[] locations,
                                         String[] descriptions, String[] brandNames, String[] sizes,
                                         String[] weights, String[] notes, InputType[] inputTypes,
                                         String[] barcodes, List<List<String>> imagesLists) {
        List<Food> foods = new ArrayList<>();
        for (int i = 0; i < foodNames.length; i++) {
            foods.add(new Food(foodNames[i], expiryDates[i], goodThruDates[i], counts[i],
                    locations[i], descriptions[i], brandNames[i], sizes[i], weights[i], notes[i],
                    barcodes[i], inputTypes[i], imagesLists.get(i)));
        }
        return foods;
    }
}
