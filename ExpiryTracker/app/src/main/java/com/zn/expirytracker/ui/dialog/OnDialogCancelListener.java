package com.zn.expirytracker.ui.dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

/**
 * Allows hosting activity or fragment to listen for users dismissing dialogs through pressing the
 * back button, or tapping outside the dialog
 * <p>
 * {@link DialogFragment} does not allow setting
 * {@link android.app.Dialog#setOnCancelListener(DialogInterface.OnCancelListener)} or
 * {@link android.app.Dialog#setOnDismissListener(DialogInterface.OnDismissListener)}. Overriding
 * {@link DialogFragment#onCancel(DialogInterface)} does the trick, and this is where you can call
 * {@link OnDialogCancelListener#onCancelled(Class, DialogInterface)}
 * <p>
 * Source: https://stackoverflow.com/questions/18267916/setoncancellistener-and-setondismisslistener-is-not-called-for-alertdialog-for-b
 */
public interface OnDialogCancelListener {
    /**
     * @param klass           For distinguishing dialogs to take unique action
     * @param dialogInterface
     */
    void onCancelled(Class klass, DialogInterface dialogInterface);
}
