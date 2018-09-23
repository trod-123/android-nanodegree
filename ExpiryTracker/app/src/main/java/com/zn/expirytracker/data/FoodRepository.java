package com.zn.expirytracker.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.AsyncTask;

import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.FoodDao;
import com.zn.expirytracker.widget.UpdateWidgetService;

import java.util.List;

/**
 * Repositories abstract access to multiple data sources, if your app has any. It is a convenience
 * class that handles data operations and liaising updates between the Dao and a Network. Access
 * your database through the repository object, rather than directly, so if you have multiple data
 * sources, this single repository can handle interacting with all of them for you. That being said,
 * this seems like a "Content Provider", doesn't it?
 * <p>
 * It is common to assess whether to update data and fetch fresh data, or to provide cached data
 * <p>
 * This repository links the following data sources:
 * <ul>
 * <li>Internal room database
 * <li>Firebase Realtime Database
 * </ul>
 */
public class FoodRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private FoodDao mFoodDao;
    private Context mContext;

    public FoodRepository(Application application) {
        getDao(application);
        mContext = application;
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
     * Returns all {@link Food} items, with columns specified by {@code summaryColumns}. The list
     * returned is not {@link LiveData}
     * <p>
     * Order by increasing expiration date, so those closer to expiring are
     * shown first.
     *
     * @return
     */
    public List<Food> getAllFoods_List() {
        return mFoodDao.getAllFoods_List();
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
     *
     * @param food
     */
    public void insertFood(boolean saveToCloud, Food food) {
        new InsertAsyncTask(mFoodDao, saveToCloud, food).execute(mContext);
    }

    /**
     * Inserts a list of {@link Food} items into the database
     *
     * @param foods
     */
    public void insertFoods(boolean saveToCloud, Food... foods) {
        new InsertAsyncTask(mFoodDao, saveToCloud, foods).execute(mContext);
    }

    /**
     * Updates a single {@link Food} item in the database
     *
     * @param food
     */
    public void updateFood(boolean saveToCloud, Food food) {
        new UpdateAsyncTask(mFoodDao, food).execute(mContext);
        if (saveToCloud) {
            // TODO: Once deleteImage() is implemented, need to see which images are deleted so
            // they can be deleted from FBS too
            FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food);
        }
    }

    /**
     * Updates a list of {@link Food} items in the database
     *
     * @param foods
     */
    public void updateFoods(boolean saveToCloud, Food... foods) {
        new UpdateAsyncTask(mFoodDao, foods).execute(mContext);
        if (saveToCloud) {
            for (Food food : foods) {
                // TODO: Once deleteImage() is implemented, need to see which images are deleted so
                // they can be deleted from FBS too
                FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food);
            }
        }
    }

    /**
     * Deletes the passed {@link Food} item
     *
     * @param wipeCloudStorage
     * @param food
     */
    public void deleteFood(boolean wipeCloudStorage, Food food) {
        new DeleteAsyncTask(mFoodDao, food).execute(mContext);
        if (wipeCloudStorage) {
            FirebaseDatabaseHelper.delete(food.get_id());
            FirebaseStorageHelper.delete(food);
        }
    }

    /**
     * Deletes the passed list of {@link Food} items
     *
     * @param wipeCloudStorage
     * @param foods
     */
    public void deleteFoods(boolean wipeCloudStorage, Food... foods) {
        new DeleteAsyncTask(mFoodDao, foods).execute(mContext);
        if (wipeCloudStorage) {
            for (Food food : foods) {
                FirebaseDatabaseHelper.delete(food.get_id());
                FirebaseStorageHelper.delete(food);
            }
        }
    }

    // NOTE: Since Firebase Storage does not support deleting directories (e.g. directories
    // titled by food_id), we will need to implement deletion on a Food rather than _id level


//    /**
//     * Deletes a single {@link Food} item by {@code id}
//     *
//     * @param id
//     */
//    public void deleteFoodById(boolean wipeCloudStorage, long id) {
//        new DeleteAsyncTask(mFoodDao, id).execute(mContext);
//        if (wipeCloudStorage) {
//            FirebaseDatabaseHelper.delete(id);
//        }
//    }

