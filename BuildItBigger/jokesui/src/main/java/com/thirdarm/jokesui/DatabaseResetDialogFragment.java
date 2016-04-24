package com.thirdarm.jokesui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by TROD on 20160419.
 */
public class DatabaseResetDialogFragment extends DialogFragment {
    private static final String LOG_TAG = DatabaseResetDialogFragment.class.getSimpleName();

    private MaterialDialog mDialog;

    public DatabaseResetDialogFragment() {
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
                .title(R.string.dialog_joke_reset_title)
                .content(R.string.dialog_joke_reset_content)
                .positiveText(R.string.dialog_joke_reset_positive_button)
                .negativeText(R.string.dialog_joke_reset_negative_button)
                .neutralText(R.string.dialog_joke_reset_neutral_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utilities.getJokesFragment(getActivity()).startResetJokeContainerTask();
                    }
                })
                .show();

        return mDialog;
    }
}
