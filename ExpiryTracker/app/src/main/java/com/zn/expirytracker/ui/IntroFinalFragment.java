package com.zn.expirytracker.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Toolbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroFinalFragment extends Fragment {

    @BindView(R.id.wel_title)
    TextView mTvTitle;
    @BindView(R.id.wel_description)
    TextView mTvDescription;
    @BindView(R.id.wel_image)
    ImageView mIv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wel_fragment_basic, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvTitle.setText(R.string.wel_page_final_title);
        mTvDescription.setText(R.string.wel_page_final_description);
        mIv.setImageResource(R.drawable.ic_check_white_24dp);

        // Set the agreement
        mTvDescription.append("\n\n\n\n");
        mTvDescription.append(Toolbox.getSpannableAgreementText(
                mTvDescription.getContext(), getString(R.string.auth_agreement_proceed,
                        getString(R.string.auth_terms), getString(R.string.auth_privacy_policy))));
        mTvDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
