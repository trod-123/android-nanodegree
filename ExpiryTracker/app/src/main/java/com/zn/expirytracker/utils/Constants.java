package com.zn.expirytracker.utils;

import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Constants used throughout the app
 */
public class Constants {

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

    public static final String BITMAP_SAVING_DIRECTORY = "scannedImagesDir";
    public static final int BITMAP_SAVING_QUALITY = 30; // out of 100
    public static final String DEFAULT_FILENAME = "food";

    public static final double GREETING_GENERIC_THRESHOLD = 0.7; // above = TOD greet, below = generic
    public static final int GREETING_EVENING_BOUNDS = 17; // 24 hours
    public static final int GREETING_MORNING_BOUNDS = 5;
    public static final int GREETING_AFTERNOON_BOUNDS = 12;
}
