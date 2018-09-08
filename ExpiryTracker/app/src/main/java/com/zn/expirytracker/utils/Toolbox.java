package com.zn.expirytracker.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
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

    /**
     * General utility method that shows the options menu without needing an action bar
     * Source: https://stackoverflow.com/questions/30417223/how-to-add-menu-button-without-action-bar
     */
    public static void showMenuPopup(Context context, View view, int menuResId,
                                     PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(listener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuResId, popup.getMenu());
        popup.show();
    }

    /**
     * Creates a uniformly structured key with the provided {@code name}, prepending the app's
     * package name. Package name explicitly declared here to allow this method to be called outside
     * the target's scope
     *
     * @param name
     * @return
     */
    public static String createStaticKeyString(String name) {
        return String.format("com.zn.expirytracker.%s", name);
    }
}