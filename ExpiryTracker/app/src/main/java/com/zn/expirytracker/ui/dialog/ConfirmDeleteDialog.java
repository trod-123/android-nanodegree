package com.zn.expirytracker.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Toolbox;

public class ConfirmDeleteDialog extends DialogFragment {

    private static final String ARG_FOOD_NAME =
            Toolbox.createStaticKeyString("confirm_delete_dialog.food_name");
    private static final String ARG_IS_LOGGED_IN =
            Toolbox.createStaticKeyString("confirm_delete_dialog.is_logged_in");

    private OnConfirmDeleteButtonClickListener callback;
    private String foodName;
    private int messageResId;

    public interface OnConfirmDeleteButtonClickListener {
        void onConfirmDeleteButtonClicked(int position);
    }

    public static ConfirmDeleteDialog newInstance(String foodName, boolean isLoggedIn) {
        ConfirmDeleteDialog fragment = new ConfirmDeleteDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FOOD_NAME, foodName);
        args.putBoolean(ARG_IS_LOGGED_IN, isLoggedIn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (OnConfirmDeleteButtonClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnConfirmDeleteButtonClickListener");
        }

        Bundle args = getArguments();
        if (args != null) {
            foodName = args.getString(ARG_FOOD_NAME,
                    getString(R.string.edit_dialog_delete_confirm_generic_food_name));
            boolean isLoggedIn = args.getBoolean(ARG_IS_LOGGED_IN, false);
            messageResId = isLoggedIn ?
                    R.string.edit_dialog_delete_confirm_message_has_account :
                    R.string.edit_dialog_delete_confirm_message_no_account;
        } else {
            foodName = getString(R.string.edit_dialog_delete_confirm_generic_food_name);
            messageResId = R.string.edit_dialog_delete_confirm_message_no_account;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit_dialog_delete_confirm_title)
                .setMessage(getString(messageResId, foodName))
                .setNeutralButton(R.string.edit_dialog_delete_confirm_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onConfirmDeleteButtonClicked(i);
                            }
                        })
                .setPositiveButton(R.string.edit_dialog_delete_confirm_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onConfirmDeleteButtonClicked(i);
                            }
                        });
        return builder.create();
    }
}
