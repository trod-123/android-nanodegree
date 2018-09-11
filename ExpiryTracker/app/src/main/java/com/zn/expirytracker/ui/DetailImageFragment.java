package com.zn.expirytracker.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
            "detail_add_image_fragment.food_id");

    @BindView(R.id.iv_detail_image)
    ImageView mImageView;
    @BindView(R.id.iv_detail_add_image_icon)
    ImageView mIvAddIcon;

    private long mFoodId;
    private String mImageUriString;

    public DetailImageFragment() {
        // Required empty public constructor
    }

    public static DetailImageFragment newInstance(int foodId, String imageUri) {
        DetailImageFragment fragment = new DetailImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FOOD_ID, foodId);
        args.putString(ARG_IMAGE_URI_STRING, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mFoodId = args.getInt(ARG_FOOD_ID);
            mImageUriString = args.getString(ARG_IMAGE_URI_STRING, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_detail_image, container, false);
        Timber.tag(DetailImageFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        if (mImageUriString != null) {
            Toolbox.loadImageFromUrl(getContext(), mImageUriString, mImageView, new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            });
            mImageView.setImageDrawable(null);
            mIvAddIcon.setVisibility(View.GONE);
        } else {
            // Set the add fragment
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