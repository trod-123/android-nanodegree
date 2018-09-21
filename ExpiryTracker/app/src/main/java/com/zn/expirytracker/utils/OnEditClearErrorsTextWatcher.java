package com.zn.expirytracker.utils;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Custom {@link TextWatcher} that clears errors for {@link TextInputLayout}s after the calling
 * EditText has changed text
 */
public class OnEditClearErrorsTextWatcher implements TextWatcher {
    private TextInputLayout mTil;

    public OnEditClearErrorsTextWatcher(TextInputLayout til) {
        mTil = til;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTil.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
