package com.zn.expirytracker.ui.capture;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CaptureOverlayFragment extends Fragment {

    public static final String ARG_INPUT_TYPE = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.input_type");
    public static final String ARG_BARCODE = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.barcode");

    @BindView(R.id.btn_overlay_scanned_positive)
    Button mBtnPositive;
    @BindView(R.id.btn_overlay_scanned_negative)
    Button mBtnNegative;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private InputType mInputType;
    private String mBarcode;

    public CaptureOverlayFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     *
     * @return
     */
    public static CaptureOverlayFragment newInstance(InputType inputType, @Nullable String barcode) {
        CaptureOverlayFragment fragment = new CaptureOverlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INPUT_TYPE, inputType);
        args.putString(ARG_BARCODE, barcode);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mInputType = (InputType) args.getSerializable(ARG_INPUT_TYPE);
            mBarcode = args.getString(ARG_BARCODE, "");
        }

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_overlay_capture, container, false);
        Timber.tag(CaptureOverlayFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
        mBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHostActivity.onBackPressed();
            }
        });

        return rootView;
    }

    private void saveItem() {
        // TODO: Implement
        Toolbox.showToast(mHostActivity, "This will save the item!");
        mHostActivity.onBackPressed();
    }
}
