package com.zn.expirytracker.ui.capture;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.InputType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaptureActivity extends AppCompatActivity {

    private static final InputType DEFAULT_INPUT_TYPE = InputType.BARCODE;
    private static final float ALPHA_ACTIVATED = 1f;
    private static final float ALPHA_DEACTIVATED = 0.3f;
    private static int DURATION_TRANSITION;

    @BindView(R.id.layout_capture_root)
    View mRootView;
    @BindView(R.id.layout_capture_barcode)
    View mBtnCaptureBarcode;
    @BindView(R.id.layout_capture_imgrec)
    View mBtnCaptureImgrec;
    @BindView(R.id.tv_capture_instruction)
    TextView mTvInstruction;
    @BindView(R.id.container_overlay_fragment)
    View mFragmentRoot;

    private InputType mCurrentInputType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);

        DURATION_TRANSITION = getResources().getInteger(R.integer.default_transition_duration);

        mBtnCaptureBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputType(InputType.BARCODE);
            }
        });
        mBtnCaptureImgrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputType(InputType.IMG_REC);
            }
        });
        mFragmentRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent clicking on the "dialog" from dismissing the fragment due to root's click listener
            }
        });
        // TODO: Only for testing
        mTvInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOverlay();
            }
        });
        setInputType(DEFAULT_INPUT_TYPE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Emulate the back button when pressing the up button, to prevent parent activity from
            // getting recreated
            // https://stackoverflow.com/questions/22947713/make-the-up-button-behave-like-the-back-button-on-android
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            super.onBackPressed();
        } else {
            Fragment topFragment = fragments.get(fragments.size() - 1);
            if (topFragment instanceof CaptureOverlayFragment) {
                activateRoot(true);
            }
            if (fragments.size() > 1) {
                // Handle cases where Glide somehow is the top fragment instead
                Fragment secondToTopFragment = fragments.get(fragments.size() - 2);
                if (secondToTopFragment instanceof CaptureOverlayFragment) {
                    activateRoot(true);
                }
            }
            super.onBackPressed();
        }
    }

    /**
     * Load the captured image result overlay
     */
    private void loadOverlay() {
        CaptureOverlayFragment fragment =
                CaptureOverlayFragment.newInstance(mCurrentInputType, null);
        getSupportFragmentManager().beginTransaction()
                // TODO: Fade out
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .setCustomAnimations(R.anim.overlay_capture_fade_in, R.anim.overlay_capture_fade_out,
//                        R.anim.overlay_capture_fade_in, R.anim.overlay_capture_fade_out)
                .add(R.id.container_overlay_fragment, fragment,
                        CaptureOverlayFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
        activateRoot(false);

        // TODO: Fetch data and show loading indicator

    }

    /**
     * Fades the root view in and out of the background
     *
     * @param activate
     */
    private void activateRoot(boolean activate) {
        float alpha = activate ? ALPHA_ACTIVATED : ALPHA_DEACTIVATED;
        View.OnClickListener listener = activate ? null : new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        };
        mRootView.animate().setDuration(DURATION_TRANSITION).alpha(alpha);
        mBtnCaptureImgrec.setEnabled(activate);
        mBtnCaptureBarcode.setEnabled(activate);
        mRootView.setOnClickListener(listener);
    }

    /**
     * Sets the current input type
     *
     * @param inputType
     */
    private void setInputType(InputType inputType) {
        if (mCurrentInputType != inputType) {
            // Only action if changing
            mCurrentInputType = inputType;
            switch (inputType) {
                case BARCODE:
                    mTvInstruction.setText(R.string.capture_mode_barcode_instruction);
                    mBtnCaptureBarcode.animate().alpha(ALPHA_ACTIVATED);
                    mBtnCaptureImgrec.animate().alpha(ALPHA_DEACTIVATED);
                    break;
                case IMG_REC:
                    mTvInstruction.setText(R.string.capture_mode_imgrec_instruction);
                    mBtnCaptureBarcode.animate().alpha(ALPHA_DEACTIVATED);
                    mBtnCaptureImgrec.animate().alpha(ALPHA_ACTIVATED);
                    break;
            }
        }
    }
}
