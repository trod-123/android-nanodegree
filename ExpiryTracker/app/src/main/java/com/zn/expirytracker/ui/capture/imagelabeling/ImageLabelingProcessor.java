// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.zn.expirytracker.ui.capture.imagelabeling;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.zn.expirytracker.ui.capture.helpers.FrameMetadata;
import com.zn.expirytracker.ui.capture.helpers.GraphicOverlay;
import com.zn.expirytracker.ui.capture.helpers.VisionProcessorBase;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Custom Image Classifier Demo.
 * <p>
 * Original code from: https://github.com/firebase/quickstart-android/tree/master/mlkit
 * Twaked just a bit for this app
 */
public class ImageLabelingProcessor extends VisionProcessorBase<List<FirebaseVisionLabel>> {

    private final FirebaseVisionLabelDetector detector;
    private OnImageRecognizedListener mListener;
    private Bitmap mBitmap;
    private View mButtonView;
    private List<FirebaseVisionLabel> mLabels;

    public ImageLabelingProcessor(View buttonView, OnImageRecognizedListener listener) {
        detector = FirebaseVision.getInstance().getVisionLabelDetector();
        mButtonView = buttonView;
        mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLabels != null) {
                    mListener.handleImage(mLabels, mBitmap);
                }
            }
        });
        mListener = listener;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Timber.e(e, "Exception thrown while trying to close Text Detector");
        }
    }

    @Override
    protected Task<List<FirebaseVisionLabel>> detectInImage(FirebaseVisionImage image) {
        mBitmap = image.getBitmapForDebugging();
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<FirebaseVisionLabel> labels,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        mLabels = labels;
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Timber.w(e, "Label detection failed.");
    }

    public interface OnImageRecognizedListener {
        void handleImage(@NonNull List<FirebaseVisionLabel> labels, Bitmap bitmap);
    }
}
