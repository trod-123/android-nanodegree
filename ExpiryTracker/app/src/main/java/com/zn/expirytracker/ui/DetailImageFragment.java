package com.zn.expirytracker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment that only contains an image
 */
public class DetailImageFragment extends Fragment {

    public static final String ARG_IMAGE_URI_STRING = Toolbox.createStaticKeyString(
            "detail_image_fragment.image_uri_string");
    public static final String ARG_FOOD_ID = Toolbox.createStaticKeyString(
            "detail_add_image_fragment.food_id"
    );

    @BindView(R.id.iv_detail_image)
    ImageView mImageView;
    @BindView(R.id.iv_detail_add_image_icon)
    ImageView mIvAddIcon;

    private int mFoodId;
    // TODO: Change this to String once URIs are available
    private int mImageUriString;

    public DetailImageFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     *
     * @return
     */
    public static DetailImageFragment newInstance(int foodId, int imageUri) {
        DetailImageFragment fragment = new DetailImageFragment();
        Bundle args = new Bundle();
        // TODO: For testing, load the imageview with a color
        args.putInt(ARG_FOOD_ID, foodId);
        args.putInt(ARG_IMAGE_URI_STRING, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mFoodId = args.getInt(ARG_FOOD_ID);
            mImageUriString = args.getInt(ARG_IMAGE_URI_STRING, Color.BLACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_detail_image, container, false);
        Timber.tag(DetailImageFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        // TODO: For testing, set a color instead of an image
        if (mImageUriString != -1) {
            mImageView.setBackgroundColor(mImageUriString);
            mIvAddIcon.setVisibility(View.GONE);
        } else {
            mImageView.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.imageBackground_none));
            mIvAddIcon.setVisibility(View.VISIBLE);
        }
        mIvAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toolbox.showToast(getContext(), "This will add an image");
            }
        });

        return rootView;
    }
}