package com.zn.expirytracker.utils;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers for editing Food info
 */
public class EditToolbox {

    /**
     * Returns all strings from the provided {@link List} of {@link EditText} objects, in 1:1 order
     *
     * @param editTexts
     * @return
     */
    public static List<String> getStringsFromEditTexts(List<? extends EditText> editTexts) {
        List<String> values = new ArrayList<>();
        for (EditText editText : editTexts) {
            values.add(editText.getText().toString());
        }
        return values;
    }
}
