package com.zn.expirytracker.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.zn.expirytracker.R;

/**
 * {@link DialogFragment} that prompts for the {@link com.zn.expirytracker.data.model.Storage}
 * location of an item
 */
public class StorageLocationDialogFragment extends DialogFragment {
    private OnStorageLocationSelectedListener callback;

    public interface OnStorageLocationSelectedListener {
        void onStorageLocationSelected(int position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (OnStorageLocationSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnStorageLocationSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.food_storage_location_dialog)
                .setItems(R.array.food_storage_location_labels, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onStorageLocationSelected(i);
                    }
                });
        return builder.create();
    }
}
