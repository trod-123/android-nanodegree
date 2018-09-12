package com.zn.expirytracker.utils;

import android.widget.EditText;

import java.util.List;

/**
 * Detects whether changes have been made to a form by keeping a list of {@link EditText} references
 */
public class FormChangedDetector<T extends EditText> {
    private List<T> mEditTexts;
    private List<String> mCachedStrings;

    public FormChangedDetector(List<T> editTexts) {
        mEditTexts = editTexts;
        mCachedStrings = EditToolbox.getStringsFromEditTexts(editTexts);
    }

    /**
     * Compares the current strings of the {@link EditText} references with this instance's cached
     * strings
     *
     * @return {@code true} if any {@link EditText} strings are different
     */
    public boolean haveFieldsChanged() {
        List<String> updatedStrings = EditToolbox.getStringsFromEditTexts(mEditTexts);
        for (int i = 0; i < mCachedStrings.size(); i++) {
            String cached = mCachedStrings.get(i);
            String updated = updatedStrings.get(i);
            if (!cached.equals(updated)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the list of cached strings
     */
    public void updateCachedFields() {
        mCachedStrings = EditToolbox.getStringsFromEditTexts(mEditTexts);
    }
}
