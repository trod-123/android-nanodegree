package com.zn.expirytracker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.rd.PageIndicatorView;
import com.zn.expirytracker.BuildConfig;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.dialog.AddImageMethodPickerBottomSheet;
import com.zn.expirytracker.ui.dialog.ConfirmDeleteDialogFragment;
import com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialogFragment;
import com.zn.expirytracker.ui.dialog.FormChangedDialogFragment;
import com.zn.expirytracker.ui.dialog.OnDialogCancelListener;
import com.zn.expirytracker.ui.dialog.StorageLocationDialogFragment;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.Constants;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.DateToolbox;
import com.zn.expirytracker.utils.EditToolbox;
import com.zn.expirytracker.utils.FormChangedDetector;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Callbacks for {@link DialogFragment} idea from: https://gist.github.com/Joev-/5695813
 */
public class EditFragment extends Fragment implements
        StorageLocationDialogFragment.OnStorageLocationSelectedListener,
        ExpiryDatePickerDialogFragment.OnDateSelectedListener,
        FormChangedDialogFragment.OnFormChangedButtonClickListener,
        ConfirmDeleteDialogFragment.OnConfirmDeleteButtonClickListener, OnDialogCancelListener,
        DetailImageFragment.OnImageButtonClickListener,
        AddImageMethodPickerBottomSheet.OnAddImageMethodSelectedListener,
        View.OnClickListener, EasyPermissions.PermissionCallbacks {

    public static final String ARG_ITEM_ID_LONG = Toolbox.createStaticKeyString(
            "edit_fragment.item_id_long");
    public static final String ARG_BARCODE_STRING = Toolbox.createStaticKeyString(
            "edit_fragment.barcode_string");
    public static final String ARG_INPUT_TYPE = Toolbox.createStaticKeyString(
            "edit_fragment.input_type");

    /**
     * For keeping track of the original EditText values after screen rotation
     */
    private static final String KEY_FORM_CHANGED_EDIT_TEXT_STRINGS_CACHE =
            Toolbox.createStaticKeyString(EditFragment.class,
                    "form_changed_edit_text_strings_cache");

    /**
     * For keeping track of the original Image uris values after screen rotation
     */
    private static final String KEY_FORM_CHANGED_STRINGS_LIST_CACHE =
            Toolbox.createStaticKeyString(EditFragment.class,
                    "form_changed_strings_list_cache");

    /**
     * For keeping track of the updated image uris values after screen rotation
     */
    private static final String KEY_CURRENT_IMAGE_URIS_LIST =
            Toolbox.createStaticKeyString(EditFragment.class, "current_image_uris_list");

    /**
     * For keeping track of the removed image uris list after screen rotation
     */
    private static final String KEY_REMOVED_IMAGE_URIS_LIST =
            Toolbox.createStaticKeyString(EditFragment.class, "deleted_image_uris_list");

    /**
     * For keeping track of the added image uris list after screen rotation
     */
    private static final String KEY_ADDED_IMAGE_URIS_LIST =
            Toolbox.createStaticKeyString(EditFragment.class, "added_image_uris_list");

    /**
     * For restoring current image pager position after screen rotation
     */
    private static final String KEY_CURRENT_IMAGE_PAGER_POSITION =
            Toolbox.createStaticKeyString(EditFragment.class, "current_image_pager_position");

    private static final String KEY_CURRENT_EXPIRY_DATE_LONG =
            Toolbox.createStaticKeyString(EditFragment.class, "current_expiry_date");
    private static final String KEY_CURRENT_GOOD_THRU_DATE_LONG =
            Toolbox.createStaticKeyString(EditFragment.class, "current_good_thru_date");
    private static final String KEY_CURRENT_STORAGE_LOC_ENUM =
            Toolbox.createStaticKeyString(EditFragment.class, "current_storage_loc");

    /**
     * Prevent the saved camera image uri from getting lost when user rotates camera activity
     */
    private static final String KEY_CURRENT_CAMERA_CAPTURE_LOC_STRING =
            Toolbox.createStaticKeyString(EditFragment.class, "current_camera_capture_loc");

    /**
     * Keep Other Info showing after config changes if it's visible beforehand
     */
    private static final String KEY_VISIBILITY_OTHER_INFO =
            Toolbox.createStaticKeyString(EditFragment.class, "visibility_other_info");

    /**
     * Default code to use for {@link EditFragment#ARG_ITEM_ID_LONG} if adding a new item, not
     * editing an existing one
     */
    public static final int POSITION_ADD_MODE = -1024;

    private static final int RC_LOAD_IMAGE = 1100;
    private static final int RC_CAMERA = 1101;
    private static final int RC_PERMISSIONS_CAMERA = 4011;
    private static final int RC_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 4012;

    public static final int DEFAULT_STARTING_COUNT = 1;
    public static final Storage DEFAULT_STARTING_STORAGE = Storage.NOT_SET;
    public static final InputType DEFAULT_INPUT_TYPE = InputType.NONE;

    private static final int IMAGE_PAGER_POSITION_NOT_SET = -1;

    @BindView(R.id.layout_edit_root)
    View mRootLayout;
    // TODO: Hide for now
//    @BindView(R.id.fab_edit_voice)
//    FloatingActionButton mFabVoice;

    @BindView(R.id.viewPager_detail_image)
    ViewPager mViewPager;
    @BindView(R.id.iv_scrim_detail_image)
    ImageView mImageScrim;
    @BindView(R.id.pageIndicatorView_detail_image)
    PageIndicatorView mPageIndicatorView;

    @BindView(R.id.tiEt_edit_food_name)
    TextInputEditText mEtFoodName;
    @BindView(R.id.tiEt_edit_food_name_error)
    TextInputEditText mEtFoodNameError;
    @BindView(R.id.tiEt_edit_date_expiry)
    TextInputEditText mEtDateExpiry;
    @BindView(R.id.tiEt_edit_date_good)
    TextInputEditText mEtDateGood;
    @BindView(R.id.iv_edit_date_good_clear)
    ImageView mIvDateGoodClear;
    @BindView(R.id.iv_edit_minus_btn)
    ImageView mIvMinus;
    @BindView(R.id.iv_edit_plus_btn)
    ImageView mIvPlus;
    @BindView(R.id.tiEt_edit_count)
    TextInputEditText mEtCount;
    @BindView(R.id.tiEt_edit_count_error)
    TextInputEditText mEtCountError;
    @BindView(R.id.iv_edit_storage_location)
    ImageView mIvLoc;
    @BindView(R.id.tiEt_edit_storage_location)
    TextInputEditText mEtLoc;
    @BindView(R.id.tiEt_edit_description)
    TextInputEditText mEtDescription;
    @BindView(R.id.iv_edit_description_clear)
    ImageView mIvDescriptionClear;

    @BindView(R.id.layout_edit_other_info)
    View mOtherInfoRootView;
    @BindView(R.id.iv_edit_other_info_caret)
    ImageView mIvExpandOtherInfo;
    @BindView(R.id.tiEt_edit_brand)
    TextInputEditText mEtBrand;
    @BindView(R.id.iv_edit_brand_clear)
    ImageView mIvBrandClear;
    @BindView(R.id.tiEt_edit_size)
    TextInputEditText mEtSize;
    @BindView(R.id.iv_edit_size_clear)
    ImageView mIvSizeClear;
    @BindView(R.id.tiEt_edit_weight)
    TextInputEditText mEtWeight;
    @BindView(R.id.iv_edit_weight_clear)
    ImageView mIvWeightClear;
    @BindView(R.id.tiEt_edit_notes)
    TextInputEditText mEtNotes;
    @BindView(R.id.iv_edit_notes_clear)
    ImageView mIvNotesClear;

    private DetailImagePagerAdapter mPagerAdapter;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private FormChangedDetector<TextInputEditText> mFormChangedDetector;
    private Food mFood;
    private long mItemId;
    private boolean mAddMode = false;

    // Fields not directly translatable or accessible via EditText
    private List<String> mImageUris;
    /**
     * Track the removed image uris for a single bulk delete operation after user saves
     */
    private List<String> mRemovedImageUris = new ArrayList<>();
    /**
     * Track the added image uris saved to cache for a bulk delete operation after user abandons
     * without saving
     */
    private List<String> mAddedImageUris = new ArrayList<>();
    private long mExpiryDate;
    private long mGoodThruDate;
    private Storage mLoc;
    private String mBarcode;
    private InputType mInputType;

    // For savedInstanceState
    private List<String> mCachedEditTextInputs;
    private List<String> mCachedImageUris;
    private int mCurrentImagePosition = IMAGE_PAGER_POSITION_NOT_SET;
    private boolean mRestoredInstance;
    private boolean mIsOtherInfoShowing = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList(KEY_FORM_CHANGED_EDIT_TEXT_STRINGS_CACHE,
                new ArrayList<>(mFormChangedDetector.getCachedEditTextStrings()));
        outState.putStringArrayList(KEY_FORM_CHANGED_STRINGS_LIST_CACHE,
                new ArrayList<>(mFormChangedDetector.getCachedStringsList()));
        outState.putStringArrayList(KEY_CURRENT_IMAGE_URIS_LIST,
                new ArrayList<>(mImageUris));
        outState.putStringArrayList(KEY_REMOVED_IMAGE_URIS_LIST,
                new ArrayList<>(mRemovedImageUris));
        outState.putStringArrayList(KEY_ADDED_IMAGE_URIS_LIST,
                new ArrayList<>(mAddedImageUris));
        outState.putInt(KEY_CURRENT_IMAGE_PAGER_POSITION, mCurrentImagePosition);
        outState.putLong(KEY_CURRENT_EXPIRY_DATE_LONG, mExpiryDate);
        outState.putLong(KEY_CURRENT_GOOD_THRU_DATE_LONG, mGoodThruDate);
        outState.putSerializable(KEY_CURRENT_STORAGE_LOC_ENUM, mLoc);
        outState.putString(KEY_CURRENT_CAMERA_CAPTURE_LOC_STRING, mCurrentCameraCapturePath);
        outState.putBoolean(KEY_VISIBILITY_OTHER_INFO, mIsOtherInfoShowing);
        super.onSaveInstanceState(outState);
    }

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new {@link EditFragment} with the provided {@code itemId}.
     * <p>
     * Provide a {@code barcode} if coming from
     * {@link com.zn.expirytracker.ui.capture.CaptureActivity}. This is not required for editing
     * <p>
     * Provide an {@code inputType} if adding a new item. Will be set to
     * {@link EditFragment#DEFAULT_INPUT_TYPE} if null. Providing one for editing will be replaced
     * by the {@code itemId} item's own {@link InputType}
     *
     * @param itemId
     * @param barcode
     * @param inputType
     * @return
     */
    public static EditFragment newInstance(long itemId, @Nullable String barcode,
                                           @Nullable InputType inputType) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID_LONG, itemId);
        args.putString(ARG_BARCODE_STRING, barcode);
        args.putSerializable(ARG_INPUT_TYPE, inputType); // enums are serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(EditFragment.class.getSimpleName());
        setHasOptionsMenu(true);

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            // The position will determine the populated elements
            mItemId = args.getLong(ARG_ITEM_ID_LONG, 0);

            mAddMode = mItemId == POSITION_ADD_MODE;
            mBarcode = args.getString(ARG_BARCODE_STRING, "");
            InputType inputType = (InputType) args.getSerializable(ARG_INPUT_TYPE);
            mInputType = inputType != null ? inputType : DEFAULT_INPUT_TYPE;
        }

        // Set the dates
        if (mAddMode) {
            // By default, load up the current day as the initial expiry date
            mExpiryDate = DateToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
            // For all new items, goodThruDate is the same as expiryDate by default
            mGoodThruDate = mExpiryDate;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, rootView);

        mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(), true,
                Toolbox.isLeftToRightLayout());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Keep the current selected position in sync between ViewPager and PageIndicator
                mPageIndicatorView.setSelection(position);
                mCurrentImagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Hide the fab when the keyboard is opened
        // https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device
