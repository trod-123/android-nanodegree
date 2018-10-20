package com.zn.expirytracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.zn.expirytracker.data.contracts.DatabaseContract;
import com.zn.expirytracker.data.typeconv.InputTypeTypeConverter;
import com.zn.expirytracker.data.typeconv.StorageTypeConverter;
import com.zn.expirytracker.data.typeconv.StringListTypeConverter;

import java.util.List;

@Entity(tableName = DatabaseContract.FOOD_TABLE_NAME)
public class Food {

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @NonNull
    @ColumnInfo(name = DatabaseContract.COLUMN_FOOD_NAME)
    private String foodName;

    @ColumnInfo(name = DatabaseContract.COLUMN_DATE_EXPIRY)
    private long dateExpiry;

    @ColumnInfo(name = DatabaseContract.COLUMN_DATE_GOOD_THRU)
    private long dateGoodThru;

    @ColumnInfo(name = DatabaseContract.COLUMN_COUNT)
    private int count;

    @TypeConverters(StorageTypeConverter.class)
    @ColumnInfo(name = DatabaseContract.COLUMN_STORAGE_LOCATION)
    private Storage storageLocation;

    @ColumnInfo(name = DatabaseContract.COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = DatabaseContract.COLUMN_BRAND_NAME)
    private String brandName;

    @ColumnInfo(name = DatabaseContract.COLUMN_SIZE)
    private String size;

    @ColumnInfo(name = DatabaseContract.COLUMN_WEIGHT)
    private String weight;

    @ColumnInfo(name = DatabaseContract.COLUMN_NOTES)
    private String notes;

    @ColumnInfo(name = DatabaseContract.COLUMN_BARCODE)
    private String barcode;

    @TypeConverters(InputTypeTypeConverter.class)
    @ColumnInfo(name = DatabaseContract.COLUMN_INPUT_TYPE)
    private InputType inputType;

    @TypeConverters(StringListTypeConverter.class)
    @ColumnInfo(name = DatabaseContract.COLUMN_IMAGES)
    private List<String> images;

    @Ignore
    public Food() {
        // Default constructor required for calls to DataSnapshot.getValue(Food.class);
    }

    /**
     * Default {@link Food} constructor used by Room, without an {@code _id} field
     *
     * @param foodName
     * @param dateExpiry
     * @param dateGoodThru
     * @param count
     * @param storageLocation
     * @param description
     * @param brandName
     * @param size
     * @param weight
     * @param notes
     * @param barcode
     * @param inputType
     * @param images
     */
    public Food(@NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                Storage storageLocation, String description, String brandName, String size,
                String weight, String notes, String barcode, InputType inputType,
                List<String> images) {
        this.foodName = foodName;
        this.dateExpiry = dateExpiry;
        this.dateGoodThru = dateGoodThru;
        this.count = count;
        this.storageLocation = storageLocation;
        this.description = description;
        this.brandName = brandName;
        this.size = size;
        this.weight = weight;
        this.notes = notes;
        this.barcode = barcode;
        this.inputType = inputType;
        this.images = images;
    }

    /**
     * Creates a task with a specified id
     * <p>
     * Because Room only expects one constructor per Entity class, annotate with {@code @Ignore}
     * since we only want to use this when editing tasks
     *
     * @param foodName
     * @param dateExpiry
     * @param dateGoodThru
     * @param count
     * @param storageLocation
     * @param description
     * @param brandName
     * @param size
     * @param weight
     * @param notes
     * @param barcode
     * @param inputType
     * @param images
     */
    @Ignore
    public Food(long _id, @NonNull String foodName, long dateExpiry, long dateGoodThru, int count,
                Storage storageLocation, String description, String brandName, String size,
                String weight, String notes, String barcode, InputType inputType,
                List<String> images) {
        this._id = _id;
        this.foodName = foodName;
        this.dateExpiry = dateExpiry;
        this.dateGoodThru = dateGoodThru;
        this.count = count;
        this.storageLocation = storageLocation;
        this.description = description;
        this.brandName = brandName;
        this.size = size;
        this.weight = weight;
        this.notes = notes;
        this.barcode = barcode;
        this.inputType = inputType;
        this.images = images;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @NonNull
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(@NonNull String foodName) {
        this.foodName = foodName;
    }

    public long getDateExpiry() {
        return dateExpiry;
    }

    public void setDateExpiry(long dateExpiry) {
        this.dateExpiry = dateExpiry;
    }

    public long getDateGoodThru() {
        return dateGoodThru;
    }

    public void setDateGoodThru(long dateGoodThru) {
        this.dateGoodThru = dateGoodThru;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Storage getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(Storage storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
