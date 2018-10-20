package com.zn.expirytracker.data.typeconv;

import androidx.room.TypeConverter;

import com.zn.expirytracker.data.model.Storage;

public class StorageTypeConverter {

    @TypeConverter
    public static int fromStorage(Storage storage) {
        return storage.getCode();
    }

    @TypeConverter
    public static Storage fromInt(int code) {
        if (code == Storage.FRIDGE.getCode()) {
            return Storage.FRIDGE;
        } else if (code == Storage.FREEZER.getCode()) {
            return Storage.FREEZER;
        } else if (code == Storage.PANTRY.getCode()) {
            return Storage.PANTRY;
        } else if (code == Storage.COUNTER.getCode()) {
            return Storage.COUNTER;
        } else if (code == Storage.CUSTOM.getCode()) {
            return Storage.CUSTOM;
        } else if (code == Storage.NOT_SET.getCode()) {
            return Storage.NOT_SET;
        } else {
            throw new IllegalArgumentException("Could not recognize Storage passed. Code:" + code);
        }
    }
}
