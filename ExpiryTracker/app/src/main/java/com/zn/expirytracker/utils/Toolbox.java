package com.zn.expirytracker.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Just a class of neat convenient global helper methods
 */
public class Toolbox {

    private static Toast mToast;

    public static int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static int DEFAULT_SNACKBAR_LENGTH = Snackbar.LENGTH_LONG;

    /**
     * Display toasts, ensuring they do not overlap with each other
     *
     * @param context
     * @param message
     */
    public static void showToast(@NonNull Context context, @NonNull String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, DEFAULT_TOAST_LENGTH);
        mToast.show();
    }
}