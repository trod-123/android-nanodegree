package com.zn.expirytracker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.rd.PageIndicatorView;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.ui.dialog.ExpiryDatePickerDialog;
import com.zn.expirytracker.ui.dialog.StorageLocationDialog;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Callbacks for {@link android.support.v4.app.DialogFragment} idea from: https://gist.github.com/Joev-/5695813
 */
public class EditFragment extends Fragment implements
        StorageLocationDialog.OnStorageLocationSelectedListener,
        ExpiryDatePickerDialog.OnDateSelectedListener {

    public static final String ARG_ITEM_POSITION_INT = Toolbox.createStaticKeyString(
            "edit_fragment.item_position_int");

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
    private DateTime mCurrentDateTimeStartOfDay;
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
        mCurrentDateTimeStartOfDay = DataToolbox.getDateTimeStartOfDay(System.currentTimeMillis());

        Bundle args = getArguments();
        if (args != null) {
            // The position will determine the populated elements
            mItemPosition = args.getInt(ARG_ITEM_POSITION_INT, 0);
        }

        // TODO: By default, dates are saved at 00:00, but for debugging this is not guaranteed
        // from the generated data.
        mExpiryDate = DataToolbox
                .getTimeInMillisStartOfDay(mDataGenerator.getExpiryDateAt(mItemPosition));
        // By default, goodThruDate is the same as expiryDate
        mGoodThruDate = mExpiryDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_edit, container, false);
        Timber.tag(EditFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        populateFields();
        setTextChangedListeners();
        setClickListeners();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                deleteItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
     * Helper that pre-populates all fields
     */
    private void populateFields() {
        // Image pager
        // TODO: Since the exact same pager is used between Detail and Edit, try finding a way to
        // "extract" all of this outside
        mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(),
                mDataGenerator.getColors());
        mViewPager.setAdapter(mPagerAdapter);
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

        // Main layout
        mEtFoodName.setText(mDataGenerator.getFoodNameAt(mItemPosition));

        mEtDateExpiry.setText(DataToolbox.getFieldFormattedDate(mExpiryDate));

        mEtDateGood.setText(DataToolbox.getFieldFormattedDate(mGoodThruDate));
        setGoodThruDateViewAttributes();

        mEtCount.setText(String.valueOf(mDataGenerator.getCountAt(mItemPosition)));

        mIvLoc.setImageResource(DataToolbox.getStorageIconResource(
                mDataGenerator.getStorageLocAt(mItemPosition)));
        mEtLoc.setText(DataToolbox.getStorageIconString
                (mDataGenerator.getStorageLocAt(mItemPosition), mHostActivity));

        String description = "The current item position is: " + mItemPosition;
        mEtDescription.setText(description);
        showViewBasedOnString(description, mIvDescriptionClear);

        // Other info layout
        String brand = "Kellogg's";
        mEtBrand.setText(brand);
        showViewBasedOnString(brand, mIvBrandClear);

        String size = "12\" x 10\"";
        mEtSize.setText(size);
        showViewBasedOnString(size, mIvSizeClear);

        String weight = "200 lbs";
        mEtWeight.setText(weight);
        showViewBasedOnString(weight, mIvWeightClear);

        String notes = "What is 1 plus " + mItemPosition;
        mEtNotes.setText(notes);
        showViewBasedOnString(notes, mIvNotesClear);

    }

    /**
     * Helper to show a view if the given {@link String} is not length 0
     *
     * @param string
     * @param view
     */
    private void showViewBasedOnString(String string, View view) {
        view.setVisibility(string != null && string.length() > 0 ? View.VISIBLE : View.GONE);
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
            Toolbox.showToast(mHostActivity, "This will save the current item!");
        }
    }

    private void deleteItem() {
        // TODO: Implement
        Toolbox.showToast(mHostActivity, "This will delete the current item!");
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
            showViewBasedOnString(charSequence.toString(), mView);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

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
}
