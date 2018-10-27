package com.zn.expirytracker.utils;

import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

/**
 * Constants used throughout the app
 */
public class Constants {

    public static final String SHARED_PREFS_NAME = "com.zn.expirytracker.sp";
    public static final String FOOD_TIMESTAMP = "food_updated";
    public static final String PREFS_TIMESTAMP = "prefs_updated";

    /**
     * A uid label for unregistered users, used to store
     * {@link com.zn.expirytracker.data.firebase.UserMetrics} in Firebase RTD
     * <p>
     * This is also used in Shared Preferences to keep track of whether the user is logged in or not
     */
    public static final String AUTH_GUEST = "unregistered";
    public static final boolean ENABLE_GUEST_WIDGET = true;

    public static final float ALPHA_ACTIVATED = 1f;
    public static final float ALPHA_DEACTIVATED = 0.3f;

    public static final long DEFAULT_VIBRATION_BUTTON_PRESS_LENGTH = 50; // millis

    public static final long PAGE_INDICATOR_FADE_IN_DURATION = 250; // millis
    public static final long PAGE_INDICATOR_FADE_IN_DELAY = 0; // millis
    public static final long PAGE_INDICATOR_FADE_OUT_DURATION = 1000; // millis
    public static final long PAGE_INDICATOR_FADE_OUT_DELAY = 500; // millis

    public static final float GLIDE_THUMBNAIL_MULTIPLIER = 0.1f;

    public static final int DURATION_TRANSITION = 150; // millis
    public static final long DURATION_VISIBILITY_ANIMATION = 300; // millis

    public static final int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static final int DEFAULT_SNACKBAR_LENGTH = Snackbar.LENGTH_LONG;

    /**
     * To be used for barcodes that did not have a valid response from UpcItemDb
     */
    public static final String BARCODE_NO_DATA = "0000000";

    /**
     * Delay in milliseconds that allows consecutive speech requests without blocking the mic.
     * Needs to be at least 200-300 or higher depending on the device. We'll set to below just to be
     * safe
     */
    public static final int DELAY_CONSECUTIVE_SPEECH_REQEUSTS = 300;

    public static final int BITMAP_SAVING_QUALITY = 30; // out of 100
    public static final String DEFAULT_FILENAME = "food";

    public static final double GREETING_GENERIC_THRESHOLD = 0.7; // above = TOD greet, below = generic
    public static final int GREETING_EVENING_BOUNDS = 17; // 24 hours
    public static final int GREETING_MORNING_BOUNDS = 5;
    public static final int GREETING_AFTERNOON_BOUNDS = 12;

    /**
     * Max number of images saved from the image list provided by UpcItemDb
     */
    public static final int MAX_BARCODE_IMAGE_LIST_SIZE = 1;

    /**
     * Max number of images users can attach to a single item
     * <p>
     * Note: When setting, this must be greater than 2 (one for barcode, one for scanned image)
     */
    public static final int MAX_IMAGE_LIST_SIZE = 3;

    /**
     * Default hour that indicates "morning"
     */
    public static final int DEFAULT_MORNING_HOUR = 9;

    /**
     * Default hour that indicates "afternoon"
     */
    public static final int DEFAULT_AFTERNOON_HOUR = 15;

    /**
     * Default hour that indicates "evening"
     */
    public static final int DEFAULT_EVENING_HOUR = 21;

    /**
     * Default hour that indicates "overnight"
     */
    public static final int DEFAULT_OVERNIGHT_HOUR = 3;

    /**
     * Limit of foods to show per day in day by day line summaries
     */
    public static final int MAX_DAILY_LINE_SUMMARY_FOOD_COUNT = 5;
}
