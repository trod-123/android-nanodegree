package com.zn.expirytracker.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FoodDao {

    /**
     * Inserts a single {@link Food} to the database. Replaces the food item if it already exists.
     *
     * @param food
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Food food);

    /**
     * Inserts an array of {@link Food} items to the database. Replaces any food item if it already
     * exists.
     *
     * @param foods
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insert(Food... foods);

    /**
     * Updates a single {@link Food} item in the database
     *
     * @param food
     */
    @Update
    void update(Food food);

    /**
     * Updates a {@link List} of {@link Food} items in the database
     *
     * @param foods
     */
    @Update
    void update(Food... foods);

    /**
     * Deletes all {@link Food} from the food table
     */
    @Query("DELETE FROM food_table")
    void deleteAll();

    /**
     * Deletes the {@link Food} item specified by the provided {@code id}
     *
     * @param id
     */
    @Query("DELETE FROM food_table WHERE _id is :id")
    void delete(long id);

    /**
     * Deletes the {@link Food} items specified by the provided {@code ids}
     *
     * @param ids
     */
    @Query("DELETE from food_table WHERE _id in (:ids) ")
    void delete(Long... ids);

    /**
     * Returns a single {@link Food} object that matches the provided {@code id}, providing only the
     * summary columns (all other values will be null)
     *
     * @param id
     * @return
     */
    @Query("SELECT _id, food_name, date_expiry, date_good_thru, count, storage_location, images" +
            " FROM food_table WHERE _id is :id")
    LiveData<Food> getFoodById_summaryColumns(long id);

    /**
     * Returns a single {@link Food} object that matches the provided {@code id}. All columns
     * provided
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM food_table WHERE _id is :id")
    LiveData<Food> getFoodById(long id);

    /**
     * Returns a list of all {@link Food} objects stored in the database.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are shown first, providing
     * only the summary columns (all other values will be null)
     *
     * @return
     */
    @Query("SELECT _id, food_name, date_expiry, date_good_thru, count, storage_location, images" +
            " FROM food_table ORDER BY date_expiry, food_name ASC")
    DataSource.Factory<Integer, Food> getAllFoods_summaryColumns();

    /**
     * Returns a list of all {@link Food} objects stored in the database.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are shown first. All columns
     * provided
     *
     * @return
     */
    @Query("SELECT * FROM food_table ORDER BY date_expiry, food_name ASC")
    DataSource.Factory<Integer, Food> getAllFoods();

    /**
     * Returns a list of {@link Food} filtered to those expiring on or before the provided
     * {@code date}.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are shown first, providing
     * only the summary columns (all other values will be null)
     *
     * @param date
     * @return
     */
    @Query("SELECT _id, food_name, date_expiry, date_good_thru, count, storage_location, images" +
            " FROM food_table WHERE date_expiry <= :date ORDER BY date_expiry, food_name ASC")
    DataSource.Factory<Integer, Food> getAllFoodExpiringBeforeDate_summaryColumns(long date);

    /**
     * Returns a list of {@link Food} filtered to those expiring on or before the provided
     * {@code date}.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are shown first. All columns
     * provided
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM food_table WHERE date_expiry <= :date " +
            "ORDER BY date_expiry, food_name ASC")
    DataSource.Factory<Integer, Food> getAllFoodExpiringBeforeDate(long date);

    /**
     * Returns a list of {@link Food} filtered to those expiring on or before the provided
     * {@code date}.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are shown first, providing
     * only the summary columns (all other values will be null)
     *
     * @param date
     * @return
     */
    @Query("SELECT _id, food_name, date_expiry, date_good_thru, count, storage_location, images" +
            " FROM food_table WHERE date_expiry <= :date ORDER BY date_expiry, food_name ASC")
    List<Food> getAllFoodExpiringBeforeDate_Widget(long date);

    /**
     * Returns a random single {@link Food} item. If there are no {@link Food} in the database,
     * returns an empty array
     *
     * @return
     */
    @Query("SELECT * FROM food_table LIMIT 1")
    Food[] getAnyFood();
}