//        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                mRootLayout.getWindowVisibleDisplayFrame(r);
//                int screenHeight = mRootLayout.getRootView().getHeight();
//
//                // r.bottom is the position above soft keypad or device button.
//                // if keypad is shown, the r.bottom is smaller than that before.
//                int keypadHeight = screenHeight - r.bottom;
//
//                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
//                    // keyboard is opened
//                    mFabVoice.hide();
//                } else {
//                    // keyboard is closed
//                    mFabVoice.show();
//                }
//            }
//        });

        if (savedInstanceState != null) {
            mCachedEditTextInputs = savedInstanceState
                    .getStringArrayList(KEY_FORM_CHANGED_EDIT_TEXT_STRINGS_CACHE);
            mCachedImageUris = savedInstanceState
                    .getStringArrayList(KEY_FORM_CHANGED_STRINGS_LIST_CACHE);

            mCurrentImagePosition = savedInstanceState.getInt(KEY_CURRENT_IMAGE_PAGER_POSITION);

            // Non edit text values that need to be preserved and restored
            mImageUris = savedInstanceState.getStringArrayList(KEY_CURRENT_IMAGE_URIS_LIST);
            mRemovedImageUris = savedInstanceState.getStringArrayList(KEY_REMOVED_IMAGE_URIS_LIST);
            mAddedImageUris = savedInstanceState.getStringArrayList(KEY_ADDED_IMAGE_URIS_LIST);
            mExpiryDate = savedInstanceState.getLong(KEY_CURRENT_EXPIRY_DATE_LONG);
            mGoodThruDate = savedInstanceState.getLong(KEY_CURRENT_GOOD_THRU_DATE_LONG);
            mLoc = (Storage) savedInstanceState.getSerializable(KEY_CURRENT_STORAGE_LOC_ENUM);

            mCurrentCameraCapturePath =
                    savedInstanceState.getString(KEY_CURRENT_CAMERA_CAPTURE_LOC_STRING);

            mIsOtherInfoShowing = savedInstanceState.getBoolean(KEY_VISIBILITY_OTHER_INFO);
            showOtherInfoFields(mIsOtherInfoShowing);

            mRestoredInstance = true;
        }

        final LiveData<Food> data = mViewModel.getSingleFoodById(mItemId, false);
        data.observe(this, new Observer<Food>() {
            @Override
            public void onChanged(@Nullable Food food) {
                mFood = food;
                populateFields(mFood); // handles picking and choosing fields to fill after rotation

                // this needs to be done AFTER fields are populated, but also call it when adding
                if (mRestoredInstance) {
                    // restore from cache after rotation
                    mFormChangedDetector = restoreExistingFormChangedDetector(
                            mCachedEditTextInputs, mCachedImageUris);
                } else {
                    mFormChangedDetector = getFormChangedDetector();
                }

                // do not make any changes to any fields once UI is loaded. this also solves issue
                // where adding an image resets the image adapter position back to 0 (though it
                // could probably be because we were saving new images to food before even saving
                // the food itself
                data.removeObserver(this);
            }
        });

        setTextChangedListeners();
        setClickListeners();

        if (!mRestoredInstance && mAddMode) {
            // Allow user to enter food name right away upon loading Add. Make sure in Manifest,
            // android:windowSoftInputMode="stateVisible" is also set for the keyboard to pop up
            mEtFoodName.requestFocus();
        }

        // Show the image limit message
        SharedPreferences sp = mHostActivity.getSharedPreferences(
                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        if (!sp.getBoolean(Constants.SP_KEY_IMAGE_LIMIT_SEEN, false)) {
            Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.limits_image_list_size));
            sp.edit().putBoolean(Constants.SP_KEY_IMAGE_LIMIT_SEEN, true).apply();
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mAddMode) {
            inflater.inflate(R.menu.menu_add, menu);
        } else {
            inflater.inflate(R.menu.menu_edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper that pre-populates all fields, depending on if we're recreating the fragment
     */
    private void populateFields(@Nullable Food food) {
        // Only load saved values if NOT adding
        if (!mAddMode && food != null) {
            // Image adapter
            if (!mRestoredInstance) {
                mImageUris = food.getImages();
            }
            mPagerAdapter.setImageUris(mImageUris);

            // Get all values first before populating fields (except dates and storage location,
            // which are restored via saveInstanceState
            String description = food.getDescription();

            String brand = food.getBrandName();
            String size = food.getSize();
            String weight = food.getWeight();
            String notes = food.getNotes();

            mBarcode = food.getBarcode();
            mInputType = food.getInputType();

            if (!mRestoredInstance) {
                // Get dates since we don't have them yet
                mExpiryDate = DateToolbox.getTimeInMillisStartOfDay(food.getDateExpiry());
                mGoodThruDate = DateToolbox.getTimeInMillisStartOfDay(food.getDateGoodThru());

                mLoc = food.getStorageLocation();

                // Only fill in fields if we haven't restored fragment instance
                mEtFoodName.setText(food.getFoodName());
                mEtCount.setText(String.valueOf(food.getCount()));

                mEtDescription.setText(description);
                hideViewIfEmptyString(description, mIvDescriptionClear);

                mEtBrand.setText(brand);
                hideViewIfEmptyString(brand, mIvBrandClear);

                mEtSize.setText(size);
                hideViewIfEmptyString(size, mIvSizeClear);

                mEtWeight.setText(weight);
                hideViewIfEmptyString(weight, mIvWeightClear);

                mEtNotes.setText(notes);
                hideViewIfEmptyString(notes, mIvNotesClear);
            }
        } else {
            // Set ADD_MODE defaults
            if (!mRestoredInstance) {
                mImageUris = new ArrayList<>();
                mEtCount.setText(String.valueOf(DEFAULT_STARTING_COUNT));
                mLoc = DEFAULT_STARTING_STORAGE;
            }
            mPagerAdapter.setImageUris(mImageUris);
            showFieldError(mEtFoodName.getText().toString().isEmpty(), FieldError.REQUIRED_NAME);
        }

        // Common fields
        mViewPager.setCurrentItem(mCurrentImagePosition != IMAGE_PAGER_POSITION_NOT_SET ?
                mCurrentImagePosition : mImageUris != null && !Toolbox.isLeftToRightLayout() ?
                mImageUris.size() : 0, false); // set for RTL layouts
        // call again out here to invalidate views
        // Call after setting the current item on the viewPager, in case we are adding an image
        // from camera where device had been rotated. If we call this before setting current item,
        // then the Android still thinks we're at the first item in the view pager, thereby not
        // refreshing the actual current item with the image instead of the add button
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setContentDescription(
                mFood != null ? mFood.getFoodName() : getString(R.string.food_image_list));
        mEtDateExpiry.setText(DateToolbox.getFieldFormattedDate(mExpiryDate));
        mEtDateGood.setText(DateToolbox.getFieldFormattedDate(mGoodThruDate));
        if (mLoc != Storage.NOT_SET) {
            mIvLoc.setImageResource(DataToolbox.getStorageIconResource(mLoc));
        } else {
            mIvLoc.setImageDrawable(null);
        }
        mEtLoc.setText(DataToolbox.getStorageIconString(mLoc, mHostActivity));
        setGoodThruDateViewAttributes();
    }

    /**
     * Helper that sets the TextChangedListeners
     */
    private void setTextChangedListeners() {
        mEtFoodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && charSequence.toString().trim().length() > 0) {
                    showFieldError(false, FieldError.REQUIRED_NAME);
                } else {
                    showFieldError(true, FieldError.REQUIRED_NAME);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && charSequence.length() > 0) {
                    int input = Integer.parseInt(charSequence.toString());
                    // Ensure only one error is showing at a time
                    showFieldError(false, FieldError.REQUIRED_COUNT);
                    showFieldError(input < 1 || input > 99, FieldError.INVALID_COUNT);

                } else {
                    showFieldError(false, FieldError.INVALID_COUNT);
                    showFieldError(true, FieldError.REQUIRED_COUNT);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mEtDescription.addTextChangedListener(new HideViewTextWatcher(mIvDescriptionClear));
        mEtBrand.addTextChangedListener(new HideViewTextWatcher(mIvBrandClear));
        mEtSize.addTextChangedListener(new HideViewTextWatcher(mIvSizeClear));
        mEtWeight.addTextChangedListener(new HideViewTextWatcher(mIvWeightClear));
        mEtNotes.addTextChangedListener(new HideViewTextWatcher(mIvNotesClear));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fab_edit_voice:
//                // TODO: Start speech input
//                Toolbox.showToast(mHostActivity, "This will start the speech input!");
//                break;
            case R.id.tiEt_edit_food_name_error:
                showFieldErrorMessage(FieldError.REQUIRED_NAME);
                break;
            case R.id.tiEt_edit_date_expiry:
                showDatePickerDialog(ExpiryDatePickerDialogFragment.DateType.EXPIRY);
                break;
            case R.id.tiEt_edit_date_good:
                showDatePickerDialog(ExpiryDatePickerDialogFragment.DateType.GOOD_THRU);
                break;
            case R.id.iv_edit_minus_btn:
                decreaseCount();
                break;
            case R.id.iv_edit_plus_btn:
                increaseCount();
                break;
            case R.id.tiEt_edit_count_error:
                if (mCurrentFieldErrors.contains(FieldError.INVALID_COUNT)) {
                    showFieldErrorMessage(FieldError.INVALID_COUNT);
                } else if (mCurrentFieldErrors.contains(FieldError.REQUIRED_COUNT)) {
                    showFieldErrorMessage(FieldError.REQUIRED_COUNT);
                }
                break;
            case R.id.tiEt_edit_storage_location:
                showLocationDialog();
                break;
            case R.id.iv_edit_date_good_clear:
                mGoodThruDate = mExpiryDate;
                String fieldFormattedDate = DateToolbox.getFieldFormattedDate(mGoodThruDate);
                mEtDateGood.setText(fieldFormattedDate);
                setGoodThruDateViewAttributes();
                break;
            case R.id.iv_edit_other_info_caret:
                showOtherInfoFields(mIsOtherInfoShowing = !mIsOtherInfoShowing);
                break;
        }
    }

    /**
     * Helper that sets the OnClickListeners
     */
    private void setClickListeners() {
//        mFabVoice.setOnClickListener(this);
        mEtFoodNameError.setOnClickListener(this);
        mEtDateExpiry.setOnClickListener(this);
        mEtDateGood.setOnClickListener(this);
        mIvMinus.setOnClickListener(this);
        mIvPlus.setOnClickListener(this);
        mEtCountError.setOnClickListener(this);
        mEtLoc.setOnClickListener(this);

        mIvDateGoodClear.setOnClickListener(this);
        mIvDescriptionClear.setOnClickListener(new ClearTextClickListener(mEtDescription));
        mIvBrandClear.setOnClickListener(new ClearTextClickListener(mEtBrand));
        mIvSizeClear.setOnClickListener(new ClearTextClickListener(mEtSize));
        mIvWeightClear.setOnClickListener(new ClearTextClickListener(mEtWeight));
        mIvNotesClear.setOnClickListener(new ClearTextClickListener(mEtNotes));

        mIvExpandOtherInfo.setOnClickListener(this);
    }

    private void showOtherInfoFields(boolean show) {
        mIvExpandOtherInfo.setImageResource(show ? R.drawable.ic_keyboard_arrow_up_black_24dp :
                R.drawable.ic_keyboard_arrow_down_black_24dp);
        mOtherInfoRootView.setVisibility(show ? View.VISIBLE : View.GONE);
        //Toolbox.showView(mOtherInfoRootView, show, true, true);
        //showView(mHostActivity, mOtherInfoRootView, show);
    }

    private void showView(Context context, View v, boolean show) {
        Animation a = AnimationUtils.loadAnimation(context, show ?
                R.anim.slide_down : R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    /**
     * Helper to show a view if the given {@link String} is not length 0
     *
     * @param string
     * @param view
     */
    private void hideViewIfEmptyString(@Nullable String string, View view) {
        Toolbox.showView(view, string != null && !string.isEmpty(), true, true);
    }

    /**
     * Fade the good thru date and append a pointer message if it matches the expiry date.
     * Otherwise, restore to default
     */
    private void setGoodThruDateViewAttributes() {
        if (mExpiryDate == mGoodThruDate) {
            mEtDateGood.setTextColor(mHostActivity.getResources().getColor(R.color.textColorPrimaryLight));
            String currentText = mEtDateGood.getText().toString();
            mEtDateGood.setText(getString(R.string.edit_good_thru_date_default, currentText));
            mIvDateGoodClear.setVisibility(View.GONE);
        } else {
            mEtDateGood.setTextColor(mHostActivity.getResources().getColor(R.color.textColorPrimary));
            mIvDateGoodClear.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Increases the count of the count edit text by 1, only if its post-increment is under 100
     */
    private void increaseCount() {
        if (!mCurrentFieldErrors.contains(FieldError.REQUIRED_COUNT)) {
            int count = Integer.parseInt(mEtCount.getText().toString());
            if (++count < 100) {
                // 100 is MAX. Only raise if post-increment is under 100
                mEtCount.setText(String.valueOf(count));
                showFieldError(false, FieldError.INVALID_COUNT);
            }
        } else {
            mEtCount.setText("1");
        }
    }

    /**
     * Decreases the count of the count edit text by 1, only if its post-decrement is above 0
     */
    private void decreaseCount() {
        if (!mCurrentFieldErrors.contains(FieldError.REQUIRED_COUNT)) {
            int count = Integer.parseInt(mEtCount.getText().toString());
            if (--count > 0) {
                // 1 is MIN. Only decrease if post-decrement is over 0
                mEtCount.setText(String.valueOf(count));
                showFieldError(false, FieldError.INVALID_COUNT);
            }
        } else {
            mEtCount.setText("1");
        }
    }

    private void saveItem() {
        if (mCurrentFieldErrors.size() > 0) {
            // Check for errors first
            showSaveErrorMessage();
        } else {
            mFormChangedDetector.updateCachedFields();
            if (mAddMode) {
                Food food = createFoodFromInputs(mExpiryDate, mGoodThruDate, mLoc, mBarcode,
                        mInputType, mImageUris);
                mViewModel.insert(true, food);
                Toolbox.showToast(mHostActivity, getString(R.string.message_item_added,
                        food.getFoodName()));
            } else {
                Food food = updateFoodFromInputs(mItemId, mExpiryDate, mGoodThruDate, mLoc,
                        mBarcode, mInputType, mImageUris);
                mViewModel.update(true, food);
                mViewModel.deleteImages(true, mRemovedImageUris, food.get_id());
                Toolbox.showToast(mHostActivity, getString(R.string.message_item_updated,
                        food.getFoodName()));
            }
            mHostActivity.finish();
        }
    }

    /**
     * Deletes the current food item and any unsaved images added. Finishes the hosting activity
     * when done
     */
    private void deleteItem() {
        mFormChangedDetector.updateCachedFields();
        // User has already confirmed, so delete food, including images from Storage
        mViewModel.delete(true, true, mFood);
        removeCachedImagesFromStorage(mAddedImageUris);
        mHostActivity.setResult(DetailActivity.RESULT_DELETED);
        mHostActivity.finish();
    }

    /**
     * Removes any unsaved images added and finishes the hosting activity when done
     */
    private void discardChanges() {
        mFormChangedDetector.updateCachedFields(); // but won't be saved
        removeCachedImagesFromStorage(mAddedImageUris);
        mHostActivity.finish();
    }

    // region Dialogs

    /**
     * Prompts the user to select a location
     * https://developer.android.com/guide/topics/ui/dialogs
     */
    private void showLocationDialog() {
        StorageLocationDialogFragment dialog = new StorageLocationDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), StorageLocationDialogFragment.class.getSimpleName());
    }

    /**
     * Prompts the user to select a date. Resulting action is based on the
     * {@link ExpiryDatePickerDialogFragment.DateType} passed
     *
     * @param dateType
     */
    private void showDatePickerDialog(ExpiryDatePickerDialogFragment.DateType dateType) {
        ExpiryDatePickerDialogFragment dialog = ExpiryDatePickerDialogFragment.newInstance(dateType, mExpiryDate,
                mGoodThruDate);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ExpiryDatePickerDialogFragment.class.getSimpleName());
    }

    /**
     * Prompts the user to either discard or save changes
     */
    private void showFormChangedDialog() {
        FormChangedDialogFragment dialog = new FormChangedDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), FormChangedDialogFragment.class.getSimpleName());
    }

    /**
     * Prompts the user to confirm whether the current item should be deleted
     */
    private void showConfirmDeleteDialog() {
        ConfirmDeleteDialogFragment dialog = ConfirmDeleteDialogFragment.newInstance(
                mFood.getFoodName(), AuthToolbox.isSignedIn(),
                ConfirmDeleteDialogFragment.DeleteType.ITEM);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ConfirmDeleteDialogFragment.class.getSimpleName());
    }

    /**
     * Handles the {@link Storage} selection from  {@link StorageLocationDialogFragment}
     *
     * @param position
     */
    @Override
    public void onStorageLocationSelected(int position) {
        switch (position) {
            case 1:
                mLoc = Storage.FRIDGE;
                break;
            case 2:
                mLoc = Storage.FREEZER;
                break;
            case 3:
                mLoc = Storage.PANTRY;
                break;
            case 4:
                mLoc = Storage.COUNTER;
                break;
            case 5:
                mLoc = Storage.CUSTOM;
                break;
            case 0:
            default:
                mLoc = Storage.NOT_SET;
        }

        // TODO: Handle rotation changes not updating text properly (open dialog, rotate, choose, value does not change from original)
        if (mLoc != Storage.NOT_SET) {
            mIvLoc.setImageResource(DataToolbox.getStorageIconResource(mLoc));
        } else {
            mIvLoc.setImageDrawable(null);
        }
        mEtLoc.setText(DataToolbox.getStorageIconString(mLoc, mHostActivity));
    }

    /**
     * Handles the date selection from {@link ExpiryDatePickerDialogFragment}
     *
     * @param dateType
     * @param selectedDate
     */
    @Override
    public void onDateSelected(ExpiryDatePickerDialogFragment.DateType dateType, DateTime selectedDate) {
        String fieldFormattedDate = DateToolbox.getFieldFormattedDate(selectedDate);
        switch (dateType) {
            case GOOD_THRU:
                mGoodThruDate = selectedDate.getMillis();
                mEtDateGood.setText(fieldFormattedDate);
                setGoodThruDateViewAttributes();
                break;
            case EXPIRY:
            default:
                mExpiryDate = selectedDate.getMillis();
                mEtDateExpiry.setText(fieldFormattedDate);
                // By default, when expiry date is changed, set good thru date equal to it
                mGoodThruDate = mExpiryDate;
                mEtDateGood.setText(fieldFormattedDate);
                setGoodThruDateViewAttributes();
        }
    }

    /**
     * Handles the button clicked from {@link FormChangedDialogFragment}
     *
     * @param position
     */
    @Override
    public void onFormChangedButtonClicked(int position) {
        switch (position) {
            case AlertDialog.BUTTON_NEGATIVE: // dismiss
                discardChanges();
                break;
            case AlertDialog.BUTTON_POSITIVE: // save
                saveItem();
                break;
        }
    }

    /**
     * Handles the button clicked from {@link ConfirmDeleteDialogFragment}
     *
     * @param position
     */
    @Override
    public void onConfirmDeleteButtonClicked(int position, boolean isSignedIn,
                                             ConfirmDeleteDialogFragment.DeleteType deleteType) {
        switch (position) {
            case AlertDialog.BUTTON_POSITIVE:
                // None of the arguments matter here at this point
                deleteItem();
                break;
        }
    }

    @Override
    public void onCancelled(Class klass, DialogInterface dialogInterface) {
        // Necessary to be overridden
    }

    // endregion

    // region Error handling

    private List<FieldError> mCurrentFieldErrors = new ArrayList<>();

    /**
     * For keeping track of current errors
     */
    private enum FieldError {
        REQUIRED_NAME, REQUIRED_COUNT, INVALID_COUNT;
    }

    /**
     * Generic method that shows an error for the provided {@link FieldError}
     *
     * @param show
     * @param error
     */
    private void showFieldError(boolean show, FieldError error) {
        // Only proceed if error list is changing
        if ((show && !mCurrentFieldErrors.contains(error)) ||
                !show && mCurrentFieldErrors.contains(error)) {

            TextInputEditText editTextView;
            TextInputEditText errorEditTextView;
            String errorMessage;

            switch (error) {
                case REQUIRED_NAME:
                    editTextView = mEtFoodName;
                    errorEditTextView = mEtFoodNameError;
                    errorMessage = getString(R.string.edit_error_required_generic);
                    break;
                case REQUIRED_COUNT:
                    editTextView = mEtCount;
                    errorEditTextView = mEtCountError;
                    errorMessage = getString(R.string.edit_error_required_generic);
                    break;
                case INVALID_COUNT:
                    editTextView = mEtCount;
                    errorEditTextView = mEtCountError;
                    errorMessage = getString(R.string.edit_error_count);
                    break;
                default:
                    // Don't do anything
                    return;
            }
            if (show) {
                errorEditTextView.setError(errorMessage);
                errorEditTextView.setContentDescription(errorMessage);
                editTextView.setTextColor(mHostActivity.getResources().getColor(R.color.textColorError));

                if (!mCurrentFieldErrors.contains(error)) {
                    // Only add error if it's not already being tracked
                    mCurrentFieldErrors.add(error);
                }
            } else {
                errorEditTextView.setError(null);
                errorEditTextView.setContentDescription(null);
                editTextView.setTextColor(mHostActivity.getResources().getColor(R.color.textColorPrimary));

                mCurrentFieldErrors.remove(error); // auto handles absent case, so no need to check
            }
        }
    }

    /**
     * Show a snackbar pertaining to the {@link FieldError} provided
     *
     * @param error
     */
    private void showFieldErrorMessage(FieldError error) {
        switch (error) {
            case REQUIRED_NAME:
            case REQUIRED_COUNT:
                Toolbox.showSnackbarMessage(getView(), getString(R.string.edit_error_required_generic));
                break;
            case INVALID_COUNT:
                Toolbox.showSnackbarMessage(getView(), getString(R.string.edit_error_count));
                break;
        }
    }

    /**
     * Shows a save error when user attempts to save with errors
     */
    private void showSaveErrorMessage() {
        Toolbox.showSnackbarMessage(getView(), getString(R.string.edit_error_saving));
    }

    // endregion

    /**
     * Custom {@link TextWatcher} that hides a given view if the hosting {@link EditText} is currently
     * empty
     */
    private class HideViewTextWatcher implements TextWatcher {
        private View mView;

        HideViewTextWatcher(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            hideViewIfEmptyString(charSequence.toString(), mView);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * Custom {@link View.OnClickListener} that clears a given {@link EditText}
     */
    private class ClearTextClickListener implements View.OnClickListener {
        private EditText mEditText;

        ClearTextClickListener(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void onClick(View view) {
            mEditText.setText("");
        }
    }

    // region Checking for form changes

    /**
     * Generates a {@link FormChangedDetector} with this fragment's list of edit texts and a
     * preservable strings list
     *
     * @return
     */
    private FormChangedDetector<TextInputEditText> getFormChangedDetector() {
        return new FormChangedDetector<>(getTextInputEditTexts(), mImageUris);
    }

    /**
     * Restores previously cached values into a new instance of a {@link FormChangedDetector}
     *
     * @param cachedEditTextInputs
     * @param cachedStringsList
     * @return
     */
    private FormChangedDetector<TextInputEditText> restoreExistingFormChangedDetector(
            List<String> cachedEditTextInputs, List<String> cachedStringsList) {
        return new FormChangedDetector<>(getTextInputEditTexts(), mImageUris,
                cachedEditTextInputs, cachedStringsList);
    }

    private List<TextInputEditText> getTextInputEditTexts() {
        return new ArrayList<>(
                Arrays.asList(
                        mEtFoodName,
                        mEtDateExpiry,
                        mEtDateGood,
                        mEtCount,
                        mEtLoc,
                        mEtDescription,
                        mEtBrand,
                        mEtSize,
                        mEtWeight,
                        mEtNotes)
        );
    }

    /**
     * Helper that checks and actions on whether fields have changed
     */
    public boolean haveFieldsChanged() {
        boolean changed = mFormChangedDetector.haveFieldsChanged();
        if (changed) {
            showFormChangedDialog();
            return true;
        } else {
            return false;
        }
    }

    // endregion

    // region Add/Update items

    /**
     * Creates a {@link Food} item. Assumes {@code editTexts} contains all of the following, in this
     * exact order:
     * <p>
     * mEtFoodName
     * mEtCount
     * mEtDescription
     * mEtBrand
     * mEtSize
     * mEtWeight
     * mEtNotes
     * <p>
     * If the size of {@code editTexts} does not match the expected size, this method returns
     * {@code null}
     *
     * @param dateExpiry
     * @param dateGoodthru
     * @param loc
     * @param barcode
     * @param inputType
     * @param imageUris
     * @return
     */
    private Food createFoodFromInputs(long dateExpiry, long dateGoodthru, Storage loc,
                                      String barcode, InputType inputType, List<String> imageUris) {
        List<TextInputEditText> editTexts = mergeEditTextsIntoList();
        List<String> etValues = EditToolbox.getStringsFromEditTexts(editTexts);
        String foodName = etValues.get(0);
        int count = Integer.valueOf(etValues.get(1));
        String description = etValues.get(2);
        String brand = etValues.get(3);
        String size = etValues.get(4);
        String weight = etValues.get(5);
        String notes = etValues.get(6);

        return new Food(foodName, dateExpiry, dateGoodthru, count, loc, description, brand, size,
                weight, notes, barcode, inputType, imageUris);
    }

    /**
     * Updates a {@link Food} item with the provided {@code _id}. Assumes {@code editTexts}
     * contains all of the following, in this exact order:
     * <p>
     * mEtFoodName
     * mEtCount
     * mEtDescription
     * mEtBrand
     * mEtSize
     * mEtWeight
     * mEtNotes
     * <p>
     * If the size of {@code editTexts} does not match the expected size, this method returns
     * {@code null}
     *
     * @param _id
     * @param dateExpiry
     * @param dateGoodthru
     * @param loc
     * @param barcode
     * @param inputType
     * @param imageUris
     * @return
     */
    private Food updateFoodFromInputs(long _id, long dateExpiry, long dateGoodthru, Storage loc,
                                      String barcode, InputType inputType, List<String> imageUris) {
        Food food = createFoodFromInputs(dateExpiry, dateGoodthru, loc, barcode, inputType,
                imageUris);
        food.set_id(_id);
        return food;
    }

    /**
     * Helper that prepares a list of {@link TextInputEditText} to be used for
     * {@link EditFragment#createFoodFromInputs(long, long, Storage, String, InputType, List)} and
     * {@link EditFragment#updateFoodFromInputs(long, long, long, Storage, String, InputType, List)}
     *
     * @return
     */
    private List<TextInputEditText> mergeEditTextsIntoList() {
        return new ArrayList<>(
                Arrays.asList(
                        mEtFoodName,
                        mEtCount,
                        mEtDescription,
                        mEtBrand,
                        mEtSize,
                        mEtWeight,
                        mEtNotes)
        );
    }

    // endregion

    // region Add new image

    /**
     * Show the bottom sheet when the Add Image button in the {@link DetailImageFragment} is
     * clicked
     */
    @Override
    public void onAddImageButtonClick() {
        // Show the bottom sheet
        AddImageMethodPickerBottomSheet bottomSheet = new AddImageMethodPickerBottomSheet();
        bottomSheet.setTargetFragment(this, 0);
        bottomSheet.show(getFragmentManager(),
                AddImageMethodPickerBottomSheet.class.getSimpleName());
    }

    @Override
    public void onCameraInputSelected() {
        captureImageFromCamera();
    }

    @Override
    public void onImagePickerSelected() {
        getImageFromStorage();
    }

    private String mCurrentCameraCapturePath;

    /**
     * Launches the camera for the user to take an image. Intent includes the filepath where the
     * image would be saved. Saving images in this way, instead of grabbing them from
     * onActivityResult() is better because (1) orientation is preserved upon displaying images,
     * and (2) the images saved are not thumbnails. intent.getData() returns the THUMBNAIL, not
     * the actual image
     * <p>
     * https://developer.android.com/training/camera/photobasics
     */
    @AfterPermissionGranted(RC_PERMISSIONS_CAMERA)
    public void captureImageFromCamera() {
        String[] perms = new String[]{Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(mHostActivity, perms)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.message_permissions_camera_shoot),
                    RC_PERMISSIONS_CAMERA, perms);
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure device has camera activity to handle this first
        if (cameraIntent.resolveActivity(mHostActivity.getPackageManager()) != null) {
            File outputFilePath = null;
            try {
                // Get the path where file would be saved
                outputFilePath = Toolbox.getBitmapSavingFilePath(mHostActivity,
                        Constants.DEFAULT_FILENAME);
                // Use File.getAbsolutePath() to get the String path that we can store in the
                // Food images list, which could then be loaded up into Glide
                mCurrentCameraCapturePath = outputFilePath.getAbsolutePath();
                Timber.d("Current Camera Capture Path: %s", mCurrentCameraCapturePath);
            } catch (IOException e) {
                Timber.e(e, "There was a problem creating the output camera image file");
            }
            if (outputFilePath != null) {
                // Generate the uri from the path. Fileprovider details in Manifest
                Uri photoUri = FileProvider.getUriForFile(
                        mHostActivity, BuildConfig.APPLICATION_ID + ".fileprovider", outputFilePath);
                // Indicate where photo output should be saved.
                // note this will set result intent data to null, but we don't need to check anyway
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, RC_CAMERA);
            } else {
                Timber.e("EditFragment: Attempted to start Capture, but the outputFilePath was null");
                Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.message_error_save_image_cache));
            }
        } else {
            Timber.d("EditFragment: Attempted to start Capture, but device does not have a camera");
            Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.message_camera_required));
        }
    }

    /**
     * Prompts the user to pick an image from storage
     */
    @AfterPermissionGranted(RC_PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void getImageFromStorage() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(mHostActivity, perms)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.message_permissions_storage),
                    RC_PERMISSIONS_WRITE_EXTERNAL_STORAGE, perms);
            return;
        }
        // Get image from user storage
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RC_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                addImageFromUri(imageUri);
            }
        } else if (requestCode == RC_CAMERA && resultCode == RESULT_OK) {
            Timber.d("EditFragment: Returning from camera with OK result. Adding the image to list...");
            // data.getData() returns a THUMBNAIL of the image, and no orientation preservation.
            // So instead we set an Extra in the camera intent containing the image storage uri
            // At this point we had already saved the image into app storage, so we don't need to
            // check for the intent data. all we need to do is to add the path to the Food image list
            // https://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
            addImageFromCamera(mCurrentCameraCapturePath);
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Called after the user returns from the AppSettingsDialog that takes the user to the
            // app settings to change permissions
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Stores a copy of the image in app's internal storage and saves the Uri into the food item.
     * Updates the food item when done
     *
     * @param uri
     */
    private void addImageFromUri(Uri uri) {
        // Save bitmap to internal storage and then update the image uri list
        try {
            Timber.d("EditFragment/AddImageFromUri: Passed uri: %s", uri);
            File outputFilePath = Toolbox.getBitmapSavingFilePath(mHostActivity,
                    Constants.DEFAULT_FILENAME);
            String inputFilePath = Toolbox.getImagePathFromInputStreamUri(mHostActivity, uri);
            Timber.d("EditFragment/AddImageFromUri: Input path: %s", inputFilePath);
            Toolbox.copyFile(new File(inputFilePath), outputFilePath);
            String path = outputFilePath.getAbsolutePath();
            Timber.d("EditFragment/AddImageFromUri: Saved image path: %s", path);
            addImageToUrisList(path);
            Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.message_image_added));
        } catch (IOException e) {
            Timber.e(e, "EditFragment/AddImageFromUri: There was a problem saving the image to internal storage");
            Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.message_error_save_image));
        }
    }

    /**
     * Updates the image uri list with the newly captured photo, with the path
     */
    private void addImageFromCamera(@NonNull String imagePath) {
        addImageToUrisList(imagePath);
        Toolbox.showSnackbarMessage(mRootLayout, getString(R.string.message_image_added));
    }

    /**
     * Helper to add an image to the food image uris list. Updates the list maintaining its order,
     * while handling LTR and RTL differences accordingly
     *
     * @param path
     */
    private void addImageToUrisList(@NonNull String path) {
        mImageUris.add(path);
        mAddedImageUris.add(path);
        if (!Toolbox.isLeftToRightLayout()) {
            mPagerAdapter.setImageUris(mImageUris);
            mViewPager.setCurrentItem(1);
        } else {
            mPagerAdapter.notifyDataSetChanged();
        }
        mPageIndicatorView.setCount(mPagerAdapter.getCount());
    }

    @Override
    public void onClearImageButtonClick(String uriString) {
        removeImageFromUrisList(uriString);
    }

    /**
     * Deletes an image from the food item image list, but does not delete the image from storage.
     * Also adds the path to a list of pending deleted images, so deletion could be handled after
     * user saves the food item
     * <p>
     * If the image was just recently added, then it will be removed from the cache
     *
     * @param path Uri of the current image
     */
    private void removeImageFromUrisList(@NonNull String path) {
        mImageUris.remove(path);
        mRemovedImageUris.add(path);
        if (mAddedImageUris.contains(path)) {
            Toolbox.deleteBitmapFromInternalStorage(Toolbox.getUriFromImagePath(path),
                    "EditFragment/RemoveImage");
            mAddedImageUris.remove(path);
        }
        if (!Toolbox.isLeftToRightLayout()) {
            mPagerAdapter.setImageUris(mImageUris);
        } else {
            mPagerAdapter.notifyDataSetChanged();
        }
        mPageIndicatorView.setCount(mPagerAdapter.getCount());
    }

    /**
     * Deletes a list of images from internal storage
     *
     * @param uriStrings
     */
    private void removeCachedImagesFromStorage(@NonNull List<String> uriStrings) {
        for (String uriString : uriStrings) {
            Toolbox.deleteBitmapFromInternalStorage(
                    Toolbox.getUriFromImagePath(uriString), "EditFragment/RemoveCache");
        }
    }

    // endregion
}
