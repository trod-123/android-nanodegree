package com.zn.expirytracker.data.model;

public enum InputType {
    BARCODE(0), IMG_REC(1), IMG_ONLY(2), TEXT_ONLY(3);

    // Below code (and parenthesis indexing above) allows Storage to function as a TypeConverter
    // for Room
    // https://stackoverflow.com/questions/44498616/android-architecture-components-using-enums
    private int code;

    InputType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
