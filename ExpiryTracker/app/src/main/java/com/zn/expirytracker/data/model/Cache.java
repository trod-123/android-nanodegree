package com.zn.expirytracker.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.zn.expirytracker.data.contracts.DatabaseContract;

import java.util.List;

@Entity(tableName = DatabaseContract.CACHE_TABLE_NAME)
public class Cache extends Food {

    public Cache(@NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                 Storage storageLocation, String description, String brandName, String size,
                 String weight, String notes, String barcode, InputType inputType, List<String> images) {
        super(foodName, dateExpiry, dateGoodThru, count, storageLocation, description, brandName,
                size, weight, notes, barcode, inputType, images);
    }

    @Ignore
    public Cache(int _id, @NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                 Storage storageLocation, String description, String brandName, String size,
                 String weight, String notes, String barcode, InputType inputType,
                 List<String> images) {
        super(_id, foodName, dateExpiry, dateGoodThru, count, storageLocation, description,
                brandName, size, weight, notes, barcode, inputType, images);
    }
}
