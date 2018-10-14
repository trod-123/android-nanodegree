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
    private List<T> mEditTextsReference; // store the references
    private List<String> mCachedStrings; // cache current values
    private List<String> mStringListReference; // for image uris
    private List<String> mCachedStringList;

    public FormChangedDetector(@NonNull List<T> editTexts, @Nullable List<String> stringList) {
        mEditTextsReference = editTexts;
        mCachedStrings = EditToolbox.getStringsFromEditTexts(editTexts);
        if (stringList != null) {
            mStringListReference = stringList;
            mCachedStringList = copyListContents(mStringListReference);
        }
    }

    public List<String> getCachedEditTextStrings() {
        return mCachedStrings;
    }

    public List<String> getCachedStringsList() {
        return mCachedStringList;
    }

    /**
     * Restores the form changed detector with existing lists
     *
     * @param editTextsReference
     * @param cachedStrings
     * @param stringListReference
     * @param cachedStringList
     */
    public FormChangedDetector(@NonNull List<T> editTextsReference,
                               @Nullable List<String> stringListReference,
                               List<String> cachedStrings, List<String> cachedStringList) {
        mEditTextsReference = editTextsReference;
        mStringListReference = stringListReference;
        mCachedStrings = cachedStrings;
        mCachedStringList = cachedStringList;
    }

    private List<String> copyListContents(List<String> list) {
        List<String> newList = new ArrayList<>(list.size());
        for (String object : list) {
            newList.add(object);
        }
        return newList;
    }

    /**
     * Compares the current strings of the {@link EditText} references with this instance's cached
     * strings
     *
     * @return {@code true} if any {@link EditText} strings are different
     */
    public boolean haveFieldsChanged() {
        // Check the Edit Texts
        List<String> updatedStrings = EditToolbox.getStringsFromEditTexts(mEditTextsReference);
        for (int i = 0; i < mCachedStrings.size(); i++) {
            String cached = mCachedStrings.get(i);
            String updated = updatedStrings.get(i);
            if (!cached.equals(updated)) {
                return true;
            }
        }

        // Check the Strings list
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
