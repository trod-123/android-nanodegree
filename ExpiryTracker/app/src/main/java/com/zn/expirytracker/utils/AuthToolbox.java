package com.zn.expirytracker.utils;

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

    public static void signIn(boolean signIn) {
        // TODO: Implement
        mSignedIn = signIn;
    }
}
