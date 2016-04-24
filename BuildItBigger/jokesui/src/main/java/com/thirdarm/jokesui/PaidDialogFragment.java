package com.thirdarm.jokesui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Custom dialog fragment for adding jokes
 */
public class PaidDialogFragment extends DialogFragment {
    private static final String LOG_TAG = PaidDialogFragment.class.getSimpleName();

    private MaterialDialog mDialog;

    public PaidDialogFragment() {
    }

    @Override
    public MaterialDialog getDialog() {
        return mDialog;
    }

    @NonNull
    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog
        mDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.dialog_paid_title)
                .content(R.string.dialog_paid_message)
                .positiveText(R.string.dialog_paid_positive_button)
                .negativeText(R.string.dialog_paid_negative_button)
                .neutralText(R.string.dialog_paid_neutral_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(getContext(),
                                getString(R.string.dialog_paid_positive_toast), Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utilities.getJokesFragment(getActivity()).startResetJokeContainerTask();
                        // For creating the action performed when clicking the notification
                        // (1) Create an intent that will open up an activity
                        Intent intent = new Intent(getContext(), getActivity().getClass());
                        // (2) Create a pending intent from this intent
                        PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        // (3) Attach the pending intent to the notification using setContentIntent()
                        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.drawable.gray_circle)
                                .setContentTitle("Loser")
                                .setContentText(getString(R.string.dialog_paid_negative_notification))
                                // Set default sound, vibration, and notification light settings
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setContentIntent(pIntent)
                                // Hide the notification after it is selected
                                .setAutoCancel(true);

                        ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, nBuilder.build());
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        throw new Error("You deserved it.");
                    }
                })
                .show();

        return mDialog;
    }
}