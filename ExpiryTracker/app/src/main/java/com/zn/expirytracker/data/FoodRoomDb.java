package com.zn.expirytracker.data;

import android.content.Context;
import android.os.AsyncTask;

import com.zn.expirytracker.BuildConfig;
import com.zn.expirytracker.data.contracts.DatabaseContract;
import com.zn.expirytracker.data.model.Cache;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.FoodDao;
import com.zn.expirytracker.data.model.Temp;
import com.zn.expirytracker.utils.DebugFields;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Food.class, Temp.class, Cache.class},
        version = DatabaseContract.CURRENT_VERSION)
public abstract class FoodRoomDb extends RoomDatabase {

    public abstract FoodDao foodDao();

    private static FoodRoomDb INSTANCE;

    public static FoodRoomDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FoodRoomDb.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context, FoodRoomDb.class, DatabaseContract.DATABASE_NAME)
                            // TODO: Implement proper migration handling
                            // Wipes and rebuilds instead of migrating if no migration object is
                            // available
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Adding a callback during the INSTANCE build() process that populates the database
     * <p>
     * Preferable to call onOpen to perform database inits and refreshes than in onCreate since
     * onCreate is only called once
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            if (BuildConfig.DEBUG && DebugFields.POPULATE_DUMMY_DATA) {
                // Only populate in debug mode, if dummy enabled. Otherwise listener does nothing
                new PopulateDbAsync(INSTANCE).execute();
            }
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private FoodRoomDb db;

        public PopulateDbAsync(FoodRoomDb db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (db.foodDao().getAnyFood().length == 0) {
                // Load data if database is empty
                loadDummyData();
            }
            return null;
        }

        /**
         * Needs to be called from a worker thread since we're inserting into the Dao
         */
        private void loadDummyData() {
            // Generate random data. Accessible anywhere in the app
            TestDataGen dataGenerator = TestDataGen.generateInstance(
                    TestDataGen.DEFAULT_NUM_CHART_ENTRIES, TestDataGen.DEFAULT_NUM_FOOD_DATA,
                    TestDataGen.DEFAULT_DATE_BOUNDS, TestDataGen.DEFAULT_GOOD_THRU_DATE_BOUNDS,
                    TestDataGen.DEFAULT_COUNT_BOUNDS, TestDataGen.DEFAULT_SIZE_FORMAT,
                    TestDataGen.DEFAULT_SIZE_BOUNDS, TestDataGen.DEFAULT_WEIGHT_FORMAT,
                    TestDataGen.DEFAULT_IMAGE_COUNT_BOUNDS, false);
            List<Food> foods = dataGenerator.getAllFoods();
            db.foodDao().insert(foods.toArray(new Food[]{}));
        }
    }
}
