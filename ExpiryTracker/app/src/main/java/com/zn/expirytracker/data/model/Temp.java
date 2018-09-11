package com.zn.expirytracker.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(tableName = DatabaseContract.TEMP_TABLE_NAME)
public class Temp extends Food {

    public Temp(@NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                Storage storageLocation, String description, String brandName, String size,
                String weight, String notes, String barcode, InputType inputType,
                List<String> images) {
        super(foodName, dateExpiry, dateGoodThru, count, storageLocation, description, brandName,
                size, weight, notes, barcode, inputType, images);
    }

    @Ignore
    public Temp(int _id, @NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                Storage storageLocation, String description, String brandName, String size,
                String weight, String notes, String barcode, InputType inputType,
                List<String> images) {
        super(_id, foodName, dateExpiry, dateGoodThru, count, storageLocation, description,
                brandName, size, weight, notes, barcode, inputType, images);
    }
}
