package com.zn.expirytracker.data.model;

public enum Storage {
    FRIDGE(0), FREEZER(1), PANTRY(2), COUNTER(3), CUSTOM(4), NOT_SET(-1);

    // Below code (and parenthesis indexing above) allows Storage to function as a TypeConverter
    // for Room
    // https://stackoverflow.com/questions/44498616/android-architecture-components-using-enums
    private int code;

    Storage(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
