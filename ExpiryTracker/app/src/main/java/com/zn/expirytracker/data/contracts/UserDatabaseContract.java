package com.zn.expirytracker.data.contracts;

public class UserDatabaseContract {

    public static final String DATABASE_NAME = DatabaseContract.DATABASE_NAME;

    public static final String USER_DATA_TABLE_NAME = "user_data_table";

    // region column names

    public static final String COLUMN_NUM_API_CALLS = "num_api_calls"; // long
    public static final String COLUMN_NUM_BARCODE_SCANS = "num_barcode_scans"; // long
    public static final String COLUMN_NUM_IMG_REC = "num_img_rec"; // long
    public static final String COLUMN_NUM_IMAGE_ONLY = "num_img_only"; // long
    public static final String COLUMN_NUM_TEXT_ONLY = "num_text_only"; // long

    // endregion
}
