package com.zn.expirytracker.ui.capture;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.ui.capture.barcodescanning.BarcodeScanningProcessor;
import com.zn.expirytracker.ui.capture.helpers.GraphicOverlay;
import com.zn.expirytracker.ui.capture.imagelabeling.ImageLabelingProcessor;
import com.zn.expirytracker.utils.Toolbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Allows users to add new items by scanning images and barcodes and fetches info about them
 */
public class CaptureActivity extends AppCompatActivity implements
        View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        BarcodeScanningProcessor.OnRecognizedBarcodeListener {

    private static final InputType DEFAULT_INPUT_TYPE = InputType.BARCODE;
    private static final float ALPHA_ACTIVATED = 1f;
    private static final float ALPHA_DEACTIVATED = 0.3f;
    private static int DURATION_TRANSITION;

    private static final int PERMISSION_REQUESTS = 1;

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
    @BindView(R.id.firePreview)
    CameraSourcePreview mPreview;
    @BindView(R.id.fireFaceOverlay)
    GraphicOverlay mGraphicOverlay;

    private CameraSource mCameraSource;
    private InputType mCurrentInputType = DEFAULT_INPUT_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);

        detectCamera(); // confirm if device has camera before proceeding

        DURATION_TRANSITION = getResources().getInteger(R.integer.default_transition_duration);
        mBtnCaptureBarcode.setOnClickListener(this);
        mBtnCaptureImgrec.setOnClickListener(this);
        mFragmentRoot.setOnClickListener(this);

        if (allPermissionsGranted()) {
            createCameraSource(mCurrentInputType);
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
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
                // Dismiss the overlay fragment first if it's showing
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
     * Instead of setting a click listener for each video individually, do it all here
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_capture_barcode:
                setInputType(InputType.BARCODE);
                break;
            case R.id.layout_capture_imgrec:
                setInputType(InputType.IMG_REC);
                break;
            case R.id.container_overlay_fragment:
                // Prevent clicking on the "dialog" from dismissing the fragment due to root's click listener
                break;
        }
    }

    @Override
    public void handleBarcode(FirebaseVisionBarcode barcode) {
        loadResultOverlay(barcode.getDisplayValue());
    }

    /**
     * Load the captured image result overlay
     */
    private void loadResultOverlay(String barcode) {
        CaptureOverlayFragment fragment =
                CaptureOverlayFragment.newInstance(mCurrentInputType, barcode);
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
        activateCameraSource(activate);
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
                    // TODO: Implement
                    mTvInstruction.setText("Still to be implemented");
                    //mTvInstruction.setText(R.string.capture_mode_imgrec_instruction);
                    mBtnCaptureBarcode.animate().alpha(ALPHA_DEACTIVATED);
                    mBtnCaptureImgrec.animate().alpha(ALPHA_ACTIVATED);
                    break;
            }
            if (allPermissionsGranted()) {
                createCameraSource(mCurrentInputType);
                startCameraSource();
            } else {
                getRuntimePermissions();
            }
        }
    }

    // region Firebase Vision listeners

    private OnSuccessListener<List<FirebaseVisionBarcode>> barcodeDetectorOnSuccessListener =
            new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                @Override
                public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                    if (firebaseVisionBarcodes != null && firebaseVisionBarcodes.size() > 0) {
                        // Only get the first barcode
                        String value = firebaseVisionBarcodes.get(0).getDisplayValue();
                        loadResultOverlay(value);
                    } else {
                        Timber.e("There were no barcodes in the detected list");
                    }
                }
            };

    private OnFailureListener barcodeDetectorOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Timber.e(e, "There was an error detecting the barcode in the passed image");
        }
    };

    private OnSuccessListener<List<FirebaseVisionLabel>> imgRecOnSuccessListener_OnDevice =
            new OnSuccessListener<List<FirebaseVisionLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionLabel> labels) {
                    if (labels != null && labels.size() > 0) {
                        CaptureActivity.this.handleLabels_OnDevice(labels);
                    } else {
                        Timber.e("There were no labels in the detected list");
                    }
                }
            };

    private OnSuccessListener<List<FirebaseVisionCloudLabel>> imgRecOnSuccessListener_OnCloud =
            new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                    if (labels != null && labels.size() > 0) {
                        handleLabels_OnCloud(labels);
                    } else {
                        Timber.e("There were no labels in the detected list");
                    }
                }
            };

    private OnFailureListener imgRecOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Timber.e(e, "There was an error recognizing the image");
        }
    };

    private void handleLabels_OnDevice(List<FirebaseVisionLabel> labels) {

    }

    private void handleLabels_OnCloud(List<FirebaseVisionCloudLabel> labels) {

    }

    // endregion

    // region Camera
    // Source: Firebase MLKit samples https://github.com/firebase/quickstart-android/tree/master/mlkit

    /**
     * Checks if device does not have camera, leave the activity
     */
    private void detectCamera() {
        if (!Toolbox.checkCameraHardware(this)) {
            // Device has no camera hardware, so quit
            Toolbox.showToast(this, getString(R.string.error_requires_camera));
            finish();
        }
    }

    private void createCameraSource(InputType inputType) {
        // If there's no existing cameraSource, create one.
        if (mCameraSource == null) {
            mCameraSource = new CameraSource(this, mGraphicOverlay);
        }
        switch (inputType) {
//                case CLASSIFICATION:
//                    Log.i(TAG, "Using Custom Image Classifier Processor");
//                    mCameraSource.setMachineLearningFrameProcessor(new CustomImageClassifierProcessor(this));
//                    break;
//                case TEXT_DETECTION:
//                    Log.i(TAG, "Using Text Detector Processor");
//                    mCameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor());
//                    break;
            case BARCODE:
                Timber.i("Using Barcode Detector Processor");
                mCameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this));
                break;
            case IMG_REC:
                Timber.i("Using Image Label Detector Processor");
                mCameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor());
                break;
        }
    }

    /**
     * Stop scans temporarily
     *
     * @param activate
     */
    private void activateCameraSource(boolean activate) {
        // If there's no existing cameraSource, create one.
        if (mCameraSource == null) {
            mCameraSource = new CameraSource(this, mGraphicOverlay);
        }
        if (activate) {
            createCameraSource(mCurrentInputType);
        } else {
            mCameraSource.setMachineLearningFrameProcessor(null);
        }
        startCameraSource();
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                mGraphicOverlay.clear();
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Timber.e(e, "Unable to start camera source.");
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    // endregion

    // region Permissions
    // Source: Firebase MLKit samples https://github.com/firebase/quickstart-android/tree/master/mlkit

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Timber.i("Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(mCurrentInputType);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Timber.i("Permission granted: %s", permission);
            return true;
        }
        Timber.i("Permission NOT granted: %s", permission);
        return false;
    }

    // endregion
}
