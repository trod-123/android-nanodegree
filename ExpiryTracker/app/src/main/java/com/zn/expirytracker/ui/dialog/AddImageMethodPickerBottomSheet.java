package com.zn.expirytracker.ui.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.expirytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddImageMethodPickerBottomSheet extends BottomSheetDialogFragment
        implements View.OnClickListener {

    @BindView(R.id.action_add_photo_camera)
    View mBtnCamera;
    @BindView(R.id.action_add_photo_library)
    View mBtnLibrary;

    OnAddImageMethodSelectedListener mListener;

    public interface OnAddImageMethodSelectedListener {
        void onCameraInputSelected();

        void onImagePickerSelected();
    }

    public static AddImageMethodPickerBottomSheet newInstance() {
        return new AddImageMethodPickerBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (OnAddImageMethodSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnAddImageMethodSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_capture_chooser, container, false);
        ButterKnife.bind(this, view);

        mBtnCamera.setOnClickListener(this);
        mBtnLibrary.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_add_photo_camera:
                mListener.onCameraInputSelected();
                dismiss();
                break;
            case R.id.action_add_photo_library:
                mListener.onImagePickerSelected();
                dismiss();
                break;
        }
    }
}
