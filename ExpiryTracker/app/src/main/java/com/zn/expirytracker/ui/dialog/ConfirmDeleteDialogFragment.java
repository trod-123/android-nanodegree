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
    private static final String ARG_DELETE_TYPE =
            Toolbox.createStaticKeyString(ConfirmDeleteDialogFragment.class, "delete_type");

    private OnConfirmDeleteButtonClickListener callback;
    private String foodName;
    private int foodMsgResId;
    private boolean mLoggedIn = false;
    private DeleteType mDeleteType = DeleteType.ITEM;

    public interface OnConfirmDeleteButtonClickListener {
        void onConfirmDeleteButtonClicked(int position, boolean isLoggedIn, DeleteType type);
    }

    /**
     * Specifies an type that influences the Delete Dialog's title, message, and resulting action
     * (to be handled in the calling activity)
     */
    public enum DeleteType {
        ITEM, // for deleting a single item
        DEVICE, // for deleting all device data only
        ACCOUNT, // for deleting user account and device data
        SIGN_OUT // for deleting all device data only, but following with a different action from DEVICE
    }

    /**
     * Creates a new dialog to confirm deleting food items, all device data, or all cloud data.
     * The arguments shape up the message of the dialog. Handle the result accordingly via
     * {@link OnConfirmDeleteButtonClickListener}
     *
     * @param foodName   The name of the food that will be deleted. Pass {@code null}
     *                   if {@code DeleteType == DEVICE || ACCOUNT}
     * @param isLoggedIn {@code true} if this will prompt deleting item from both device and cloud.
     *                   Argument does not matter if {@code DeleteType != ITEM}
     * @param deleteType Specifies the dialog's title, message, and resulting action
     * @return
     */
    public static ConfirmDeleteDialogFragment newInstance(String foodName, boolean isLoggedIn,
                                                          DeleteType deleteType) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOOD_NAME_STRING, foodName);
        args.putBoolean(ARG_IS_LOGGED_IN_BOOL, isLoggedIn);
        args.putSerializable(ARG_DELETE_TYPE, deleteType);
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
            mLoggedIn = args.getBoolean(ARG_IS_LOGGED_IN_BOOL, false);
            foodMsgResId = mLoggedIn ?
                    R.string.edit_dialog_delete_confirm_message_has_account :
                    R.string.edit_dialog_delete_confirm_message_no_account;
            mDeleteType = (DeleteType) args.getSerializable(ARG_DELETE_TYPE);
        } else {
            foodName = getString(R.string.edit_dialog_delete_confirm_generic_food_name);
            foodMsgResId = R.string.edit_dialog_delete_confirm_message_no_account;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        switch (mDeleteType) {
            case ACCOUNT:
                title = getString(R.string.edit_dialog_delete_account_title);
                message = getString(R.string.edit_dialog_delete_account_message);
                break;
            case DEVICE:
                title = getString(R.string.edit_dialog_delete_device_data_title);
                message = getString(R.string.edit_dialog_delete_device_data_message);
                break;
            case ITEM:
                title = getString(R.string.edit_dialog_delete_confirm_title);
                message = getString(foodMsgResId, foodName);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Invalid DeleteType passed: %s", mDeleteType));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.edit_dialog_delete_confirm_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onConfirmDeleteButtonClicked(i, mLoggedIn, mDeleteType);
                            }
                        })
                .setPositiveButton(R.string.edit_dialog_delete_confirm_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onConfirmDeleteButtonClicked(i, mLoggedIn, mDeleteType);
                            }
                        });
        return builder.create();
    }
}
