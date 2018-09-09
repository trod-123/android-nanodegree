package com.zn.expirytracker.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.ui.dialog.ConfirmDeleteDialog;
import com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialog;
import com.zn.expirytracker.ui.dialog.FormChangedDialog;
import com.zn.expirytracker.ui.dialog.StorageLocationDialog;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.FormChangedDetector;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Callbacks for {@link android.support.v4.app.DialogFragment} idea from: https://gist.github.com/Joev-/5695813
 */
public class EditFragment extends Fragment implements
        StorageLocationDialog.OnStorageLocationSelectedListener,
        ExpiryDatePickerDialog.OnDateSelectedListener,
        FormChangedDialog.OnFormChangedButtonClickListener,
        ConfirmDeleteDialog.OnConfirmDeleteButtonClickListener {

    public static final String ARG_ITEM_POSITION_INT = Toolbox.createStaticKeyString(
            "edit_fragment.item_position_int");
    public static final int POSITION_ADD_MODE = -1024; // pass this as the position to enable add

    public static final int DEFAULT_STARTING_COUNT = 1;
    public static final Storage DEFAULT_STARTING_STORAGE = Storage.FRIDGE;

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
    private TestDataGen mDataGenerator;
    private FormChangedDetector mFormChangedDetector;
    private int mItemPosition;

    private long mExpiryDate;
    private long mGoodThruDate;

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(int itemPosition) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_POSITION_INT, itemPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mHostActivity = getActivity();
        mDataGenerator = TestDataGen.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            // The position will determine the populated elements
            mItemPosition = args.getInt(ARG_ITEM_POSITION_INT, 0);
        }

        // Set the dates
        if (mItemPosition != POSITION_ADD_MODE) {
            // TODO: By default, dates are saved at 00:00, but for debugging this is not guaranteed
            // from the generated data.
            mExpiryDate = DataToolbox
                    .getTimeInMillisStartOfDay(mDataGenerator.getExpiryDateAt(mItemPosition));
        } else {
            // By default, load up the current day as the initial expiry date
            mExpiryDate = DataToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
        }
        // TODO: By default, goodThruDate is the same as expiryDate. But once real data comes in, do
        // NOT do this
        mGoodThruDate = mExpiryDate;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_edit, container, false);
        Timber.tag(EditFragment.class.getSimpleName());
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

        populateFields(mItemPosition);
        setTextChangedListeners();
        setClickListeners();

        // this needs to be done AFTER fields are populated
        mFormChangedDetector = getFormChangedDetector();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mItemPosition == POSITION_ADD_MODE) {
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
     * Helper that pre-populates all fields. Does not pre-populate if
     * {@code itemPosition == POSITION_ADD_MODE}
     */
    private void populateFields(int itemPosition) {
        // Only load from database if in ADD_MODE
        if (itemPosition != POSITION_ADD_MODE) {
            // Image adapter
            mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(),
                    mDataGenerator.getColors());

            // Main layout
            mEtFoodName.setText(mDataGenerator.getFoodNameAt(itemPosition));
            mEtCount.setText(String.valueOf(mDataGenerator.getCountAt(itemPosition)));

            mIvLoc.setImageResource(DataToolbox.getStorageIconResource(
                    mDataGenerator.getStorageLocAt(itemPosition)));
            mEtLoc.setText(DataToolbox.getStorageIconString
                    (mDataGenerator.getStorageLocAt(itemPosition), mHostActivity));

            String description = "The current item position is: " + itemPosition;
            mEtDescription.setText(description);
            hideViewIfEmptyString(description, mIvDescriptionClear);

            // Other info layout
            String brand = "Kellogg's";
            mEtBrand.setText(brand);
            hideViewIfEmptyString(brand, mIvBrandClear);

            String size = "12\" x 10\"";
            mEtSize.setText(size);
            hideViewIfEmptyString(size, mIvSizeClear);

            String weight = "200 lbs";
            mEtWeight.setText(weight);
            hideViewIfEmptyString(weight, mIvWeightClear);

            String notes = "What is 1 plus " + itemPosition;
            mEtNotes.setText(notes);
            hideViewIfEmptyString(notes, mIvNotesClear);
        } else {
            // Set ADD_MODE defaults
            mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(), new int[]{});
            showFieldError(true, FieldError.REQUIRED_NAME);
            mEtCount.setText(String.valueOf(DEFAULT_STARTING_COUNT));
            mIvLoc.setImageResource(DataToolbox.getStorageIconResource(
                    DEFAULT_STARTING_STORAGE));
            mEtLoc.setText(DataToolbox.getStorageIconString
                    (DEFAULT_STARTING_STORAGE, mHostActivity));
        }

        // Common fields
        mViewPager.setAdapter(mPagerAdapter);
        mEtDateExpiry.setText(DataToolbox.getFieldFormattedDate(mExpiryDate));
        mEtDateGood.setText(DataToolbox.getFieldFormattedDate(mGoodThruDate));
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
                showDatePickerDialog(ExpiryDatePickerDialog.DateType.EXPIRY);
            }
        });
        mEtDateGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(ExpiryDatePickerDialog.DateType.GOOD_THRU);
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
            // TODO: Implement
            mFormChangedDetector.updateCachedFields();
            Toolbox.showToast(mHostActivity, "This will save the current item!");
            mHostActivity.onBackPressed();
        }
    }

    private void deleteItem() {
        // TODO: Implement
        mFormChangedDetector.updateCachedFields();
        Toolbox.showToast(mHostActivity, "This will delete the current item!");
        mHostActivity.onBackPressed();
    }

    private void discardChanges() {
        mFormChangedDetector.updateCachedFields(); // but won't be saved
        mHostActivity.onBackPressed();
    }

    // region Dialogs

    /**
     * Prompts the user to select a location
     * https://developer.android.com/guide/topics/ui/dialogs
     */
    private void showLocationDialog() {
        StorageLocationDialog dialog = new StorageLocationDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), StorageLocationDialog.class.getSimpleName());
    }

    /**
     * Prompts the user to select a date. Resulting action is based on the
     * {@link com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialog.DateType} passed
     *
     * @param dateType
     */
    private void showDatePickerDialog(ExpiryDatePickerDialog.DateType dateType) {
        ExpiryDatePickerDialog dialog = ExpiryDatePickerDialog.newInstance(dateType, mExpiryDate,
                mGoodThruDate);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ExpiryDatePickerDialog.class.getSimpleName());
    }

    /**
     * Prompts the user to either discard or save changes
     */
    private void showFormChangedDialog() {
        FormChangedDialog dialog = new FormChangedDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), FormChangedDialog.class.getSimpleName());
    }

    /**
     * Prompts the user to confirm whether the current item should be deleted
     */
    private void showConfirmDeleteDialog() {
        ConfirmDeleteDialog dialog = ConfirmDeleteDialog.newInstance(
                mDataGenerator.getFoodNameAt(mItemPosition), AuthToolbox.checkIfSignedIn());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ConfirmDeleteDialog.class.getSimpleName());
    }

    /**
     * Handles the {@link Storage} selection from  {@link StorageLocationDialog}
     *
     * @param position
     */
    @Override
    public void onStorageLocationSelected(int position) {
        Storage loc;
        switch (position) {
            case 0:
                loc = Storage.FRIDGE;
                break;
            case 1:
                loc = Storage.FREEZER;
                break;
            case 2:
                loc = Storage.PANTRY;
                break;
            case 3:
                loc = Storage.COUNTER;
                break;
            case 4:
            default:
                loc = Storage.CUSTOM;
        }

        // TODO: Handle rotation changes not updating text properly (open dialog, rotate, choose, value does not change from original)
        mIvLoc.setImageResource(DataToolbox.getStorageIconResource(loc));
        mEtLoc.setText(DataToolbox.getStorageIconString(loc, mHostActivity));
    }

    /**
     * Handles the date selection from {@link ExpiryDatePickerDialog}
     *
     * @param dateType
     * @param selectedDate
     */
    @Override
    public void onDateSelected(ExpiryDatePickerDialog.DateType dateType, DateTime selectedDate) {
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
     * Handles the button clicked from {@link FormChangedDialog}
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
     * Handles the button clicked from {@link ConfirmDeleteDialog}
     *
     * @param position
     */
    @Override
    public void onConfirmDeleteButtonClicked(int position) {
        switch (position) {
            case AlertDialog.BUTTON_POSITIVE: // delete
                deleteItem();
                break;
        }
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
                    errorMessage = getString(R.string.edit_error_required);
                    break;
                case REQUIRED_COUNT:
                    editTextView = mEtCount;
                    errorEditTextView = mEtCountError;
                    errorMessage = getString(R.string.edit_error_required);
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
                Toolbox.showSnackbarMessage(getView(), getString(R.string.edit_error_required));
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
        return new FormChangedDetector<>(editTexts);
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
}
