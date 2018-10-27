package com.zn.expirytracker.data;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.firebase.FirebaseStorageHelper;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.FoodDao;
import com.zn.expirytracker.ui.widget.UpdateWidgetService;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.Constants;
import com.zn.expirytracker.utils.Toolbox;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import timber.log.Timber;

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
 * <li>Firebase Storage
 * </ul>
 */
public class FoodRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private FoodDao mFoodDao;
    private Context mContext;

    /**
     * Only perform cloud operations if user is currently signed in
     */
    private boolean mIsSignedIn;

    public FoodRepository(Application application) {
        getDao(application);
        mContext = application;

        if (mIsSignedIn = AuthToolbox.isSignedIn())
            FirebaseUpdaterHelper.setFoodChildEventListener(new FoodChildEventListener());
    }

    public void stopListeningForFoodChanges() {
        if (mIsSignedIn) FirebaseUpdaterHelper.listenForFoodChanges(false);
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
     * Uploads all the food currently saved locally into the cloud
     */
    public void uploadAllFoods() {
        new UploadAsyncTask(mFoodDao).execute();
    }

    /**
     * Updates a single {@link Food} item in the database
     *
     * @param food
     */
    public void updateFood(boolean saveToCloud, Food food) {
        new UpdateAsyncTask(mFoodDao, food).execute(mContext);
        if (saveToCloud && mIsSignedIn) {
            FirebaseDatabaseHelper.write(food, mContext, true);
            FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food, mContext);
        }
    }

    /**
     * Updates a list of {@link Food} items in the database
     *
     * @param foods
     */
    public void updateFoods(boolean saveToCloud, Food... foods) {
        new UpdateAsyncTask(mFoodDao, foods).execute(mContext);
        if (saveToCloud && mIsSignedIn) {
            for (Food food : foods) {
                FirebaseDatabaseHelper.write(food, mContext, true);
                FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food, mContext);
            }
        }
    }

    /**
     * Deletes the passed {@link Food} item
     *
     * @param wipeCloudStorage
     * @param food
     */
    public void deleteFood(boolean wipeCloudStorage, boolean wipeCloudImages, Food food) {
        new DeleteAsyncTask(mFoodDao, food).execute(mContext);
        if (wipeCloudStorage && mIsSignedIn) {
            FirebaseDatabaseHelper.delete(food.get_id(), mContext, true);
            deleteImages(wipeCloudImages, food);
        }
    }

    /**
     * Deletes the passed list of {@link Food} items
     *
     * @param wipeCloudStorage
     * @param foods
     */
    public void deleteFoods(boolean wipeCloudStorage, boolean wipeCloudImages, Food... foods) {
        new DeleteAsyncTask(mFoodDao, foods).execute(mContext);
        if (wipeCloudStorage && mIsSignedIn) {
            for (Food food : foods) {
                FirebaseDatabaseHelper.delete(food.get_id(), mContext, true);
                deleteImages(wipeCloudImages, food);
            }
        }
    }

    /**
     * Deletes only the images that are in Firebase Storage for the provided Food item
     * <p>
     * Preferably this method should be called after completion of the Food's deletion in Firebase
     * RTD so there are no dangling image references
     * (Concept: https://stackoverflow.com/questions/48527169/firebase-when-deleting-from-storage-and-database-should-the-storage-deletion-b)
     * <p>
     * Note: There is currently no way to deleteImages a directory directly, so we will need to deleteImages
     * each of the contents individually
     *
     * @param removeFromCloud
     * @param food
     */
    public void deleteImages(boolean removeFromCloud, Food food) {
        deleteImages(removeFromCloud, food.getImages(), food.get_id());
    }

    /**
     * Deletes only the images that are in Firebase Storage for the provided Food id
     * <p>
     * Preferably this method should be called after completion of the Food's deletion in Firebase
     * RTD so there are no dangling image references
     * (Concept: https://stackoverflow.com/questions/48527169/firebase-when-deleting-from-storage-and-database-should-the-storage-deletion-b)
     * <p>
     * Note: There is currently no way to deleteImages a directory directly, so we will need to deleteImages
     * each of the contents individually
     *
     * @param removeFromCloud
     * @param imageUris
     * @param foodId
     */
    public void deleteImages(boolean removeFromCloud, List<String> imageUris, long foodId) {
        if (mIsSignedIn) {
            String id = String.valueOf(foodId);
            for (int i = 0; i < imageUris.size(); i++) {
                final String imageUriString = imageUris.get(i);
                // Check form
                UriType type = getImageUriType(imageUriString);
                switch (type) {
                    case LOCAL:
                        Toolbox.deleteBitmapFromInternalStorage(
                                Toolbox.getUriFromImagePath(imageUriString),
                                "FoodRepository/RemoveImage");
                        // don't break here so we can delete from FBS if it's there
                    case FBS:
                        if (removeFromCloud) {
                            FirebaseStorageHelper.deleteImage(imageUriString, id);
                        }
                        break;
                }
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
    private class InsertAsyncTask extends AsyncTask<Context, Void, Long[]> {
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
            int size = mAsyncTaskDao.getAllFoods_List().size();
            if (mFoods.length + size <= Constants.MAX_FOODS_DATABASE_SIZE_DEFAULT) {
                Long[] insertedIds = mAsyncTaskDao.insert(mFoods);
                // Update the widget
                UpdateWidgetService.updateFoodWidget(contexts[0]);
                return insertedIds;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long[] ids) {
            if (ids != null) {
                Timber.d("FoodDao/foods inserted");
                if (mSaveToCloud && mIsSignedIn) {
                    for (int i = 0; i < mFoods.length; i++) {
                        Food food = mFoods[i];
                        food.set_id(ids[i]);
                        FirebaseDatabaseHelper.write(food, mContext, true);
                        FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food, mContext);
                    }
                }
            } else {
                Timber.w("FoodDao/foods not inserted. max limit reached");
                Toolbox.showToast(mContext, mContext.getString(
                        R.string.limits_food_storage_hit, Constants.MAX_FOODS_DATABASE_SIZE_DEFAULT));
            }
        }
    }

    /**
     * Use an AsyncTask to fetch all locally stored foods and upload them to Firebase RTD
     */
    private class UploadAsyncTask extends AsyncTask<Void, Void, List<Food>> {
        private FoodDao mAsyncTaskDao;

        UploadAsyncTask(FoodDao foodDao) {
            mAsyncTaskDao = foodDao;
        }

        @Override
        protected List<Food> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAllFoods_List();
        }

        @Override
        protected void onPostExecute(List<Food> foods) {
            for (Food food : foods) {
                FirebaseDatabaseHelper.write(food, mContext, true);
                FirebaseStorageHelper.uploadAllLocalUrisToFirebaseStorage(food, mContext);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.d("FoodDao/foods updated");
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.d("FoodDao/foods deleted");
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

    // region Uri Matching for Image Uri links

    public enum UriType {
        LOCAL, WEB, FBS
    }

    // We can be confident that Firebase Storage and Web uris will always start with the following
    // There is a chance the Local directory may be different across devices
    private static final String URI_FBS_PREFIX = "https://firebasestorage.googleapis.com/";
    private static final String URI_WEB_PREFIX = "http";

    public static UriType getImageUriType(@NonNull String imageUriString) {
        if (imageUriString.startsWith(URI_FBS_PREFIX)) return UriType.FBS;
        if (imageUriString.startsWith(URI_WEB_PREFIX)) return UriType.WEB;
        return UriType.LOCAL;
    }

    // endregion

    /**
     * Listens to RTD food list change events
     */
    private class FoodChildEventListener implements ChildEventListener {
        private final String TAG = "FoodChildEventListener";

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (mIsSignedIn) {
                try {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food != null) {
                        Timber.d("Food added from RTD: id_%s %s",
                                food.get_id(), food.getFoodName());
                        insertFood(false, food); // don't save to cloud to avoid infinite loop
                    } else {
                        Timber.e("Food added from RTD was null. Not updating DB...");
                    }
                } catch (DatabaseException e) {
                    Timber.e(e, "Food added from RTD contained child of wrong type. Not updating DB..");
                }
            } else {
                Timber.d("FoodChildEventListener/onChildAdded Called while not signed in. Not doing anything...");
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (mIsSignedIn) {
                try {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food != null) {
                        Timber.d("Food changed from RTD: id_%s %s",
                                food.get_id(), food.getFoodName());
                        updateFood(false, food); // don't save to cloud to avoid infinite loop
                    } else {
                        Timber.e("Food changed from RTD was null. Not updating DB...");
                    }
                } catch (DatabaseException e) {
                    Timber.e(e, "Food changed from RTD contained child of wrong type. Not updating DB..");
                }
            } else {
                Timber.d("FoodChildEventListener/onChildChanged Called while not signed in. Not doing anything...");
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            if (mIsSignedIn) {
                try {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food != null) {
                        Timber.d("Food removed from RTD: id_%s %s",
                                food.get_id(), food.getFoodName());
                        deleteFood(false, false, food); // don't save to cloud to avoid infinite loop
                    } else {
                        Timber.e("Food removed from RTD was null. Not updating DB...");
                    }
                } catch (DatabaseException e) {
                    Timber.e(e, "Food removed from RTD contained child of wrong type. Not updating DB..");
                }
            } else {
                Timber.d("FoodChildEventListener/onChildRemoved Called while not signed in. Not doing anything...");
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            // Not used here
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("%s/Cancelled error pulling from RTD: %s", TAG, databaseError.getMessage());
        }
    }
}
