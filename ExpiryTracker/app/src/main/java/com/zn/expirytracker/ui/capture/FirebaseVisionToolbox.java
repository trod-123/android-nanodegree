package com.zn.expirytracker.ui.capture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseIntArray;
import android.view.Surface;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

import timber.log.Timber;

/**
 * Set of {@link FirebaseVision} helper methods
 */
public class FirebaseVisionToolbox {

    private static final float DEFAULT_IMGREC_CONFIDENCE_THRESHOLD = 0.8f;

    /**
     * Creates a {@link FirebaseVisionImage} object from an {@link Image}, then, passes the
     * {@link FirebaseVisionImage} object to the
     * {@link com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector#detectInImage(FirebaseVisionImage)}
     * <p>
     * https://firebase.google.com/docs/ml-kit/android/read-barcodes
     *
     * @param firebaseImage
     */
    private static void runBarcodeDetector(FirebaseVisionImage firebaseImage,
                                           OnSuccessListener<List<FirebaseVisionBarcode>> onSuccessListener,
                                           OnFailureListener onFailureListener) {
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(firebaseImage)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Returns 10 labels corresponding to the passed {@code firebaseImage}
     * <p>
     * https://firebase.google.com/docs/ml-kit/android/label-images
     *
     * @param firebaseImage
     */
    private static void runImageRecognizer_OnDevice(FirebaseVisionImage firebaseImage,
                                                    OnSuccessListener<List<FirebaseVisionLabel>> onSuccessListener,
                                                    OnFailureListener onFailurelistener) {
        FirebaseVisionLabelDetectorOptions options = new FirebaseVisionLabelDetectorOptions.Builder()
                .setConfidenceThreshold(DEFAULT_IMGREC_CONFIDENCE_THRESHOLD)
                .build();
        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options);
        Task<List<FirebaseVisionLabel>> result = detector.detectInImage(firebaseImage)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailurelistener);
    }


    /**
     * Returns 10 labels corresponding to the passed {@code firebaseImage}
     * <p>
     * https://firebase.google.com/docs/ml-kit/android/label-images
     *
     * @param firebaseImage
     */
    private void runImageRecognizer_OnCloud(FirebaseVisionImage firebaseImage,
                                            OnSuccessListener<List<FirebaseVisionCloudLabel>> onSuccessListener,
                                            OnFailureListener onFailureListener) {
        FirebaseVisionCloudDetectorOptions options = new FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .build();
        FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLabelDetector(options);
        Task<List<FirebaseVisionCloudLabel>> result = detector.detectInImage(firebaseImage)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Generates a {@link FirebaseVisionImage} from a provided {@code bitmap}
     * <p>
     * Source code from MLKit Documentation: https://firebase.google.com/docs/ml-kit/
     *
     * @param bitmap
     * @return
     */
    private static FirebaseVisionImage generateFirebaseVisionImage(Bitmap bitmap) {
        return FirebaseVisionImage.fromBitmap(bitmap);
    }

    /**
     * Generates a {@link FirebaseVisionImage} from a provided {@code image}. Returns {@code null}
     * if error
     * <p>
     * Source code from MLKit Documentation: https://firebase.google.com/docs/ml-kit/
     *
     * @param image
     * @return
     */
    private static FirebaseVisionImage generateFirebaseVisionImage(Image image, String cameraId,
                                                                   Activity activity, Context context) {
        try {
            int rotation = getRotationCompensation(cameraId, activity, context);
            // Determine the angle image must be rotated to compensate for both device's rotation and
            // orientation of device's camera sensor
            return FirebaseVisionImage.fromMediaImage(image, rotation);
        } catch (CameraAccessException e) {
            Timber.e(e, "There was an error getting the FirebaseVisionImage");
            return null;
        }
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation. This is needed for images coming from the camera
     * <p>
     * Source code from MLKit Documentation: https://firebase.google.com/docs/ml-kit/
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Timber.e("Bad rotation value: %s", rotationCompensation);
        }
        return result;
    }
}
