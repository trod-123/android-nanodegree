package com.zn.expirytracker.ui.capture;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
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
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
            "capture_overlay_fragment.input_type");
    public static final String ARG_BARCODE = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.barcode");
    public static final String ARG_BARCODE_BITMAP = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.barcode_bitmap");
    public static final String ARG_IMAGE_BITMAP = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.image_bitmap");

    private static final int REQ_CODE_SPEECH_INPUT_NAME = 1024;
    private static final int REQ_CODE_SPEECH_INPUT_EXPIRY_DATE = 1026;

    /**
     * Allows consecutive speech requests without blocking the mic. Needs to be at least 200
     */
    private static final int DELAY_CONSECUTIVE_SPEECH_REQEUSTS = 250;

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
    private String mName = "";
    private String mDescription = "";
    long mDateExpiry;
    private String mBrand = "";
    private String mSize = "";
    private String mWeight = "";
    private List<String> mImageUris = new ArrayList<>();

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
        }

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mCurrentDateStartOfDay = DataToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        mVoicePrompt = sp.getBoolean(getString(R.string.pref_capture_voice_input_key), true);
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

        // Fetch data
        switch (mInputType) {
            case BARCODE:
                fetchBarcodeData(mBarcode);
                break;
            case IMG_REC:
                fetchImageData(null);
                break;
            case IMG_ONLY:
                // For image only, there is no data to fetch, so just populate views
                populateFieldsFromImageOnly(mImageBitmap);
                break;
        }
        return rootView;
    }

    /**
     * Shows or hides the overlay data with a progress bar
     *
     * @param show
     */
    private void showOverlayData(boolean show) {
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
        Toolbox.showToast(mHostActivity, String.format("Saved %s!", mName));
        mHostActivity.onBackPressed();
    }

    /**
     * Saves the user-taken shots to internal storage, and appends their uri strings to food.
     * Note only either {@code mBarcodeBitmap} or {@code mImageBitmap} is not null.
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
     * Populates overlay with {@code upcItem} data gathered.
     * <p>
     * If {@code upcItem == null}, then prompt user for name and date
     *
     * @param upcItem
     */
    private void populateFieldsFromBarcode(@Nullable UpcItem upcItem) {
        mTvBarcode.setText(mBarcode);
        if (upcItem != null && upcItem.getItems().size() > 0) {
            Item item = upcItem.getItems().get(0);
            mName = item.getTitle();
            mTvName.setText(mName);
            mDescription = item.getDescription();
            if (mDescription.trim().isEmpty()) {
                mTvDescription.setVisibility(View.GONE);
            } else {
                mTvDescription.setText(mDescription);
            }
            mImageUris = item.getImages();
            String imageUri = "";
            if (mImageUris != null && mImageUris.size() > 0) {
                imageUri = mImageUris.get(0);
            }
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
                    return false;
                }
            });
            GlideApp.with(mHostActivity)
                    .load(mBarcodeBitmap)
                    .into(mIvBarcode);
            mTvAttr.setText(R.string.data_attribution_upcitemdb);

            mBrand = item.getBrand();
            mSize = item.getSize();
            mWeight = item.getWeight();
        } else {
            // upcItem is null or has no results
            mTvDescription.setVisibility(View.GONE);
            // Load the barcode in the main image instead and hide the other
            Toolbox.showView(mPbImage, true, false);
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
            mIvBarcode.setVisibility(View.GONE);
            mTvAttr.setVisibility(View.GONE);
        }
        promptForMissingInfo();
    }

    /**
     * Populates overlay with the just the image, and hides unnecessary views
     *
     * @param image
     */
    private void populateFieldsFromImageOnly(@NonNull Bitmap image) {
        mTvDescription.setVisibility(View.GONE);
        // Load the barcode in the main image instead and hide the other
        Toolbox.showView(mPbImage, true, false);
        GlideApp.with(mHostActivity)
                .load(image)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Timber.e(e, "Error loading barcode bitmap into overlay / image only");
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
        mTvBarcode.setVisibility(View.GONE);
        mIvBarcode.setVisibility(View.GONE);
        mTvAttr.setVisibility(View.GONE);

        promptForMissingInfo();
    }

    /**
     * Prompt the user for additional required info. Use voice input if enabled in Settings
     */
    private void promptForMissingInfo() {
        if (mName != null && mName.isEmpty()) {
            // If prompting name, name should be first. Will prompt for Expiry date after name is
            // provided
            promptItemName(mVoicePrompt, false);
        } else {
            promptExpiryDate(mVoicePrompt, false, false);
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
            populateFieldsFromBarcode(upcItem.first);
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
    private void handleResponseCode(Pair<UpcItem, UpcItemDbService.ResponseCode> upcItem) {
        String errorToast = null;
        switch (upcItem.second) {
            case OK:
                // Even with a good response, the items may be empty
                if (upcItem.first.getItems().size() == 0) {
                    errorToast = "Item not found in database";
                }
                break;
            case NO_INTERNET:
                errorToast = "Internet connection lost while getting data. Please try again later";
                break;
            case NOT_FOUND:
            case INVALID_QUERY:
                errorToast = "Item not found in database";
                break;
            case EXCEED_LIMIT:
                errorToast = "API calls limit has been reached. Please contact the developer";
                break;
            case SERVER_ERR:
                errorToast = "Server error. Please try again later";
                break;
            case OTHER:
            default:
                errorToast = "Unknown error";
                break;
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
                }, DELAY_CONSECUTIVE_SPEECH_REQEUSTS);
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
                showOverlayData(true);
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
                }, DELAY_CONSECUTIVE_SPEECH_REQEUSTS);
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

    private DateTime parseDateFromVoiceInput(String dateString) {
        // https://stackoverflow.com/questions/9945072/convert-string-to-date-in-java
        // TODO: Get more formats...
        SimpleDateFormat[] dateFormats = new SimpleDateFormat[4];
        dateFormats[0] = new SimpleDateFormat("MMMM dd yyyy");
        dateFormats[1] = new SimpleDateFormat("MMMM dd");
        dateFormats[2] = new SimpleDateFormat("MMM dd");
        dateFormats[3] = new SimpleDateFormat("MMMM dd");

        Date date;
        // https://stackoverflow.com/questions/13239972/how-do-you-implement-a-re-try-catch
        int count = 0;
        while (true) {
            try {
                // https://stackoverflow.com/questions/28514346/parsing-a-date-s-ordinal-indicator-st-nd-rd-th-in-a-date-time-string/28514476
                date = dateFormats[count].parse(dateString
                        .replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                if (count != 0)
                    date.setYear((new Date()).getYear()); // set current year if not provided
                return new DateTime(date.getTime());
            } catch (ParseException e) {
                if (++count == dateFormats.length) {
                    Timber.e(e, "There was an error parsing the date");
                    break;
                }
            }
        }
        // Return current date if error
        Toolbox.showToast(mHostActivity, "There was an error parsing the date");
        return new DateTime(mCurrentDateStartOfDay);
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
        mTvExpiryDate.setText(getString(R.string.expiry_msg_generic,
                DataToolbox.getFormattedFullDateString(date)));
        mDateExpiry = date;
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
                    String dateString = result.get(0);
                    if (isCancelled(dateString)) {
                        mHostActivity.onBackPressed();
                        return;
                    }
                    DateTime date = parseDateFromVoiceInput(dateString);
                    if (DataToolbox.compareTwoDates(date.getMillis(), mCurrentDateStartOfDay)) {
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
