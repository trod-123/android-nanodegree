package com.zn.expirytracker.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects whether changes have been made to a form by keeping a list of {@link EditText} references
 */
public class FormChangedDetector<T extends EditText> {
    private List<T> mEditTextsReference; // store the references, and cache their current values
    private List<String> mCachedStrings;
    private List<String> mStringListReference;
    private List<String> mCachedStringList;

    public FormChangedDetector(@NonNull List<T> editTexts, @Nullable List<String> stringList) {
        mEditTextsReference = editTexts;
        mCachedStrings = EditToolbox.getStringsFromEditTexts(editTexts);
        if (stringList != null) {
            mStringListReference = stringList;
            mCachedStringList = copyListContents(mStringListReference);
        }
    }

    private List<String> copyListContents(List<String> list) {
        List<String> newList = new ArrayList<>(list.size());
        for (String object : list) {
            newList.add(object);
        }

        // TODO: Test if this works after having implemented delete functionality

//        newList.addAll(list);
        return newList;
    }

    /**
     * Compares the current strings of the {@link EditText} references with this instance's cached
     * strings
     *
     * @return {@code true} if any {@link EditText} strings are different
     */
    public boolean haveFieldsChanged() {
        List<String> updatedStrings = EditToolbox.getStringsFromEditTexts(mEditTextsReference);
        for (int i = 0; i < mCachedStrings.size(); i++) {
            String cached = mCachedStrings.get(i);
            String updated = updatedStrings.get(i);
            if (!cached.equals(updated)) {
                return true;
            }
        }
        if (mCachedStringList != null && mStringListReference != null) {
            // Check size first to see if necessary to iterate through individual items
            if (mCachedStringList.size() != mStringListReference.size()) {
                return true;
            }
            // Size is the same, so check contents
            for (int i = 0; i < mCachedStringList.size(); i++) {
                String cached = mCachedStringList.get(i);
                String updated = mStringListReference.get(i);
                if (!cached.equals(updated)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates the list of cached strings
     */
    public void updateCachedFields() {
        mCachedStrings = EditToolbox.getStringsFromEditTexts(mEditTextsReference);
        mCachedStringList = copyListContents(mStringListReference);
    }
}
