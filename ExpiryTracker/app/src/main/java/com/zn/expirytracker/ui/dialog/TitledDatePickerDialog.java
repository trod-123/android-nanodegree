package com.zn.expirytracker.ui.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * Custom {@link DatePickerDialog} with a title
 */
public class TitledDatePickerDialog extends DatePickerDialog {

    private CharSequence title;

    public TitledDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(title);
    }
}
