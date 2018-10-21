package com.zn.expirytracker.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zn.expirytracker.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddItemInputPickerBottomSheet extends BottomSheetDialogFragment
        implements View.OnClickListener {

    @BindView(R.id.action_add_item_camera)
    View mBtnCamera;
    @BindView(R.id.action_add_item_text)
    View mBtnText;

    OnInputMethodSelectedListener mListener;

    public interface OnInputMethodSelectedListener {
        void onCameraInputSelected();

        void onTextInputSelected();

        void onCancelled();
    }

    public static AddItemInputPickerBottomSheet newInstance() {
        return new AddItemInputPickerBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (OnInputMethodSelectedListener) getTargetFragment();
            if (mListener == null) {
                // Activity is likely calling this instead of a Fragment
                mListener = (OnInputMethodSelectedListener) getActivity();
            }
        } catch (ClassCastException | IllegalStateException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnInputMethodSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_input_chooser, container, false);
        ButterKnife.bind(this, view);

        mBtnCamera.setOnClickListener(this);
        mBtnText.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_add_item_camera:
                mListener.onCameraInputSelected();
                dismiss();
                break;
            case R.id.action_add_item_text:
                mListener.onTextInputSelected();
                dismiss();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onCancelled();
    }
}
