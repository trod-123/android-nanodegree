package com.zn.expirytracker.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
            DetailImageFragment.class, "image_uri_string");
    public static final String ARG_EDIT_MODE = Toolbox.createStaticKeyString(
            DetailImageFragment.class, "edit_mode");

    @BindView(R.id.iv_detail_image)
    ImageView mImageView;
    @BindView(R.id.pb_detail_image)
    ProgressBar mPb;
    @BindView(R.id.iv_detail_add_image_icon)
    ImageView mIvAddIcon;
    @BindView(R.id.iv_detail_image_broken)
    ImageView mIvBroken;
    @BindView(R.id.iv_detail_clear_image_icon)
    ImageView mIvClearIcon;

    private OnImageButtonClickListener mListener;
    @Nullable
    private String mImageUriString;
    private boolean mEditMode;

    public DetailImageFragment() {
        // Required empty public constructor
    }

    /**
     * Allows the hosting Activity or Fragment to handle adding or removing images
     */
    public interface OnImageButtonClickListener {
        /**
         * Adds an image to the current food item
         */
        void onAddImageButtonClick();

        /**
         * Removes the current image from the current food item
         *
         * @param uriString Uri of the current image
         */
        void onClearImageButtonClick(String uriString);
    }

    public static DetailImageFragment newInstance(String imageUri, boolean editMode) {
        DetailImageFragment fragment = new DetailImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI_STRING, imageUri);
        args.putBoolean(ARG_EDIT_MODE, editMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(DetailImageFragment.class.getSimpleName());

        Bundle args = getArguments();
        if (args != null) {
            mImageUriString = args.getString(ARG_IMAGE_URI_STRING, null);
            mEditMode = args.getBoolean(ARG_EDIT_MODE, false);
        }

        if (mEditMode) {
            try {
                mListener = (OnImageButtonClickListener) getParentFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Parent fragment must implement " +
                        "OnImageButtonClickListener");
            }
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
            mImageView.setImageDrawable(null);
            Toolbox.showView(mPb, true, false);
            Toolbox.loadImageFromUrl(getContext(), mImageUriString, mImageView,
                    new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Bitmap> target, boolean isFirstResource) {
                            Timber.e(e, "There was an error loading the image: %s", mImageUriString);
                            Toolbox.showView(mPb, false, false);
                            Toolbox.showView(mIvBroken, true, false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            Toolbox.showView(mPb, false, false);
                            Toolbox.showView(mIvBroken, false, false);
                            return false;
                        }
                    });
            if (mEditMode) {
                mIvClearIcon.setVisibility(View.VISIBLE);
                mIvClearIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClearImageButtonClick(mImageUriString);
                    }
                });
            }
        } else {
            // Set the add fragment
            mImageView.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.imageBackground_none));
            mIvAddIcon.setVisibility(View.VISIBLE);
            mIvAddIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAddImageButtonClick();
                }
            });
        }

        return rootView;
    }
}