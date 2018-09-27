package com.zn.expirytracker.data.contracts;

public class DatabaseContract {

    public static final String DATABASE_NAME = "food_database";
    public static final int CURRENT_VERSION = 1;

    // region table names

    // Makes up the “real” database, which is synced with Firebase
    public static final String FOOD_TABLE_NAME = "food_table";
    // A hold for items captured, but not yet saved
    public static final String TEMP_TABLE_NAME = "temp_table";
    // A hold to keep track of database transactions that were done offline or when connections
    // could not be made with Firebase
    public static final String CACHE_TABLE_NAME = "cache_table";

    // endregion

    // region column names

    public static final String COLUMN_FOOD_NAME = "food_name"; // string
    public static final String COLUMN_DATE_EXPIRY = "date_expiry"; // long
    public static final String COLUMN_DATE_GOOD_THRU = "date_good_thru"; // long
    public static final String COLUMN_COUNT = "count"; // int
    public static final String COLUMN_STORAGE_LOCATION = "storage_location"; // enum
    public static final String COLUMN_DESCRIPTION = "description"; // string
    public static final String COLUMN_BRAND_NAME = "brand_name"; // string
    public static final String COLUMN_SIZE = "size"; // string
    public static final String COLUMN_WEIGHT = "weight"; // string
    public static final String COLUMN_NOTES = "notes"; // string
    public static final String COLUMN_BARCODE = "barcode"; // long
    public static final String COLUMN_INPUT_TYPE = "input_type"; // enum
    public static final String COLUMN_IMAGES = "images"; // string array

    // endregion

    // region pre-select dao columns

    public static final String SUMMARY_COLUMNS =
            "_id, food_name, date_expiry, date_good_thru, count, storage_location, images";

    // endregion
}
