package com.zn.expirytracker.data.typeconv;

import android.arch.persistence.room.TypeConverter;

import com.zn.expirytracker.data.model.InputType;

public class InputTypeTypeConverter {

    @TypeConverter
    public static int fromInputType(InputType inputType) {
        return inputType.getCode();
    }

    @TypeConverter
    public static InputType fromInt(int code) {
        if (code == InputType.BARCODE.getCode()) {
            return InputType.BARCODE;
        } else if (code == InputType.IMG_REC.getCode()) {
            return InputType.IMG_REC;
        } else if (code == InputType.IMG_ONLY.getCode()) {
            return InputType.IMG_ONLY;
        } else if (code == InputType.TEXT_ONLY.getCode()) {
            return InputType.TEXT_ONLY;
        } else if (code == InputType.NONE.getCode()) {
            return InputType.NONE;
        } else {
            throw new IllegalArgumentException("Could not recognize InputType passed. Code:" + code);
        }
    }
}
