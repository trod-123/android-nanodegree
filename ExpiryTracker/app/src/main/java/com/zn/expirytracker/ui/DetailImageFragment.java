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
    public static final String ARG_EDIT_MODE = Toolbox.createStaticKeyString(
            DetailImageFragment.class, "edit_mode");

    @BindView(R.id.iv_detail_image)
    ImageView mImageView;
    @BindView(R.id.iv_detail_add_image_icon)
    ImageView mIvAddIcon;

    private AddImageButtonClickListener mListener;
    private String mImageUriString;
    private boolean mEditMode;

    public DetailImageFragment() {
        // Required empty public constructor
    }

    public interface AddImageButtonClickListener {
        void onAddImageButtonSelected();
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
                mListener = (AddImageButtonClickListener) getParentFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Parent fragment must implement " +
                        "AddImageButtonClickListener");
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
            Toolbox.loadImageFromUrl(getContext(), mImageUriString, mImageView, new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    Timber.e(e, "There was an error loading the image: %s", mImageUriString);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            });
        } else {
            // Set the add fragment
            mImageView.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.imageBackground_none));
            mIvAddIcon.setVisibility(View.VISIBLE);
            mIvAddIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAddImageButtonSelected();
                }
            });
        }

        return rootView;
    }
}