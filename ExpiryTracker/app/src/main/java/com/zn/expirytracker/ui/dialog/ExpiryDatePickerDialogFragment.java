package com.zn.expirytracker.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.DateToolbox;
import com.zn.expirytracker.utils.DebugFields;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

/**
 * {@link DialogFragment} that prompts for the expiry dates of an item
 */
public class ExpiryDatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_DATE_TYPE = Toolbox.createStaticKeyString(
            "expiry_date_picker_dialog.date_type");
    private static final String ARG_CURRENT_DATE = Toolbox.createStaticKeyString(
            "expiry_date_picker_dialog.current_date");
    private static final String ARG_GOOD_THRU_DATE = Toolbox.createStaticKeyString(
            "expiry_date_picker_dialog.good_thru_date");
    private static final String ARG_REPROMPT_BOOLEAN =
            Toolbox.createStaticKeyString("expiry_date_picker_dialog.reprompt");

    public enum DateType {
        EXPIRY, GOOD_THRU;
    }

    private OnDateSelectedListener mCallback;
    private OnDialogCancelListener cancelCallback;

    private DateType mDateType;
    private long mExpiryDate; // only if DateType is GOOD_THRU, the selected date must be >= this date
    private long mGoodThruDate;
    private boolean mReprompt; // for hiding the initial toast prompt

    public interface OnDateSelectedListener {
        void onDateSelected(DateType dateType, DateTime selectedDate);
    }

    /**
     * Creates a new {@link ExpiryDatePickerDialogFragment} with a {@link DateType}
     *
     * @param dateType
     * @param expiryDateStartOfDay
     * @param goodThruDateStartOfDay
     * @return
     */
    public static ExpiryDatePickerDialogFragment newInstance(DateType dateType, long expiryDateStartOfDay,
                                                             long goodThruDateStartOfDay) {
        ExpiryDatePickerDialogFragment fragment = new ExpiryDatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE_TYPE, dateType.toString());
        args.putLong(ARG_CURRENT_DATE, expiryDateStartOfDay);
        args.putLong(ARG_GOOD_THRU_DATE, goodThruDateStartOfDay);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new {@link ExpiryDatePickerDialogFragment} with a {@link DateType}
     *
     * @param dateType
     * @param expiryDateStartOfDay
     * @param goodThruDateStartOfDay
     * @param reprompt               Pass {@code true} to hide the initial Toast prompt
     * @return
     */
    public static ExpiryDatePickerDialogFragment newInstance(DateType dateType, long expiryDateStartOfDay,
                                                             long goodThruDateStartOfDay, boolean reprompt) {
        ExpiryDatePickerDialogFragment fragment = newInstance(dateType, expiryDateStartOfDay, goodThruDateStartOfDay);
        Bundle args = fragment.getArguments();
        args.putBoolean(ARG_REPROMPT_BOOLEAN, reprompt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (OnDateSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement OnDateSelectedListener");
        }
        try {
            cancelCallback = (OnDialogCancelListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnDialogCancelListener");
        }

        Bundle args = getArguments();
        if (args != null) {
            String dateTypeString = args.getString(ARG_DATE_TYPE, DateType.EXPIRY.toString());
            mDateType = DateType.valueOf(dateTypeString);
            mExpiryDate = args.getLong(ARG_CURRENT_DATE,
                    DateToolbox.getDateTimeStartOfDay(System.currentTimeMillis()).getMillis());
            // Set to expiry date by default
            mGoodThruDate = args.getLong(ARG_GOOD_THRU_DATE, mExpiryDate);
            mReprompt = args.getBoolean(ARG_REPROMPT_BOOLEAN, false);

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DateTime dateToDisplay;
        String prompt;
        switch (mDateType) {
            case GOOD_THRU:
                dateToDisplay = new DateTime(mGoodThruDate);
                prompt = getString(R.string.voice_prompt_good_thru);
                break;
            case EXPIRY:
            default:
                dateToDisplay = new DateTime(mExpiryDate);
                prompt = getString(R.string.voice_prompt_expiry);
        }
        int day = dateToDisplay.getDayOfMonth();
        int month = dateToDisplay.getMonthOfYear() - 1; // DateTime starts counting months from 1
        int year = dateToDisplay.getYear();

        TitledDatePickerDialog dialog = new TitledDatePickerDialog(getActivity(), this, year, month, day);
        dialog.setPermanentTitle(getString(R.string.action_set_expiry_date));

        if (!mReprompt) {
            Toolbox.showToast(getContext(), prompt);
        }
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancelCallback.onCancelled(this.getClass(), dialog);
    }

    /**
     * Confirm date is valid before sending to callback
     * <p>
     * 1) Expiry date must be at least the current date or later
     * <p>
     * 2) Good thru date must be at least expiry date or later
     *
     * @param datePicker
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        DateTime selectedDate = new DateTime(year, month + 1, dayOfMonth,
                0, 0);
        long selectedDateInMillis = selectedDate.getMillis();
        long currentTimeInMillis = DateToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis());
        if (!DebugFields.OVERRIDE_EXPIRY_DATE_RULES) {
            if (mDateType == DateType.GOOD_THRU && selectedDateInMillis < mExpiryDate) {
                // Test condition 2 first
                Toolbox.showToast(getContext(), getString(R.string.edit_error_date_good_thru));
                reshowDatePickerDialog();
            } else if (selectedDateInMillis < currentTimeInMillis) {
                // Condition 1
                Toolbox.showToast(getContext(), getString(R.string.edit_error_date_expiry));
                reshowDatePickerDialog();
            } else {
                // Both conditions passed
                mCallback.onDateSelected(mDateType, selectedDate);
            }
        } else {
            mCallback.onDateSelected(mDateType, selectedDate);
        }
    }

    /**
     * Reshow the dialog if user selected date that did not satisfy conditions in
     * {@link ExpiryDatePickerDialogFragment#onDateSet(DatePicker, int, int, int)}
     * <p>
     * From: https://stackoverflow.com/questions/30069406/datepickerdialog-how-to-reopen-it-when-invalid-date-set
     */
    private void reshowDatePickerDialog() {
        ExpiryDatePickerDialogFragment dialog = newInstance(mDateType, mExpiryDate, mGoodThruDate,
                true);
        // needed so calling fragment remains the same
        dialog.setTargetFragment(getTargetFragment(), 0);

        dialog.show(getFragmentManager(), ExpiryDatePickerDialogFragment.class.getSimpleName());
    }


}
