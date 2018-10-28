package com.zn.expirytracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.zn.expirytracker.constants.Constants;
import com.zn.expirytracker.constants.KeyConstants;

import androidx.annotation.NonNull;

/**
 * Ad mob helper class
 */
public class AdStrategy {

    private static final String KEY_ENABLE_ADS = "enable_ads";

    /**
     * Persist a value for enabling ads using SharedPreferences
     * <p>
     * TODO: Implement using a Google Play in-app purchase implementation, and update the SP
     * value accordingly
     *
     * @param context
     * @param enable
     */
    public static void enableAds(Context context, boolean enable) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_ENABLE_ADS, enable).apply();
    }

    /**
     * Checks SharedPreferences if ads are enabled. Returns {@code true} by default
     * <p>
     * TODO: Implement using a Google Play in-app purchase implementation, and update the SP value
     * accordingly
     *
     * @param context
     * @return
     */
    public static boolean areAdsEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_ENABLE_ADS, KeyConstants.ALWAYS_SHOW_ADS);
    }

    /**
     * Initialize ads for this app, only if ads are enabled. Needs to only be called once during
     * each app session
     *
     * @param context
     */
    public static void initializeAds(Context context) {
        if (areAdsEnabled(context)) {
            MobileAds.initialize(context, BuildConfig.AdMobId);
        }
    }

    /**
     * Loads the ad for the provided AdView, only if ads are enabled
     */
    public static void loadAds(@NonNull AdView adView) {
        if (areAdsEnabled(adView.getContext())) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }
}
