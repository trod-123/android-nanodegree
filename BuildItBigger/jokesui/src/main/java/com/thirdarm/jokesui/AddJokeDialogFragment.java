package com.thirdarm.jokesui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom dialog fragment for adding jokes
 */
public class AddJokeDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AddJokeDialogFragment.class.getSimpleName();

    private MaterialDialog mDialog;
    private View customView;

    private String mAddDialogTitle;
    private String mAddDialogJoke;

    public AddJokeDialogFragment() {
    }

    @Override
    public MaterialDialog getDialog() {
        return mDialog;
    }

    @NonNull
    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        mAddDialogTitle = "";
        mAddDialogJoke = "";
        // Get the layout inflater to Add the custom dialog layout
        customView = getActivity().getLayoutInflater().inflate(R.layout.dialog_addjoke, null);

        // Set the text watches for the edit text fields
        EditText etTitle = (EditText) customView.findViewById(R.id.dialog_add_joke_edittext_name);
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddDialogTitle = s.toString();
                enableDisableAddJokeDialogPositiveButton();
            }
        });
        EditText etJoke = (EditText) customView.findViewById(R.id.dialog_add_joke_edittext_joke);
        etJoke.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddDialogJoke = s.toString();
                enableDisableAddJokeDialogPositiveButton();
            }
        });

        // Build the dialog
        mDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_add_joke_title)
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                .customView(customView, false)
                // Set the action buttons
                .positiveText(R.string.dialog_add_joke_positive_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String joke = ((EditText) customView.findViewById(R.id.dialog_add_joke_edittext_joke)).getText().toString();
                        String jokeName = ((EditText) customView.findViewById(R.id.dialog_add_joke_edittext_name)).getText().toString();
                        List<Integer> categoryIds = new ArrayList<>();
                        categoryIds.add(0);
                        Utilities.getJokesFragment(getActivity())
                                .startAddJokeTask(joke, jokeName, categoryIds);
                    }
                })
                .negativeText(R.string.dialog_add_joke_negative_button)
                .build();

        // Positive Button is enabled if and only if all edit text fields are populated with at
        // least one character.
        enableDisableAddJokeDialogPositiveButton();

        return mDialog;
    }

    /**
     * Helper method that checks the lengths of the AddJokeDialog text fields
     * @return True if both text fields are populated
     */
    public boolean checkAddJokeDialogTextFieldLength () {
        return mAddDialogTitle.length() > 0 && mAddDialogJoke.length() > 0;
    }

    /**
     * Helper method that enables or disables the add joke dialog positive button based on populated
     * length of each of the add joke dialog text fields
     */
    public void enableDisableAddJokeDialogPositiveButton () {
        if (checkAddJokeDialogTextFieldLength()) {
            mDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        } else {
            mDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }
}