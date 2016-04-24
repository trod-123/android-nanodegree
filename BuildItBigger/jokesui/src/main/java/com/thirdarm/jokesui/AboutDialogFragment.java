package com.thirdarm.jokesui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by TROD on 20160419.
 */
public class AboutDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AboutDialogFragment.class.getSimpleName();

    private MaterialDialog mDialog;

    public AboutDialogFragment() {
    }

    @Override
    public MaterialDialog getDialog() {
        return mDialog;
    }

    @NonNull
    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, null);

        // Build the dialog
        mDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.about_title)
                .customView(view, false)
                .positiveText(R.string.about_dismiss)
                .show();

        return mDialog;
    }
}
