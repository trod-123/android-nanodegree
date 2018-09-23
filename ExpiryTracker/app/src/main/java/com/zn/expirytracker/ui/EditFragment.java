package com.zn.expirytracker.ui;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

import com.rd.PageIndicatorView;
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
import com.zn.expirytracker.utils.EditToolbox;
import com.zn.expirytracker.utils.FormChangedDetector;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Callbacks for {@link android.support.v4.app.DialogFragment} idea from: https://gist.github.com/Joev-/5695813
 */
public class EditFragment extends Fragment implements
        StorageLocationDialogFragment.OnStorageLocationSelectedListener,
        ExpiryDatePickerDialogFragment.OnDateSelectedListener,
        FormChangedDialogFragment.OnFormChangedButtonClickListener,
        ConfirmDeleteDialogFragment.OnConfirmDeleteButtonClickListener, OnDialogCancelListener,
        DetailImageFragment.AddImageButtonClickListener,
        AddImageMethodPickerBottomSheet.OnAddImageMethodSelectedListener {

    public static final String ARG_ITEM_ID_LONG = Toolbox.createStaticKeyString(
            "edit_fragment.item_id_long");
    public static final String ARG_BARCODE_STRING = Toolbox.createStaticKeyString(
            "edit_fragment.barcode_string");
    public static final String ARG_INPUT_TYPE = Toolbox.createStaticKeyString(
            "edit_fragment.input_type");

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

    @BindView(R.id.layout_edit_root)
    View mRootLayout;
    @BindView(R.id.fab_edit_voice)
    FloatingActionButton mFabVoice;

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
    private FormChangedDetector mFormChangedDetector;
    private Food mFood;
    private long mItemId;
    private boolean mAddMode = false;

    // Fields not directly translatable or accessible via EditText
    private List<String> mImageUris;
    private long mExpiryDate;
    private long mGoodThruDate;
    private Storage mLoc;
    private String mBarcode;
    private InputType mInputType;

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
            mExpiryDate = DataToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
            // For all new items, goodThruDate is the same as expiryDate by default
            mGoodThruDate = mExpiryDate;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, rootView);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Keep the current selected position in sync between ViewPager and PageIndicator
                mPageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Hide the fab when the keyboard is opened
        // https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootLayout.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    mFabVoice.hide();
                } else {
                    // keyboard is closed
                    mFabVoice.show();
                }
            }
        });

        final LiveData<Food> data = mViewModel.getSingleFoodById(mItemId, false);
        data.observe(this, new Observer<Food>() {
            @Override
            public void onChanged(@Nullable Food food) {
                populateFields(food);
                // this needs to be done AFTER fields are populated, but also call it when adding
                mFormChangedDetector = getFormChangedDetector();
                // do not make any changes to any fields once UI is loaded. this also solves issue
                // where adding an image resets the image adapter position back to 0
                data.removeObserver(this);
            }
        });

        setTextChangedListeners();
        setClickListeners();

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
     * Helper that pre-populates all fields
     */
    private void populateFields(@Nullable Food food) {
        mFood = food;
        // Only load saved values if NOT adding
        if (!mAddMode && food != null) {
            // Image adapter
            mImageUris = food.getImages();
            mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(), true);
            mPagerAdapter.setImageUris(mImageUris);

            // Dates
            mExpiryDate = DataToolbox.getTimeInMillisStartOfDay(food.getDateExpiry());
            mGoodThruDate = DataToolbox.getTimeInMillisStartOfDay(food.getDateGoodThru());

            // Main layout
            mEtFoodName.setText(food.getFoodName());
            mEtCount.setText(String.valueOf(food.getCount()));

            mLoc = food.getStorageLocation();

            String description = food.getDescription();
            mEtDescription.setText(description);
            hideViewIfEmptyString(description, mIvDescriptionClear);

            // Other info layout
            String brand = food.getBrandName();
            mEtBrand.setText(brand);
            hideViewIfEmptyString(brand, mIvBrandClear);

            String size = food.getSize();
            mEtSize.setText(size);
            hideViewIfEmptyString(size, mIvSizeClear);

            String weight = food.getWeight();
            mEtWeight.setText(weight);
            hideViewIfEmptyString(weight, mIvWeightClear);

            String notes = food.getNotes();
            mEtNotes.setText(notes);
            hideViewIfEmptyString(notes, mIvNotesClear);

            mBarcode = food.getBarcode();
            mInputType = food.getInputType();
        } else {
            // Set ADD_MODE defaults
            mImageUris = new ArrayList<>();
            mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(), true);
            mPagerAdapter.setImageUris(mImageUris);
            showFieldError(true, FieldError.REQUIRED_NAME);
            mEtCount.setText(String.valueOf(DEFAULT_STARTING_COUNT));
            mLoc = DEFAULT_STARTING_STORAGE;
        }

        // Common fields
        mViewPager.setAdapter(mPagerAdapter);
        mEtDateExpiry.setText(DataToolbox.getFieldFormattedDate(mExpiryDate));
        mEtDateGood.setText(DataToolbox.getFieldFormattedDate(mGoodThruDate));
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
                if (charSequence != null && charSequence.length() > 0) {
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

    /**
     * Helper that sets the OnClickListeners
     */
    private void setClickListeners() {
        mFabVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Start speech input
                Toolbox.showToast(mHostActivity, "This will start the speech input!");
            }
        });
        mEtFoodNameError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFieldErrorMessage(FieldError.REQUIRED_NAME);
            }
        });
        mEtDateExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(ExpiryDatePickerDialogFragment.DateType.EXPIRY);
            }
        });
        mEtDateGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(ExpiryDatePickerDialogFragment.DateType.GOOD_THRU);
            }
        });
        mIvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseCount();
            }
        });
        mIvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseCount();
            }
        });
        mEtCountError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentFieldErrors.contains(FieldError.INVALID_COUNT)) {
                    showFieldErrorMessage(FieldError.INVALID_COUNT);
                } else if (mCurrentFieldErrors.contains(FieldError.REQUIRED_COUNT)) {
                    showFieldErrorMessage(FieldError.REQUIRED_COUNT);
                }
            }
        });
        mEtLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationDialog();
            }
        });

        mIvDateGoodClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoodThruDate = mExpiryDate;
                String fieldFormattedDate = DataToolbox.getFieldFormattedDate(mGoodThruDate);
                mEtDateGood.setText(fieldFormattedDate);
                setGoodThruDateViewAttributes();
            }
        });
        mIvDescriptionClear.setOnClickListener(new ClearTextClickListener(mEtDescription));
        mIvBrandClear.setOnClickListener(new ClearTextClickListener(mEtBrand));
        mIvSizeClear.setOnClickListener(new ClearTextClickListener(mEtSize));
        mIvWeightClear.setOnClickListener(new ClearTextClickListener(mEtWeight));
        mIvNotesClear.setOnClickListener(new ClearTextClickListener(mEtNotes));
    }

    /**
     * Helper to show a view if the given {@link String} is not length 0
     *
     * @param string
     * @param view
     */
    private void hideViewIfEmptyString(@Nullable String string, View view) {
        view.setVisibility(string != null && !string.isEmpty() ? View.VISIBLE : View.GONE);
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
                Toolbox.showToast(mHostActivity, getString(R.string.message_item_updated,
                        food.getFoodName()));
            }
            mHostActivity.finish();
        }
    }

    private void deleteItem() {
        mFormChangedDetector.updateCachedFields();
        mViewModel.delete(true, mFood);
        mHostActivity.finish();
    }

    private void discardChanges() {
        mFormChangedDetector.updateCachedFields(); // but won't be saved
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
        String fieldFormattedDate = DataToolbox.getFieldFormattedDate(selectedDate);
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
                editTextView.setTextColor(mHostActivity.getResources().getColor(R.color.textColorError));

                if (!mCurrentFieldErrors.contains(error)) {
                    // Only add error if it's not already being tracked
                    mCurrentFieldErrors.add(error);
                }
            } else {
                errorEditTextView.setError(null);
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
     * Generates a {@link FormChangedDetector} with this fragment's list of edit texts
     *
     * @return
     */
    private FormChangedDetector<TextInputEditText> getFormChangedDetector() {
        List<TextInputEditText> editTexts = new ArrayList<>(
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
        return new FormChangedDetector<>(editTexts, mImageUris);
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
    public void onAddImageButtonSelected() {
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
    public void captureImageFromCamera() {
        if (ContextCompat.checkSelfPermission(mHostActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Always ask (unless user ticked "don't ask again")
            requestPermissions(new String[]{Manifest.permission.CAMERA}, RC_PERMISSIONS_CAMERA);
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
                        mHostActivity, "com.zn.expirytracker.fileprovider", outputFilePath);
                // Indicate where photo output should be saved
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, RC_CAMERA);
            }
        }
    }

    /**
     * Prompts the user to pick an image from storage
     */
    public void getImageFromStorage() {
        if (ContextCompat.checkSelfPermission(mHostActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Always ask (unless user ticked "don't ask again")
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
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
        switch (requestCode) {
            case RC_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera();
                } else {
                    Toolbox.showSnackbarMessage(mRootLayout,
                            "Please enable camera permissions to take photos");
                }
                break;
            case RC_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageFromStorage();
                } else {
                    Toolbox.showSnackbarMessage(mRootLayout,
                            "Please enable storage permissions to pick photos");
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                addImageFromUri(imageUri);
            }
        } else if (requestCode == RC_CAMERA && resultCode == RESULT_OK) {
            if (data != null) {
                // data.getData() returns a THUMBNAIL of the image, and no orientation preservation
                // At this point we had already saved the image into app storage, so all we need
                // to do is to add the path to the Food image list
                addImageFromCamera(mCurrentCameraCapturePath);
            }
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
            File outputFilePath = Toolbox.getBitmapSavingFilePath(mHostActivity,
                    Constants.DEFAULT_FILENAME);
            String inputFilePath = Toolbox.getGalleryUriPath(mHostActivity, uri);
            Toolbox.copyFile(new File(inputFilePath), outputFilePath);
            String path = outputFilePath.getAbsolutePath();
            Timber.d("Saved image path: %s", path);
            mImageUris.add(path);
            mPagerAdapter.notifyDataSetChanged();
            Toolbox.showSnackbarMessage(mRootLayout, "Image added!");
        } catch (IOException e) {
            Timber.e(e, "There was a problem saving the image to internal storage");
        }
    }

    /**
     * Updates the image uri list with the newly captured photo, with the path
     */
    private void addImageFromCamera(String imagePath) {
        mImageUris.add(imagePath);
        mPagerAdapter.notifyDataSetChanged();
        Toolbox.showSnackbarMessage(mRootLayout, "Image added!");
    }

    /**
     * Deletes an image from the food item image list. Only delete from internal storage if it is
     * in the app's bitmap capture folder
     *
     * @param uri
     */
    private void deleteImage(Uri uri) {
        // TODO: Implement
    }

    // endregion
}
