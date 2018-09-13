package com.zn.expirytracker.ui.capture;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialog;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CaptureOverlayFragment extends Fragment
        implements ExpiryDatePickerDialog.OnDateSelectedListener {

    public static final String ARG_INPUT_TYPE = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.input_type");
    public static final String ARG_BARCODE = Toolbox.createStaticKeyString(
            "capture_overlay_fragment.barcode");

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    public static final String UPCITEMDB_BASE_SEARCH_URL = "https://api.upcitemdb.com/prod/trial/search?s=";
    public static final String UPCITEMDB_BASE_UPC_SEARCH_URL =
            "https://api.upcitemdb.com/prod/trial/lookup?upc=";


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
    @BindView(R.id.iv_overlay_scanned_barcode)
    ImageView mIvBarcode;
    @BindView(R.id.iv_overlay_scanned_storage_loc)
    ImageView mIvLoc;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private long mCurrentDateStartOfDay;

    private InputType mInputType;
    private String mBarcode;
    private String mName;
    private String mDescription;
    private String mImageUri;
    private String mBarcodeUri;
    long mDateExpiry;

    public CaptureOverlayFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     *
     * @return
     */
    public static CaptureOverlayFragment newInstance(InputType inputType, @Nullable String barcode) {
        CaptureOverlayFragment fragment = new CaptureOverlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INPUT_TYPE, inputType);
        args.putString(ARG_BARCODE, barcode);
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
        }

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mCurrentDateStartOfDay = DataToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
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
        }

        return rootView;
    }

    private void saveItem() {
        // TODO: Implement
        Toolbox.showToast(mHostActivity, "This will save the item!");
        mHostActivity.onBackPressed();
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

    }

    /**
     * Populates overlay with {@code result} data gathered from the appropriate database provided by
     * {@code inputType}
     *
     * @param inputType
     * @param result
     */
    private void fillInFieldsFromOnline(InputType inputType, String result) {
        String attr;

        Toolbox.showToast(mHostActivity, result);

        switch (inputType) {
            case BARCODE:
                mName = "";
                mDescription = "";
                attr = getString(R.string.data_attribution_upcitemdb);
                // TODO: Set the imageUri to the first image in the result array
                mImageUri = "";
                // TODO: Set the barcodeUri to the same image captured
                mBarcodeUri = "";
                break;
            case IMG_REC:
                mName = "";
                mDescription = "";
                attr = getString(R.string.data_attribution_google_imgrec);
                // TODO: Set the imageUri to the same image captured
                mImageUri = "";
                mBarcodeUri = "";
                mIvBarcode.setVisibility(View.INVISIBLE);
                break;
            default:
                return;
        }

        // common fields
        mTvBarcode.setText(mBarcode);
        mTvName.setText(mName);
        mTvDescription.setText(mDescription);
        mTvAttr.setText(attr);
        Toolbox.loadImageFromUrl(mHostActivity, mImageUri, mIvImage, new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        });
        Toolbox.loadImageFromUrl(mHostActivity, mBarcode, mIvBarcode, new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        });

        // expiry date, use voice prompt depending on settings
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        boolean voicePrompt = sp.getBoolean(getString(R.string.pref_capture_voice_input_key), true);
        promptExpiryDate(voicePrompt);
    }

    /**
     * Fetches info about the provided barcode from the upcitemdb
     */
    private class GetProductsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            fillInFieldsFromOnline(mInputType, result);
        }

        // https://api.upcitemdb.com/prod/trial/search?s=google%20pixel%202&match_mode=0&type=product
        @Override
        protected String doInBackground(String... barcodes) {
            String queryUrl = UPCITEMDB_BASE_UPC_SEARCH_URL + barcodes[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(queryUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error getting results";
        }
    }

    // region Get expiry date

    /**
     * Ask the user for the scanned item's expiry date
     */
    private void promptExpiryDate(boolean voiceInput) {
        if (voiceInput) {
            startVoiceInput();
        } else {
            showDatePickerDialog();
        }
    }

    /**
     * Prompts the user to say a date
     */
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt_expiry));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    /**
     * Prompts the user to select a date, with the user's current date as default date
     */
    private void showDatePickerDialog() {
        ExpiryDatePickerDialog dialog = ExpiryDatePickerDialog.newInstance(
                ExpiryDatePickerDialog.DateType.EXPIRY, mCurrentDateStartOfDay,
                mCurrentDateStartOfDay);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ExpiryDatePickerDialog.class.getSimpleName());
    }

    // endregion

    // region Handling date input

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String dateString = result.get(0);
                    DateTime date = parseDateFromVoiceInput(dateString);
                    setExpiryDate(date.getMillis());
                } else if (resultCode == RESULT_CANCELED) {
                    // Set current date as default expiry date
                    setExpiryDate(mCurrentDateStartOfDay);
                }
                break;
            }
        }
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
    public void onDateSelected(ExpiryDatePickerDialog.DateType dateType, DateTime selectedDate) {
        setExpiryDate(selectedDate.getMillis());
    }

    /**
     * Sets the expiry date textview
     *
     * @param date
     */
    private void setExpiryDate(long date) {
        mTvExpiryDate.setText(getString(R.string.expiry_msg_generic,
                DataToolbox.getFormattedFullDateString(date)));
        mDateExpiry = date;
    }

    // endregion

    /**
     * Helper that creates food object
     *
     * @param name
     * @param description
     * @param dateExpiry
     * @param brand
     * @param size
     * @param weight
     * @param images
     * @param barcode
     * @param inputType
     * @param loc
     * @return
     */
    private Food createFood(String name, String description, long dateExpiry, String brand,
                            String size, String weight, Bitmap images, String barcode,
                            InputType inputType, Storage loc) {
        List<String> imageUris = new ArrayList<>();
        return new Food(name, dateExpiry, dateExpiry, 1, loc, description, brand, size,
                weight, null, barcode, inputType, imageUris);
    }
}
