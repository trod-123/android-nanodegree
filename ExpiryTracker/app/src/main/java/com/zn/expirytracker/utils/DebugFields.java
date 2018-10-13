package com.zn.expirytracker.utils;

import com.google.firebase.database.Logger;

/**
 * Convenience class hosting fields for enabling and disabling debug features at will
 */
public class DebugFields {

    /**
     * For demoing. {@code true} enables the Demo test account to show in the Sign-in activity
     */
    public static final boolean DEMO_TEST_ACCOUNT_ACTIVATED = false;
    public static final String DEMO_TEST_ACCOUNT_ID = "GWdCRZ7KL5hda4KgB9kngU1jiKL2";
    public static final String DEMO_TEST_EMAIL = "demo@zn.io";
    public static final String DEMO_TEST_PASSWORD = "7H3D066035W00F4ND7H3C0W6035M00!!!";

    /**
     * For debugging. {@code true} allows notifications to show up immediately after enabling them
     */
    public static final boolean ENABLE_QUICK_REMINDERS = false;

    /**
     * Pre-Firebase RTD data population. Keep {@code false}. Do not touch!
     */
    public static final boolean POPULATE_DUMMY_DATA = false;

    /**
     * See {@link com.google.firebase.database.FirebaseDatabase#setLogLevel(Logger.Level)} and
     * {@link com.google.firebase.database.Logger.Level#DEBUG}
     */
    public static final boolean ENABLE_FIREBASE_DATABASE_DEEP_LOGGING = false;

    /**
     * See {@link android.os.StrictMode}
     */
    public static final boolean ENABLE_STRICT_MODE = false;

    /**
     * For debugging. {@code true} allows
     * <ul>
     * <li>Setting expiry date before current date</li>
     * <li>Setting good thru date before expiry date</li>
     * </ul>
     */
    public static final boolean OVERRIDE_EXPIRY_DATE_RULES = false;
}