//    /**
//     * Deletes a list of {@link Food} items by {@code ids}
//     *
//     * @param ids
//     */
//    public void deleteFoodsByIds(boolean wipeCloudStorage, Long... ids) {
//        new DeleteAsyncTask(mFoodDao, ids).execute(mContext);
//        if (wipeCloudStorage) {
//            for (long id : ids) {
//                FirebaseDatabaseHelper.delete(id);
//            }
//        }
//    }

//    /**
//     * Deletes all {@link Food} items in the database
//     */
//    public void deleteAllFoods(boolean wipeCloudStorage) {
//        new DeleteAllFoodsAsyncTask(mFoodDao).execute(mContext);
//        if (wipeCloudStorage) {
//            FirebaseDatabaseHelper.deleteAll();
//        }
//    }

    // region AsyncTasks

    /**
     * Use an AsyncTask to properly insert a new {@link Food} into the database. Also sets the
     * id of the newly inserted food so we can push to Firebase RTD
     */
    private static class InsertAsyncTask extends AsyncTask<Context, Void, Long[]> {
        private FoodDao mAsyncTaskDao;
        private boolean mSaveToCloud;
        private Food[] mFoods;

        InsertAsyncTask(FoodDao foodDao, boolean saveToCloud, Food... foods) {
            mAsyncTaskDao = foodDao;
            mSaveToCloud = saveToCloud;
            mFoods = foods;
        }

        @Override
        protected Long[] doInBackground(Context... contexts) {
            Long[] insertedIds = mAsyncTaskDao.insert(mFoods);
            // Update the widget
            UpdateWidgetService.updateFoodWidget(contexts[0]);
            return insertedIds;
        }

        @Override
        protected void onPostExecute(Long[] ids) {
            if (mSaveToCloud) {
                for (int i = 0; i < mFoods.length; i++) {
                    Food food = mFoods[i];
                    food.set_id(ids[i]);
                    // This uploads the images first before saving to RTD
                    FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food);
                }
            }
        }
    }

    /**
     * Use an AsyncTask to properly update an existing {@link Food} in the database
     */
    private static class UpdateAsyncTask extends AsyncTask<Context, Void, Void> {
        private FoodDao mAsyncTaskDao;
        private Food[] mFoods;

        UpdateAsyncTask(FoodDao foodDao, Food... foods) {
            mAsyncTaskDao = foodDao;
            mFoods = foods;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            mAsyncTaskDao.update(mFoods);
            // Update the widget
            UpdateWidgetService.updateFoodWidget(contexts[0]);
            return null;
        }
    }

    /**
     * Use an AsyncTask to properly delete an existing {@link Food} in the database
     */
    private static class DeleteAsyncTask extends AsyncTask<Context, Void, Void> {
        private FoodDao mAsyncTaskDao;
        private Long[] mIds;
        private Food[] mFoods;

        /**
         * Delete a food via passing its ids
         *
         * @param foodDao
         * @param ids
         */
        DeleteAsyncTask(FoodDao foodDao, Long... ids) {
            mAsyncTaskDao = foodDao;
            mIds = ids;
        }

        /**
         * Delete a food via passing the foods themselves
         *
         * @param foodDao
         * @param foods
         */
        DeleteAsyncTask(FoodDao foodDao, Food... foods) {
            mAsyncTaskDao = foodDao;
            mFoods = foods;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            if (mIds != null) {
                mAsyncTaskDao.delete(mIds);
            } else if (mFoods != null) {
                mAsyncTaskDao.delete(mFoods);
            }
            // Update the widget
            UpdateWidgetService.updateFoodWidget(contexts[0]);
            return null;
        }
    }

    /**
     * Use an AsyncTask to properly delete all {@link Food} items in the database
     */
    private static class DeleteAllFoodsAsyncTask extends AsyncTask<Context, Void, Void> {
        private FoodDao mAsyncTaskDao;

        DeleteAllFoodsAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            mAsyncTaskDao.deleteAll();
            // Update the widget
            UpdateWidgetService.updateFoodWidget(contexts[0]);
            return null;
        }
    }

    // endregion
}
