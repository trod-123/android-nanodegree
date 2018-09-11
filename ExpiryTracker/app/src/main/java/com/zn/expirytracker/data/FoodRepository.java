package com.zn.expirytracker.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.FoodDao;

/**
 * Repositories abstract access to multiple data sources, if your app has any. It is a convenience
 * class that handles data operations and liaising updates between the Dao and a Network. Access
 * your database through the repository object, rather than directly, so if you have multiple data
 * sources, this single repository can handle interacting with all of them for you. That being said,
 * this seems like a "Content Provider", doesn't it?
 * <p>
 * It is common to assess whether to update data and fetch fresh data, or to provide cached data
 */
public class FoodRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private FoodDao mFoodDao;

    public FoodRepository(Application application) {
        getDao(application);
    }

    private void getDao(Application application) {
        FoodRoomDb db = FoodRoomDb.getDatabase(application);
        mFoodDao = db.foodDao();
    }

    /**
     * Returns a single {@link Food} by {@code id}, with columns specified by {@code summaryColumns}
     *
     * @param id
     * @param summaryColumns
     * @return
     */
    public LiveData<Food> getSingleFoodById(long id, boolean summaryColumns) {
        return summaryColumns ? mFoodDao.getFoodById_summaryColumns(id) : mFoodDao.getFoodById(id);
    }

    /**
     * Returns all {@link Food} items, with columns specified by {@code summaryColumns}
     * <p>
     * Order by increasing expiration date, so those closer to expiring are
     * shown first.
     *
     * @param summaryColumns
     * @return
     */
    public LiveData<PagedList<Food>> getAllFoods(boolean summaryColumns) {
        return summaryColumns ?
                new LivePagedListBuilder<>(mFoodDao.getAllFoods_summaryColumns(), DEFAULT_PAGE_SIZE)
                        .build() :
                new LivePagedListBuilder<>(mFoodDao.getAllFoods(), DEFAULT_PAGE_SIZE)
                        .build();
    }

    /**
     * Returns all {@link Food} expiring on or before {@code date}, with columns specified by
     * {@code summaryColumns}.
     * <p>
     * Order by increasing expiration date, so those closer to expiring are
     * shown first.
     *
     * @param date
     * @param summaryColumns
     * @return
     */
    public LiveData<PagedList<Food>> getAllFoodsExpiringBeforeDate(long date, boolean summaryColumns) {
        return summaryColumns ?
                new LivePagedListBuilder<>(mFoodDao.getAllFoodExpiringBeforeDate_summaryColumns(date), DEFAULT_PAGE_SIZE)
                        .build() :
                new LivePagedListBuilder<>(mFoodDao.getAllFoodExpiringBeforeDate(date), DEFAULT_PAGE_SIZE)
                        .build();
    }

    /**
     * Inserts a single {@link Food} item into the database
     * @param food
     */
    public void insertFood(Food food) {
        new InsertAsyncTask(mFoodDao).execute(food);
    }

    /**
     * Inserts a list of {@link Food} items into the database
     *
     * @param foods
     */
    public void insertFoods(Food... foods) {
        new InsertAsyncTask(mFoodDao).execute(foods);
    }

    /**
     * Updates a single {@link Food} item in the database
     * @param food
     */
    public void updateFood(Food food) {
        new UpdateAsyncTask(mFoodDao).execute(food);
    }

    /**
     * Updates a list of {@link Food} items in the database
     *
     * @param foods
     */
    public void updateFoods(Food... foods) {
        new UpdateAsyncTask(mFoodDao).execute(foods);
    }

    /**
     * Deletes a single {@link Food} item by {@code id}
     * @param id
     */
    public void deleteFoodById(long id) {
        new DeleteAsyncTask(mFoodDao).execute(id);
    }

    /**
     * Deletes a list of {@link Food} items by {@code ids}
     *
     * @param ids
     */
    public void deleteFoodsByIds(Long... ids) {
        new DeleteAsyncTask(mFoodDao).execute(ids);
    }

    /**
     * Deletes all {@link Food} items in the database
     */
    public void deleteAllFoods() {
        new DeleteAllFoodsAsyncTask(mFoodDao).execute();
    }

    // region AsyncTasks

    /**
     * Use an AsyncTask to properly insert a new {@link Food} into the database
     */
    private static class InsertAsyncTask extends AsyncTask<Food, Void, Void> {
        private FoodDao mAsyncTaskDao;

        InsertAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            mAsyncTaskDao.insert(foods);
            return null;
        }
    }

    /**
     * Use an AsyncTask to properly update an existing {@link Food} in the database
     */
    private static class UpdateAsyncTask extends AsyncTask<Food, Void, Void> {
        private FoodDao mAsyncTaskDao;

        UpdateAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            mAsyncTaskDao.update(foods);
            return null;
        }
    }

    /**
     * Use an AsyncTask to properly delete an existing {@link Food} in the database
     */
    private static class DeleteAsyncTask extends AsyncTask<Long, Void, Void> {
        private FoodDao mAsyncTaskDao;

        DeleteAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            mAsyncTaskDao.delete(ids);
            return null;
        }
    }

    /**
     * Use an AsyncTask to properly delete all {@link Food} items in the database
     */
    private static class DeleteAllFoodsAsyncTask extends AsyncTask<Void, Void, Void> {
        private FoodDao mAsyncTaskDao;

        DeleteAllFoodsAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    // endregion
}
