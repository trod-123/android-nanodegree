package com.zn.expirytracker.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.zn.expirytracker.R;

public class FormChangedDialogFragment extends DialogFragment {
    private OnFormChangedButtonClickListener callback;

    public interface OnFormChangedButtonClickListener {
        void onFormChangedButtonClicked(int position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (FormChangedDialogFragment.OnFormChangedButtonClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnFormChangedButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.edit_dialog_changes_not_saved)
                .setNegativeButton(R.string.edit_dialog_changes_not_saved_discard,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onFormChangedButtonClicked(i);
                            }
                        })
                .setPositiveButton(R.string.edit_dialog_changes_not_saved_save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callback.onFormChangedButtonClicked(i);
                            }
                        });
        return builder.create();
    }
}
