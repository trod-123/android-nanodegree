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

public class ConfirmDeleteDialogFragment extends DialogFragment {

    private static final String ARG_FOOD_NAME_STRING =
            Toolbox.createStaticKeyString("confirm_delete_dialog.food_name");
    private static final String ARG_IS_LOGGED_IN_BOOL =
            Toolbox.createStaticKeyString("confirm_delete_dialog.is_logged_in");
    private static final String ARG_WIPE_ALL_BOOL =
            Toolbox.createStaticKeyString("confirm_delete_dialog.wipe_all");

    private OnConfirmDeleteButtonClickListener callback;
    private String foodName;
    private int messageResId;
    private boolean mWipeAll = false;

    public interface OnConfirmDeleteButtonClickListener {
        void onConfirmDeleteButtonClicked(int position);
    }

    public static ConfirmDeleteDialogFragment newInstance(String foodName, boolean isLoggedIn, boolean wipeAll) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOOD_NAME_STRING, foodName);
        args.putBoolean(ARG_IS_LOGGED_IN_BOOL, isLoggedIn);
        args.putBoolean(ARG_WIPE_ALL_BOOL, wipeAll);
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
            foodName = args.getString(ARG_FOOD_NAME_STRING,
                    getString(R.string.edit_dialog_delete_confirm_generic_food_name));
            boolean isLoggedIn = args.getBoolean(ARG_IS_LOGGED_IN_BOOL, false);
            messageResId = isLoggedIn ?
                    R.string.edit_dialog_delete_confirm_message_has_account :
                    R.string.edit_dialog_delete_confirm_message_no_account;
            mWipeAll = args.getBoolean(ARG_WIPE_ALL_BOOL, false);
        } else {
            foodName = getString(R.string.edit_dialog_delete_confirm_generic_food_name);
            messageResId = R.string.edit_dialog_delete_confirm_message_no_account;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mWipeAll ? R.string.edit_dialog_delete_device_data_title :
                R.string.edit_dialog_delete_confirm_title)
                .setMessage(mWipeAll ? getString(R.string.edit_dialog_delete_device_data_message) :
                        getString(messageResId, foodName))
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
