package com.zn.expirytracker.ui.capture;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.ui.capture.barcodescanning.BarcodeScanningProcessor;
import com.zn.expirytracker.ui.capture.helpers.GraphicOverlay;
import com.zn.expirytracker.ui.capture.imagelabeling.ImageLabelingProcessor;
import com.zn.expirytracker.utils.Constants;
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
        BarcodeScanningProcessor.OnRecognizedBarcodeListener,
        ImageLabelingProcessor.OnImageRecognizedListener {

    private static final InputType DEFAULT_INPUT_TYPE = InputType.BARCODE;

    private static final int PERMISSION_REQUESTS = 1;
    private static final int SCAN_JITTER_SECS = 1000 * 2; // millis

    @BindView(R.id.layout_capture_root)
    View mRootView;
    @BindView(R.id.layout_capture_barcode)
    View mBtnCaptureBarcode;
    @BindView(R.id.layout_capture_imgrec)
    View mBtnCaptureImgrec;
    @BindView(R.id.layout_capture_imgonly)
    View mBtnCaptureImgonly;
    @BindView(R.id.tv_capture_instruction)
    TextView mTvInstruction;
    @BindView(R.id.container_overlay_fragment)
    View mFragmentRoot;
    @BindView(R.id.firePreview)
    CameraSourcePreview mPreview;
    @BindView(R.id.fireFaceOverlay)
    GraphicOverlay mGraphicOverlay;
    @BindView(R.id.btn_capture_image)
    View mBtnImgRec;

    private CameraSource mCameraSource;
    private InputType mCurrentInputType = DEFAULT_INPUT_TYPE;
    private boolean mCameraActivated;

    private Thread mJitterThread;

    private boolean mScanJitter = false; // prevent consecutive scans from happening too close with each other

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);

        detectCamera(); // confirm if device has camera before proceeding

        mBtnCaptureBarcode.setOnClickListener(this);
        mBtnCaptureImgrec.setOnClickListener(this);
        mBtnCaptureImgonly.setOnClickListener(this);
        mFragmentRoot.setOnClickListener(this);

        if (allPermissionsGranted()) {
            startCameraSource();
            setupFrameProcessing(mCurrentInputType);
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
        if (mCameraActivated) {
            // Restore the frame processor. Make sure camera is started before this
            // This handles situations where the overlay is dismissed before data finished loading,
            // but does not take care of situations where user dismisses the dialog after it's
            // been shown
            setupFrameProcessing(mCurrentInputType);
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop(); // also stops the frame processor
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

    // Called first before onResume()
    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            super.onBackPressed();
        } else {
            for (Fragment fragment : fragments) {
                String tag = fragment.getTag();
                if (tag != null && tag.equals(CaptureOverlayFragment.class.getSimpleName())) {
                    activateRoot(true);
                    break;
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
            case R.id.layout_capture_imgonly:
                setInputType(InputType.IMG_ONLY);
                break;
            case R.id.container_overlay_fragment:
                // Prevent clicking on the "dialog" from dismissing the fragment due to root's click listener
                break;
        }
    }

    /**
     * Load up the overlay.
     * <p>
     * See {@link CaptureActivity#startJitterCountdown()}
     *
     * @param barcode
     * @param barcodeBitmap
     */
    @Override
    public void handleBarcode(FirebaseVisionBarcode barcode, Bitmap barcodeBitmap) {
        if (!mScanJitter) {
            loadResultOverlay(barcode.getDisplayValue(), barcodeBitmap);
            mScanJitter = true;
        }
    }

    /**
     * Load up the overlay
     *
     * @param labels
     * @param bitmap
     */
    @Override
    public void handleImage(@NonNull List<FirebaseVisionLabel> labels, Bitmap bitmap) {
        if (mCurrentInputType == InputType.IMG_REC) {
            // TODO: Implement and pass to overlay. Currently shows dummy toast with labels
            StringBuilder sb = new StringBuilder();
            for (FirebaseVisionLabel label : labels) {
                sb.append(label.getLabel());
                sb.append(", ");
            }
            Toolbox.showToast(this, sb.toString().substring(0, sb.length() - 2));
        } else if (mCurrentInputType == InputType.IMG_ONLY) {
            // TODO: Get IMGONLY its own method, it will not be called from here
            loadResultOverlay(bitmap);
            mScanJitter = true;
        }
    }

    /**
     * Load the captured image result overlay for the barcode
     */
    private void loadResultOverlay(String barcode, Bitmap barcodeImage) {
        activateRoot(false);
        CaptureOverlayFragment fragment =
                CaptureOverlayFragment.newInstance_BarcodeInput(barcode, barcodeImage);
        startFragment(fragment);

    }

    /**
     * Load the result overlay with the captured image
     *
     * @param image
     */
    private void loadResultOverlay(Bitmap image) {
        activateRoot(false);
        CaptureOverlayFragment fragment = CaptureOverlayFragment.newInstance_ImageInput(image);
        startFragment(fragment);
    }

    /**
     * Helper to start the fragment with a fade-in
     *
     * @param fragment
     */
    private void startFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                // TODO: Fade out
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .setCustomAnimations(R.anim.overlay_capture_fade_in, R.anim.overlay_capture_fade_out,
//                        R.anim.overlay_capture_fade_in, R.anim.overlay_capture_fade_out)
                .add(R.id.container_overlay_fragment, fragment,
                        fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Fades the root view in and out of the background
     *
     * @param activate
     */
    private void activateRoot(boolean activate) {
        mCameraActivated = activate;
        enableFrameProcessing(activate);

        float alpha = activate ? Constants.ALPHA_ACTIVATED : Constants.ALPHA_DEACTIVATED;
        View.OnClickListener listener = activate ? null : new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        };
        mRootView.animate().setDuration(Constants.DURATION_TRANSITION).alpha(alpha);
        mBtnCaptureImgonly.setEnabled(activate);
        mBtnCaptureImgrec.setEnabled(activate);
        mBtnCaptureBarcode.setEnabled(activate);
        mRootView.setOnClickListener(listener);

        int visibility = (activate && mCurrentInputType == InputType.IMG_REC ||
                mCurrentInputType == InputType.IMG_ONLY) ? View.VISIBLE : View.GONE;
        mBtnImgRec.setVisibility(visibility);

        if (activate) {
            startJitterCountdown();
        }
    }

    /**
     * Jitters are in place to prevent rapid consecutive scanning. The length of the jitter is set
     * by {@link CaptureActivity#SCAN_JITTER_SECS}. Once {@link CaptureActivity#mScanJitter} is
     * {@code false}, image scans can begin again
     */
    private void startJitterCountdown() {
        // Make sure the thread is killed before running it again
        // https://stackoverflow.com/questions/6186537/how-do-i-kill-an-android-thread-completely
        if (mJitterThread != null) {
            mJitterThread.interrupt();
            mJitterThread = null;
        }
        mJitterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SCAN_JITTER_SECS);
                } catch (InterruptedException e) {
                    Timber.e(e);
                    return;
                }
                mScanJitter = false;
            }
        });
        mJitterThread.start();
    }

    /**
     * Sets the current input type. This determines the callback that returns info of the scanned
     * item
     *
     * @param inputType
     */
    private void setInputType(InputType inputType) {
        if (mCurrentInputType != inputType) {
            // Only action if changing
            mCurrentInputType = inputType;
            fadeInOutInputTypes(inputType);
            switch (inputType) {
                case BARCODE:
                    mTvInstruction.setText(R.string.capture_mode_barcode_instruction);
                    mBtnImgRec.setVisibility(View.GONE);
                    break;
                case IMG_REC:
                    // TODO: Implement
                    mTvInstruction.setText("Still to be implemented. In the meantime, tapping " +
                            "anywhere shows labels for image on camera");
                    //mTvInstruction.setText(R.string.capture_mode_imgrec_instruction);
                    mBtnCaptureBarcode.animate().alpha(Constants.ALPHA_DEACTIVATED);
                    mBtnImgRec.setVisibility(View.VISIBLE);
                    break;
                case IMG_ONLY:
                    mTvInstruction.setText(R.string.capture_mode_imgonly_instruction);
                    mBtnImgRec.setVisibility(View.VISIBLE);
                    break;
            }
            if (allPermissionsGranted()) {
                startCameraSource();
                setupFrameProcessing(mCurrentInputType);
            } else {
                getRuntimePermissions();
            }
        }
    }

    /**
     * Fades out all capture buttons, but keeps the passed {@link InputType} activated
     *
     * @param inputType
     */
    private void fadeInOutInputTypes(InputType inputType) {
        mBtnCaptureBarcode.animate().alpha(Constants.ALPHA_DEACTIVATED);
        mBtnCaptureImgrec.animate().alpha(Constants.ALPHA_DEACTIVATED);
        mBtnCaptureImgonly.animate().alpha(Constants.ALPHA_DEACTIVATED);

        switch (inputType) {
            case BARCODE:
                mBtnCaptureBarcode.animate().alpha(Constants.ALPHA_ACTIVATED);
                break;
            case IMG_REC:
                mBtnCaptureImgrec.animate().alpha(Constants.ALPHA_ACTIVATED);
                break;
            case IMG_ONLY:
                mBtnCaptureImgonly.animate().alpha(Constants.ALPHA_ACTIVATED);
                break;
        }


    }

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

    /**
     * Assigns a frame processor to the camera source. Passing {@code null} or an invalid
     * {@code inputType} disables the frame processor.
     * <p>
     * To prevent crashes, make sure {@link CaptureActivity#startCameraSource()} had previously
     * been called
     *
     * @param inputType
     */
    private void setupFrameProcessing(InputType inputType) {
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
            case IMG_ONLY:
                // TODO: Null the frame processor, but still have a way to capture the image
            case IMG_REC:
                Timber.i("Using Image Label Detector Processor");
                mCameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor(mBtnImgRec, this));
                break;
            default:
                Timber.i("Passing in null processor");
                mCameraSource.setMachineLearningFrameProcessor(null);
        }
    }

    /**
     * Stop scans temporarily
     *
     * @param activate
     */
    private void enableFrameProcessing(boolean activate) {
        if (activate) {
            startCameraSource();
            setupFrameProcessing(mCurrentInputType);
        } else {
            // Re-enable frame processing in onResume(), after camera had been restarted
            setupFrameProcessing(InputType.NONE);
        }
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
            setupFrameProcessing(mCurrentInputType);
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