package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zn.expirytracker.R;

/**
 * Set of helper methods for user authentication
 */
public class AuthToolbox {

    private static boolean mSignedIn;

    /**
     * Returns true if the user is currently signed in
     *
     * @return
     */
    public static boolean checkIfSignedIn() {
        // TODO: Implement
        return mSignedIn;
    }

    public static void updateDisplayName(String name) {
        // TODO: Implement
    }

    public static void signIn(Context context, boolean signIn) {
        // TODO: Implement. The below code should occur if sign-in was successful
        mSignedIn = signIn;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putBoolean(context.getString(R.string.pref_account_signed_in_key), mSignedIn)
                .apply();
    }
}
