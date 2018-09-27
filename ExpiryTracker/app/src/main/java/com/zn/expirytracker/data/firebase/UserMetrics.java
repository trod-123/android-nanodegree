package com.zn.expirytracker.data.firebase;

import com.zn.expirytracker.data.contracts.UserDatabaseContract;

/**
 * For tracking different user metrics through Firebase RTD
 */
public class UserMetrics {

    /**
     * Track the number of UpcItemDb Api calls users make
     */
    public static void incrementApiCallCount() {
        FirebaseDatabaseHelper.incrementUserMetricCount(UserDatabaseContract.COLUMN_NUM_API_CALLS);
    }

    /**
     * Track the number of times users add items through scanning barcodes
     */
    public static void incrementBarcodeScansCount() {
        FirebaseDatabaseHelper.incrementUserMetricCount(UserDatabaseContract.COLUMN_NUM_BARCODE_SCANS);
    }

    /**
     * Track the number of times users add items through taking pictures
     */
    public static void incrementImgRecInputCount() {
        FirebaseDatabaseHelper.incrementUserMetricCount(UserDatabaseContract.COLUMN_NUM_IMG_REC);
    }

    /**
     * Track the number of times users add items through taking pictures
     */
    public static void incrementImgOnlyInputCount() {
        FirebaseDatabaseHelper.incrementUserMetricCount(UserDatabaseContract.COLUMN_NUM_IMAGE_ONLY);
    }

    /**
     * Track the number of times users add items through text input
     */
    public static void incrementUserTextOnlyInputCount() {
        FirebaseDatabaseHelper.incrementUserMetricCount(UserDatabaseContract.COLUMN_NUM_TEXT_ONLY);
    }
}
