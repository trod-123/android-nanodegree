package com.zn.expirytracker.ui.capture;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.expirytracker.GlideApp;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.data.upcitemdb.UpcItemDbService;
import com.zn.expirytracker.data.upcitemdb.model.Item;
import com.zn.expirytracker.data.upcitemdb.model.UpcItem;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialogFragment;
import com.zn.expirytracker.ui.dialog.OnDialogCancelListener;
import com.zn.expirytracker.ui.dialog.TextInputDialogFragment;
import com.zn.expirytracker.utils.Constants;
import com.zn.expirytracker.utils.DateToolbox;
import com.zn.expirytracker.utils.DebugFields;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CaptureOverlayFragment extends Fragment
        implements TextInputDialogFragment.OnTextConfirmedListener, OnDialogCancelListener,
        ExpiryDatePickerDialogFragment.OnDateSelectedListener {

    private static final boolean DEBUG_MODE_NO_API_CALL = false;

    public static final String ARG_INPUT_TYPE = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "input_type");
    public static final String ARG_BARCODE = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "barcode");
    public static final String ARG_BARCODE_BITMAP = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "barcode_bitmap");
    public static final String ARG_IMAGE_BITMAP = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "image_bitmap");

    private static final int REQ_CODE_SPEECH_INPUT_NAME = 1024;
    private static final int REQ_CODE_SPEECH_INPUT_EXPIRY_DATE = 1026;

    private static final int DEFAULT_MAX_IMAGES = 5;

    @BindView(R.id.layout_overlay_capture_root)
    View mRootView;
    @BindView(R.id.pb_overlay_scanned)
    ProgressBar mPb;
    @BindView(R.id.btn_overlay_scanned_positive)
    Button mBtnPositive;
    @BindView(R.id.btn_overlay_scanned_negative)
    Button mBtnNegative;
    @BindView(R.id.tv_overlay_capture_barcode)
    TextView mTvBarcode;
    @BindView(R.id.tv_overlay_scanned_name)
    TextView mTvName;
    @BindView(R.id.tv_overlay_scanned_description)
    TextView mTvDescription;
    @BindView(R.id.tv_overlay_scanned_expiry_date)
    TextView mTvExpiryDate;
    @BindView(R.id.tv_overlay_capture_attr)
    TextView mTvAttr;
    @BindView(R.id.iv_overlay_scanned_image)
    ImageView mIvImage;
    @BindView(R.id.pb_overlay_scanned_image)
    ProgressBar mPbImage;
    @BindView(R.id.iv_overlay_scanned_barcode)
    ImageView mIvBarcode;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private long mCurrentDateStartOfDay;
    private boolean mVoicePrompt;

    private InputType mInputType = InputType.NONE;
    private String mBarcode = "";
    private Bitmap mBarcodeBitmap;
    private Bitmap mImageBitmap;
    private String mBarcodeBitmapPath;
    private String mImageBitmapPath;
    private String mName = "";
    private String mDescription = "";
    long mDateExpiry;
    private String mBrand = "";
    private String mSize = "";
    private String mWeight = "";
    private List<String> mImageUris = new ArrayList<>(); // stores image uris from barcode data

    private boolean mDateSet = false; // if the date has been set, don't prompt again

    // Only store values that weren't generated from CaptureActivity (those values are retained
    // in Fragment arguments

    /**
     * Restore not from Fragment args to prevent crash when bringing app to background
     */
    private static final String KEY_BARCODE_BITMAP_URI = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "barcode_bitmap");

    /**
     * Restore not from fragment args to prevent crash when bringing app to background
     */
    private static final String KEY_IMAGE_BITMAP_URI = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "image_bitmap");

    private static final String KEY_FOOD_NAME_STRING = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "food_name");

    private static final String KEY_EXPIRY_DATE_LONG = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "expiry_date");

    private static final String KEY_DESCRIPTION_STRING = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "description");

    private static final String KEY_BRAND_STRING = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "brand");

    private static final String KEY_SIZE_STRING = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "size");

    private static final String KEY_WEIGHT_STRING = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "weight");

    private static final String KEY_IMAGE_URIS = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "image_uris");

    private static final String KEY_EXPIRY_DATE_SET_BOOLEAN = Toolbox.createStaticKeyString(
            CaptureOverlayFragment.class, "date_set");

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Store current food object values
        if (mBarcodeBitmap != null) {
            try {
                String path = Toolbox.saveBitmapToInternalStorage(
                        mBarcodeBitmap, Constants.DEFAULT_FILENAME, mHostActivity);
                outState.putString(KEY_BARCODE_BITMAP_URI, path);
            } catch (IOException e) {
                Timber.e(e, "There was an error saving the barcode bitmap into outState. Not saving...");
            }
        } else {
            outState.putString(KEY_BARCODE_BITMAP_URI, mBarcodeBitmapPath);
        }
        if (mImageBitmap != null) {
            try {
                String path = Toolbox.saveBitmapToInternalStorage(
                        mImageBitmap, Constants.DEFAULT_FILENAME, mHostActivity);
                outState.putString(KEY_IMAGE_BITMAP_URI, path);
            } catch (IOException e) {
                Timber.e(e, "There was an error saving the image bitmap into outState. Not saving...");
            }
        } else {
            outState.putString(KEY_IMAGE_BITMAP_URI, mImageBitmapPath);
        }
        outState.putString(KEY_FOOD_NAME_STRING, mName);
        outState.putLong(KEY_EXPIRY_DATE_LONG, mDateExpiry);
        outState.putString(KEY_DESCRIPTION_STRING, mDescription);
        outState.putString(KEY_BRAND_STRING, mBrand);
        outState.putString(KEY_SIZE_STRING, mSize);
        outState.putString(KEY_WEIGHT_STRING, mWeight);
        outState.putStringArrayList(KEY_IMAGE_URIS, new ArrayList<>(mImageUris));
        outState.putBoolean(KEY_EXPIRY_DATE_SET_BOOLEAN, mDateSet);
        super.onSaveInstanceState(outState);
    }

    public CaptureOverlayFragment() {
        // Required empty public constructor
    }

    /**
     * Specifically for Barcode scans
     *
     * @param barcode
     * @param barcodeImage
     * @return
     */
    public static CaptureOverlayFragment newInstance_BarcodeInput(@Nullable String barcode,
                                                                  @Nullable Bitmap barcodeImage) {
        CaptureOverlayFragment fragment = new CaptureOverlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INPUT_TYPE, InputType.BARCODE);
        args.putString(ARG_BARCODE, barcode);
        args.putParcelable(ARG_BARCODE_BITMAP, barcodeImage);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Specifically for Image only captures
     *
     * @param image
     * @return
     */
    public static CaptureOverlayFragment newInstance_ImageInput(Bitmap image) {
        CaptureOverlayFragment fragment = new CaptureOverlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INPUT_TYPE, InputType.IMG_ONLY);
        args.putParcelable(ARG_IMAGE_BITMAP, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mInputType = (InputType) args.getSerializable(ARG_INPUT_TYPE);
            mBarcode = args.getString(ARG_BARCODE, "");
            mBarcodeBitmap = args.getParcelable(ARG_BARCODE_BITMAP);
            mImageBitmap = args.getParcelable(ARG_IMAGE_BITMAP);
            // remove bitmaps from args to prevent java.lang.RuntimeException:
            // android.os.TransactionTooLargeException when bringing app to background
            // (so we save these as uris in onSaveInstanceState)
            args.remove(ARG_BARCODE_BITMAP);
            args.remove(ARG_IMAGE_BITMAP);
            setArguments(args);
        }

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mCurrentDateStartOfDay = DateToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        mVoicePrompt = sp.getBoolean(getString(R.string.pref_capture_voice_input_key),
                mHostActivity.getResources().getBoolean(R.bool.pref_capture_voice_input_default));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_overlay_capture, container, false);
        Timber.tag(CaptureOverlayFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        // Set the buttons
        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
        mBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHostActivity.onBackPressed();
            }
        });

        showOverlayData(false);

        if (savedInstanceState != null) {
            mBarcodeBitmapPath = savedInstanceState.getString(KEY_BARCODE_BITMAP_URI);
            mImageBitmapPath = savedInstanceState.getString(KEY_IMAGE_BITMAP_URI);
            mName = savedInstanceState.getString(KEY_FOOD_NAME_STRING, "");
            mDateExpiry = savedInstanceState.getLong(KEY_EXPIRY_DATE_LONG, mCurrentDateStartOfDay);
            mDescription = savedInstanceState.getString(KEY_DESCRIPTION_STRING, "");
            mBrand = savedInstanceState.getString(KEY_BRAND_STRING, "");
            mSize = savedInstanceState.getString(KEY_SIZE_STRING, "");
            mWeight = savedInstanceState.getString(KEY_WEIGHT_STRING, "");
            mImageUris = savedInstanceState.getStringArrayList(KEY_IMAGE_URIS);
            mDateSet = savedInstanceState.getBoolean(KEY_EXPIRY_DATE_SET_BOOLEAN, false);

            populateFields(mName, mDateExpiry, mDescription, mImageUris);

        } else {
            // Fetch data, only if this is our first time
            switch (mInputType) {
                case BARCODE:
                    fetchBarcodeData(mBarcode);
                    break;
                case IMG_REC:
                    fetchImageData(null);
                    break;
                case IMG_ONLY:
                    // For image only, there is no data to fetch, so just populate views
                    populateFieldsFromImageOnly(mImageBitmap, mImageBitmapPath);
                    break;
            }
        }

        // Dynamically set the overlay size as a proportion of the screen width
        // https://github.com/codepath/android_guides/wiki/Using-DialogFragment
        Display display = mHostActivity.getWindow().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        float multiplier = Toolbox.getFloatFromResources(
                getResources(), R.dimen.capture_overlay_width_multiplier);

        CardView.LayoutParams params = new CardView.LayoutParams((int) (point.x * multiplier),
                CardView.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        rootView.setLayoutParams(params);
        return rootView;
    }

    /**
     * Shows or hides the overlay data with a progress bar. Also disables screen rotations while
     * overlay is hidden. Screen rotations are restored when overlay regains visibility
     *
     * @param show
     */
    private void showOverlayData(boolean show) {
        if (show) {
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
        Toolbox.showView(mRootView, show, false);
        Toolbox.showView(mPb, !show, false);
    }

    /**
     * Saves the item into the database
     */
    private void saveItem() {
        // First save the images into the food item
        saveBitmapsToFood();

        Food food = createFood(mName, mDescription, mDateExpiry, mBrand, mSize, mWeight,
                mImageUris, mBarcode, mInputType, Storage.NOT_SET);
        mViewModel.insert(true, food);
        Toolbox.showToast(mHostActivity, getString(R.string.message_item_saved, mName));
        mHostActivity.onBackPressed();
    }

    /**
     * Saves the user-taken shots to internal storage, and appends their uri strings to food.
     * Note only either {@code mBarcodeBitmap} or {@code mImageBitmap} is not null.
     * <p>
     * If we're restored from savedInstanceState, then save the bitmap paths instead of the images
     */
    private void saveBitmapsToFood() {
        if (mBarcodeBitmap != null) {
            try {
                String path = Toolbox.saveBitmapToInternalStorage(
                        mBarcodeBitmap, Constants.DEFAULT_FILENAME, mHostActivity);
                Timber.d("Saved barcode bitmap path: %s", path);
                mImageUris.add(path);
            } catch (IOException e) {
                Timber.e(e, "There was a problem saving the barcode bitmap to internal storage");
            }
        } else if (mBarcodeBitmapPath != null) {
            mImageUris.add(mBarcodeBitmapPath);
        }
        if (mImageBitmap != null) {
            try {
                String path = Toolbox.saveBitmapToInternalStorage(
                        mImageBitmap, Constants.DEFAULT_FILENAME, mHostActivity);
                Timber.d("Saved image bitmap path: %s", path);
                mImageUris.add(path);
            } catch (IOException e) {
                Timber.e(e, "There was a problem saving the image bitmap to internal storage");
            }
        } else if (mImageBitmapPath != null) {
            mImageUris.add(mImageBitmapPath);
        }
    }

    /**
     * Gets barcode data online
     *
     * @param barcode
     */
    private void fetchBarcodeData(String barcode) {
        new GetProductsAsyncTask().execute(barcode);
    }

    private void fetchImageData(Bitmap image) {
        // TODO: Implement
    }

    /**
     * Exclusively for saving down barcode data into fields
     *
     * @param upcItem
     */
    private void setFieldsFromBarcode(@Nullable UpcItem upcItem) {
        if (upcItem != null && upcItem.getItems().size() > 0) {
            Item item = upcItem.getItems().get(0);
            mName = item.getTitle();
            mDescription = item.getDescription();
            mImageUris = item.getImages();
            mBrand = item.getBrand();
            mSize = item.getSize();
            mWeight = item.getWeight();
        }
    }

    /**
     * Populates overlay with {@code upcItem} data gathered.
     * <p>
     * Prompt user for name and date if missing
     *
     * @param name
     * @param dateExpiry
     * @param description
     * @param imageUris
     */
    private void populateFields(String name, long dateExpiry, String description,
                                List<String> imageUris) {
        mTvBarcode.setText(mBarcode);
        mTvName.setText(name);
        if (mDescription.trim().isEmpty()) {
            mTvDescription.setVisibility(View.GONE);
        } else {
            mTvDescription.setText(description);
        }
        mTvExpiryDate.setText(getString(R.string.expiry_msg_generic,
                DateToolbox.getFormattedFullDateString(dateExpiry)));
        if (mBarcodeBitmap != null || mBarcodeBitmapPath != null) {
            // TODO: Clean up code here
            if (mImageUris != null && mImageUris.size() > 0) {
                // Set the main image to the first image from the list
                String imageUri = imageUris.get(0);

                Toolbox.showView(mPbImage, true, false);
                Toolbox.loadImageFromUrl(mHostActivity, imageUri, mIvImage, new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        // If there is no image, hide the view
                        mIvImage.setVisibility(View.GONE);
                        Toolbox.showView(mPbImage, false, false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        Toolbox.showView(mPbImage, false, false);
                        mIvImage.setContentDescription(mName);
                        return false;
                    }
                });

                // Set the barcode bitmap with either the bitmap itself, or the bitmap path
                if (mBarcodeBitmap != null) {
                    GlideApp.with(mHostActivity)
                            .load(mBarcodeBitmap)
                            .into(mIvBarcode);
                } else {
                    // Barcode bitmap is null, but we have the bitmap uri from outState
                    Toolbox.loadImageFromUrl(mHostActivity, mBarcodeBitmapPath, mIvBarcode, new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    });
                }
                mTvAttr.setText(R.string.data_attribution_upcitemdb);
            } else {
                // upcItem is null, so load the barcode in the main image instead and hide the other
                if (mBarcodeBitmapPath == null) {
                    GlideApp.with(mHostActivity)
                            .load(mBarcodeBitmap)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                            Target<Drawable> target, boolean isFirstResource) {
                                    Timber.e(e, "Error loading barcode bitmap into overlay / barcode");
                                    Toolbox.showView(mPbImage, false, false);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target, DataSource dataSource,
                                                               boolean isFirstResource) {
                                    Toolbox.showView(mPbImage, false, false);
                                    return false;
                                }
                            })
                            .into(mIvImage);
                } else {
                    Toolbox.loadImageFromUrl(mHostActivity, mBarcodeBitmapPath, mIvImage, new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Bitmap> target, boolean isFirstResource) {
                            Timber.e(e, "Error loading barcode bitmap into overlay / barcode uri");
                            Toolbox.showView(mPbImage, false, false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            Toolbox.showView(mPbImage, false, false);
                            return false;
                        }
                    });
                }
                mIvBarcode.setVisibility(View.GONE);
                mTvAttr.setVisibility(View.GONE);
            }
        } else {
            // Barcode bitmap is null, so we only have the image bitmap
            populateFieldsFromImageOnly(mImageBitmap, mImageBitmapPath);
        }
        promptForMissingInfo();
    }

    /**
     * Populates overlay with the just the image, and hides unnecessary views
     *
     * @param image
     */
    private void populateFieldsFromImageOnly(@Nullable Bitmap image, @Nullable String imagePath) {
        mTvDescription.setVisibility(View.GONE);
        // Load the barcode in the main image instead and hide the other
        Toolbox.showView(mPbImage, true, false);
        if (image != null) {
            GlideApp.with(mHostActivity)
                    .load(image)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Timber.e(e, "Error loading image bitmap into overlay / image only");
                            Toolbox.showView(mPbImage, false, false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            Toolbox.showView(mPbImage, false, false);
                            return false;
                        }
                    })
                    .into(mIvImage);
        } else if (imagePath != null) {
            Toolbox.loadImageFromUrl(mHostActivity, imagePath, mIvImage, new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Bitmap> target, boolean isFirstResource) {
                    Timber.e(e, "Error loading image bitmap into overlay / image only path");
                    Toolbox.showView(mPbImage, false, false);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                               DataSource dataSource, boolean isFirstResource) {
                    Toolbox.showView(mPbImage, false, false);
                    return false;
                }
            });
        }
        mTvBarcode.setVisibility(View.GONE);
        mIvBarcode.setVisibility(View.GONE);
        mTvAttr.setVisibility(View.GONE);

        promptForMissingInfo();
    }

    /**
     * Prompt the user for additional required info. Use voice input if enabled in Settings.
     * If no additional info is needed, then just show the view
     */
    private void promptForMissingInfo() {
        if (mName != null && mName.isEmpty()) {
            // If prompting name, name should be first. Will prompt for Expiry date after name is
            // provided
            promptItemName(mVoicePrompt, false);
        } else if (!mDateSet) {
            promptExpiryDate(mVoicePrompt, false, false);
        } else {
            // All fields are already set, so show the view
            showOverlayData(true);
        }
    }

    /**
     * Prompts the user for voice input. Input is handled in
     * {@link CaptureOverlayFragment#onActivityResult(int, int, Intent)} with the provided
     * {@code speechRequestCode}
     *
     * @param speechRequestCode
     * @param reprompt          {@code true} will append reprompt message
     */
    private void startVoiceInput(int speechRequestCode, boolean reprompt) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        String prompt;
        switch (speechRequestCode) {
            case REQ_CODE_SPEECH_INPUT_EXPIRY_DATE:
                prompt = getString(R.string.voice_prompt_expiry);
                if (reprompt) {
                    prompt += String.format(" %s", getString(R.string.edit_error_date_expiry));
                }
                break;
            case REQ_CODE_SPEECH_INPUT_NAME:
                prompt = getString(R.string.voice_prompt_name);
                break;
            default:
                Timber.d("In startVoiceInput(). Invalid speechRequestCode passed: %s. Returning...",
                        speechRequestCode);
                return;
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        try {
            startActivityForResult(intent, speechRequestCode);
        } catch (ActivityNotFoundException a) {
            Timber.e(a, "There was an issue starting the speech request");
        }
    }

    /**
     * Dismiss the overlay if the user dismisses a dialog prompt
     *
     * @param klass
     * @param dialogInterface
     */
    @Override
    public void onCancelled(Class klass, DialogInterface dialogInterface) {
        mHostActivity.onBackPressed();
    }

    /**
     * Fetches info about the provided barcode from the upcitemdb
     */
    private class GetProductsAsyncTask extends
            AsyncTask<String, Void, Pair<UpcItem, UpcItemDbService.ResponseCode>> {
        UpcItemDbService service;

        @Override
        protected void onPostExecute(@Nullable Pair<UpcItem, UpcItemDbService.ResponseCode> upcItem) {
            if (upcItem != null) {
                setFieldsFromBarcode(upcItem.first);
            }
            populateFields(mName, mDateExpiry, mDescription, mImageUris);
            handleResponseCode(upcItem);
        }

        @Override
        protected void onPreExecute() {
            service = new UpcItemDbService(mHostActivity);
        }

        // https://api.upcitemdb.com/prod/trial/search?s=google%20pixel%202&match_mode=0&type=product
        @Override
        protected Pair<UpcItem, UpcItemDbService.ResponseCode> doInBackground(String... barcodes) {
            if (DEBUG_MODE_NO_API_CALL) {
                // empty upcitem simulates no response
                return new Pair<>(new UpcItem(), UpcItemDbService.ResponseCode.OTHER);
            } else {
                try {
                    return service.fetchUpcItemInfo(barcodes[0]);
                } catch (IOException e) {
                    Timber.e(e, "There was an error fetching the upcitemdb result");
                }
                return null;
            }
        }
    }

    /**
     * Display a toast to the user pertaining to the UpcItemDb response code
     *
     * @param upcItem
     */
    private void handleResponseCode(@Nullable Pair<UpcItem, UpcItemDbService.ResponseCode> upcItem) {
        String errorToast = null;
        if (upcItem != null) {
            switch (upcItem.second) {
                case OK:
                    // Even with a good response, the items may be empty
                    if (upcItem.first.getItems().size() == 0) {
                        errorToast = getString(R.string.upcitemdb_error_item_not_found);
                    }
                    break;
                case NO_INTERNET:
                    errorToast = getString(R.string.upcitemdb_error_no_internet);
                    break;
                case NOT_FOUND:
                case INVALID_QUERY:
                    errorToast = getString(R.string.upcitemdb_error_item_not_found);
                    break;
                case EXCEED_LIMIT:
                    errorToast = getString(R.string.upcitemdb_error_api_limit);
                    break;
                case SERVER_ERR:
                    errorToast = getString(R.string.upcitemdb_error_server);
                    break;
                case OTHER:
                default:
                    errorToast = getString(R.string.upcitemdb_error_unknown);
                    break;
            }
        } else {
            // A null item likely means error accessing api server
            errorToast = getString(R.string.upcitemdb_error_host_resolution);
        }
        if (errorToast != null) {
            Toolbox.showToast(mHostActivity, errorToast);
        }
    }

    // region Get item name

    /**
     * Ask the user for the scanned item's name
     *
     * @param voiceInput
     */
    private void promptItemName(boolean voiceInput, boolean delay) {
        if (voiceInput) {
            if (delay) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startVoiceInput(REQ_CODE_SPEECH_INPUT_NAME, false);
                    }
                }, Constants.DELAY_CONSECUTIVE_SPEECH_REQEUSTS);
            } else {
                startVoiceInput(REQ_CODE_SPEECH_INPUT_NAME, false);
            }
        } else {
            showInputTextDialog();
        }
    }

    /**
     * Prompts the user to type in the item's name
     */
    private void showInputTextDialog() {
        TextInputDialogFragment dialog = TextInputDialogFragment.newInstance(getString(R.string.voice_prompt_name));
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), TextInputDialogFragment.class.getSimpleName());
    }

    @Override
    public void onTextConfirmed(int position, String textInput) {
        switch (position) {
            case Dialog.BUTTON_POSITIVE:
                setItemName(textInput, true);
                break;
            default:
                mHostActivity.onBackPressed();
                break;
        }
    }

    /**
     * Sets the name textview. Convenience method for handling interfaced requests. Chain the next
     * request through {@code promptExpiryDate}
     *
     * @param name
     */
    private void setItemName(String name, boolean promptExpiryDate) {
        mTvName.setText(name);
        mName = name;
        mIvImage.setContentDescription(mName);

        // Expiry date is the next step, which is required. Putting the call here to ensure it is
        // not forgotten since UI shows only after expiry date is provided
        if (promptExpiryDate) {
            promptExpiryDate(mVoicePrompt, true, false);
        }
    }

    // endregion

    // region Get expiry date

    /**
     * Ask the user for the scanned item's expiry date
     *
     * @param voiceInput
     * @param reprompt   {@code true} will append reprompt message
     */
    private void promptExpiryDate(boolean voiceInput, boolean delay, final boolean reprompt) {
        if (voiceInput) {
            if (delay) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startVoiceInput(REQ_CODE_SPEECH_INPUT_EXPIRY_DATE, reprompt);
                    }
                }, Constants.DELAY_CONSECUTIVE_SPEECH_REQEUSTS);
            } else {
                startVoiceInput(REQ_CODE_SPEECH_INPUT_EXPIRY_DATE, reprompt);
            }
        } else {
            showDatePickerDialog();
        }
    }

    /**
     * Prompts the user to select a date, with the user's current date as default date
     */
    private void showDatePickerDialog() {
        ExpiryDatePickerDialogFragment dialog = ExpiryDatePickerDialogFragment.newInstance(
                ExpiryDatePickerDialogFragment.DateType.EXPIRY, mCurrentDateStartOfDay,
                mCurrentDateStartOfDay);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ExpiryDatePickerDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDateSelected(ExpiryDatePickerDialogFragment.DateType dateType, DateTime selectedDate) {
        setExpiryDate(selectedDate.getMillis());
        showOverlayData(true);
    }

    /**
     * Sets the expiry date textview. Convenience method for handling interfaced requests
     *
     * @param date
     */
    private void setExpiryDate(long date) {
        int res = date >= new DateTime().withTimeAtStartOfDay().getMillis() ?
                R.string.expiry_msg_generic : R.string.expiry_msg_past_generic;
        mTvExpiryDate.setText(getString(res, DateToolbox.getFormattedFullDateString(date)));
        mDateExpiry = date;
        mDateSet = true;
    }

    // endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT_EXPIRY_DATE:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spokenDateString = result.get(0);
                    if (isCancelled(spokenDateString)) {
                        mHostActivity.onBackPressed();
                        return;
                    }
                    DateTime date = DateToolbox.parseDateFromString(
                            spokenDateString, mHostActivity, mCurrentDateStartOfDay);
                    if (DebugFields.OVERRIDE_EXPIRY_DATE_RULES ||
                            DateToolbox.compareTwoDates(date.getMillis(), mCurrentDateStartOfDay)) {
                        setExpiryDate(date.getMillis());
                    } else {
                        // reprompt
                        promptExpiryDate(mVoicePrompt, true, true);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mHostActivity.onBackPressed();
                    return;
                }
                showOverlayData(true); // date is the last thing to load before showing data
                break;
            case REQ_CODE_SPEECH_INPUT_NAME:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String name = result.get(0);
                    if (isCancelled(name)) {
                        mHostActivity.onBackPressed();
                        return;
                    }
                    setItemName(Toolbox.toTitleCase(name), true);
                } else if (resultCode == RESULT_CANCELED) {
                    mHostActivity.onBackPressed();
                    return;
                }
                break;
        }
    }

    /**
     * Checks if the user has cancelled voice prompt
     *
     * @param string
     * @return
     */
    private boolean isCancelled(String string) {
        String[] cancelInputs = mHostActivity.getResources().getStringArray(R.array.voice_input_cancelled);
        return Arrays.asList(cancelInputs).contains(string);
    }

    /**
     * Helper that creates food object
     *
     * @param name
     * @param description
     * @param dateExpiry
     * @param brand
     * @param size
     * @param weight
     * @param imageUris
     * @param barcode
     * @param inputType
     * @param loc
     * @return
     */
    private Food createFood(String name, String description, long dateExpiry, String brand,
                            String size, String weight, List<String> imageUris, String barcode,
                            InputType inputType, Storage loc) {
        return new Food(name, dateExpiry, dateExpiry, 1, loc, description, brand, size,
                weight, "", barcode, inputType, imageUris);
    }
}
