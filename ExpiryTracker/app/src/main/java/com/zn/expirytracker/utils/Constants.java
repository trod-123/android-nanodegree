package com.zn.expirytracker.utils;

import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Constants used throughout the app
 */
public class Constants {

    public static final String SHARED_PREFS_NAME = "com.zn.expirytracker.sp";
    public static final String FOOD_TIMESTAMP = "food_updated";
    public static final String PREFS_TIMESTAMP = "prefs_updated";

    public static final float ALPHA_ACTIVATED = 1f;
    public static final float ALPHA_DEACTIVATED = 0.3f;

    public static final long DEFAULT_VIBRATION_BUTTON_PRESS_LENGTH = 50; // millis

    public static final long PAGE_INDICATOR_FADE_IN_DURATION = 250; // millis
    public static final long PAGE_INDICATOR_FADE_IN_DELAY = 0; // millis
    public static final long PAGE_INDICATOR_FADE_OUT_DURATION = 1000; // millis
    public static final long PAGE_INDICATOR_FADE_OUT_DELAY = 500; // millis

    public static final float GLIDE_THUMBNAIL_MULTIPLIER = 0.1f;

    public static final int DURATION_TRANSITION = 150; // millis

    public static final int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static final int DEFAULT_SNACKBAR_LENGTH = Snackbar.LENGTH_LONG;

    /**
     * Delay in milliseconds that allows consecutive speech requests without blocking the mic.
     * Needs to be at least 200-300 or higher depending on the device. We'll set to below just to be
     * safe
     */
    public static final int DELAY_CONSECUTIVE_SPEECH_REQEUSTS = 300;

    public static final String BITMAP_SAVING_DIRECTORY = "scannedImagesDir";
    public static final int BITMAP_SAVING_QUALITY = 30; // out of 100
    public static final String DEFAULT_FILENAME = "food";

    public static final double GREETING_GENERIC_THRESHOLD = 0.7; // above = TOD greet, below = generic
    public static final int GREETING_EVENING_BOUNDS = 17; // 24 hours
    public static final int GREETING_MORNING_BOUNDS = 5;
    public static final int GREETING_AFTERNOON_BOUNDS = 12;
}
