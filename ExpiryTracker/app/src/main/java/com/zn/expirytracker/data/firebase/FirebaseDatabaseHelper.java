package com.zn.expirytracker.data.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zn.expirytracker.data.contracts.DatabaseContract;
import com.zn.expirytracker.data.contracts.SettingsDatabaseContract;
import com.zn.expirytracker.data.contracts.UserDatabaseContract;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import timber.log.Timber;

/**
 * Set of functions used to interface with the Firebase RTD
 */
public class FirebaseDatabaseHelper {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance()
            .getReference(DatabaseContract.DATABASE_NAME + "/" +
                    DatabaseContract.FOOD_TABLE_NAME);

    private static DatabaseReference mDatabase_Preferences = FirebaseDatabase.getInstance()
            .getReference(SettingsDatabaseContract.DATABASE_NAME + "/" +
                    SettingsDatabaseContract.PREFERENCES_TABLE_NAME);

    private static DatabaseReference mDatabase_UserData = FirebaseDatabase.getInstance()
            .getReference(UserDatabaseContract.DATABASE_NAME + "/" +
                    UserDatabaseContract.USER_DATA_TABLE_NAME);

    /**
     * Custom {@link OnCompleteListener} for Firebase RTD that accepts a tag
     */
    private static class FirebaseRTD_OnCompleteListener implements OnCompleteListener<Void> {
        private String TAG;

        FirebaseRTD_OnCompleteListener(String tag) {
            TAG = tag;
        }

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Timber.d("In %s: Push success", TAG);
            } else {
                Timber.d("In %s: Push failed", TAG);
            }
        }
    }

    // for debugging, keep track of number of listener instances
    private static int food = 0;
    private static int prefs = 0;
    private static int food_time = 0;
    private static int prefs_time = 0;

    private static ChildEventListener mFoodChildEventListener;
    private static ChildEventListener mPrefsChildEventListener;
    private static ValueEventListener mFoodTimestampValueEventListener;
    private static ValueEventListener mPrefsTimestampValueEventListener;

    public static void addChildEventListener(@NonNull ChildEventListener listener) {
        if (mFoodChildEventListener == null) {
            Timber.d("firebase/rtd/FOOD: Adding the listener. %s", ++food);
            mFoodChildEventListener = listener;
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase.child(uid).addChildEventListener(mFoodChildEventListener);
        } else {
            Timber.d("firebase/rtd/FOOD: Listener already active. Not adding");
        }
    }

    public static void addChildEventListener_Preferences(@NonNull ChildEventListener listener) {
        if (mPrefsChildEventListener == null) {
            Timber.d("firebase/rtd/PREFS: Adding the listener. %s", ++prefs);
            mPrefsChildEventListener = listener;
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_Preferences.child(uid).addChildEventListener(mPrefsChildEventListener);
        } else {
            Timber.d("firebase/rtd/PREFS: Listener already active. Not adding");
        }
    }

    public static void addValueEventListener_FoodTimestamp(@NonNull ValueEventListener listener) {
        if (mFoodTimestampValueEventListener == null) {
            Timber.d("firebase/rtd/FOOD_TIMESTAMP: Adding the listener. %s", ++food_time);
            mFoodTimestampValueEventListener = listener;
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_UserData.child(uid).child(Constants.FOOD_TIMESTAMP)
                    .addValueEventListener(mFoodTimestampValueEventListener);
        } else {
            Timber.d("firebase/rtd/FOOD_TIMESTAMP: Listener already active. Not adding");
        }
    }

    public static void addValueEventListener_PrefsTimestamp(@NonNull ValueEventListener listener) {
        if (mPrefsTimestampValueEventListener == null) {
            Timber.d("firebase/rtd/PREFS_TIMESTAMP: Adding the listener. %s", ++prefs_time);
            mPrefsTimestampValueEventListener = listener;
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_UserData.child(uid).child(Constants.PREFS_TIMESTAMP)
                    .addValueEventListener(mPrefsTimestampValueEventListener);
        } else {
            Timber.d("firebase/rtd/PREFS_TIMESTAMP: Listener already active. Not adding");
        }
    }

    public static void removeChildEventListener() {
        if (mFoodChildEventListener != null) {
            Timber.d("firebase/rtd/FOOD: Removing the listener. %s", --food);
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase.child(uid).removeEventListener(mFoodChildEventListener);
            mFoodChildEventListener = null;
        }
    }

    public static void removeChildEventListener_Preferences() {
        if (mPrefsChildEventListener != null) {
            Timber.d("firebase/rtd/PREFS: Removing the listener. %s", --prefs);
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_Preferences.child(uid).removeEventListener(mPrefsChildEventListener);
            mPrefsChildEventListener = null;
        }
    }

    public static void removeValueEventListener_FoodTimestamp() {
        if (mFoodTimestampValueEventListener != null) {
            Timber.d("firebase/rtd/FOOD_TIMESTAMP: Removing the listener. %s", --food_time);
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_UserData.child(uid).child(Constants.FOOD_TIMESTAMP)
                    .removeEventListener(mFoodTimestampValueEventListener);
            mFoodTimestampValueEventListener = null;
        }
    }

    public static void removeValueEventListener_PrefsTimestamp() {
        if (mPrefsTimestampValueEventListener != null) {
            Timber.d("firebase/rtd/PREFS_TIMESTAMP: Removing the listener. %s", --prefs_time);
            // Get the user id, to serve as first child
            String uid = AuthToolbox.getUserId();
            mDatabase_UserData.child(uid).child(Constants.PREFS_TIMESTAMP)
                    .removeEventListener(mPrefsTimestampValueEventListener);
            mPrefsTimestampValueEventListener = null;
        }
    }

    /**
     * Adds a timestamp in Firebase RTD
     *
     * @param uid
     * @param type
     * @param context
     * @param setLocal {@code true} if the local timestamp should also be set, preventing an
     *                 additional RTD refresh
     */
    private static void setTimestamp(String uid, TimestampType type, Context context,
                                     boolean setLocal) {
        String key;
        switch (type) {
            case FOOD:
                key = Constants.FOOD_TIMESTAMP;
                break;
            case PREFS:
                key = Constants.PREFS_TIMESTAMP;
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Invalid TimestampType passed: %s", type));
        }
        // create a fake timestamp for RTD that works across locales as well
        String timestamp = mDatabase_UserData.push().getKey();
        mDatabase_UserData.child(uid).child(key).setValue(timestamp)
                .addOnCompleteListener(new FirebaseRTD_OnCompleteListener(
                        "firebase/rtd/timestamp"));
        if (setLocal) {
            Timber.d("In firebase/rtd/timestamp: local timestamp set");
            SharedPreferences sp = context.getSharedPreferences(
                    Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            sp.edit().putString(key, timestamp).apply();
        }
    }

    public enum TimestampType {
        FOOD, PREFS
    }

    /**
     * Writes a single food item to Firebase RTD. Should only be called while the user is
     * logged in or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param food
     * @param context
     * @param setLocalTimestamp {@code true} if the local timestamp should also be set, preventing
     *                          an additional RTD refresh
     */
    public static void write(Food food, Context context, boolean setLocalTimestamp) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();

        // Get food id
        String foodId = String.valueOf(food.get_id());

        // Check connection for logging, then save the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/push...");
        mDatabase.child(uid).child(foodId).setValue(food)
                .addOnCompleteListener(new FirebaseRTD_OnCompleteListener("firebase/rtd/write"));

        setTimestamp(uid, TimestampType.FOOD, context, setLocalTimestamp);
    }

    /**
     * Writes a list of images to the food item in Firebase RTD with the provided {@code foodId}.
     * Should only be called while the user is logged in or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param foodId
     * @param imageUris
     * @param context
     * @param setLocalTimestamp
     */
    public static void writeImagesOnly(String foodId, List<String> imageUris,
                                       Context context, boolean setLocalTimestamp) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();

        Map<String, Object> updatedImageUris = new HashMap<>();
        updatedImageUris.put(DatabaseContract.COLUMN_IMAGES, imageUris);

        // Check connection for logging, then save the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/pushImages...");
        // updateChildren() allows us to keep all siblings unchanged
        mDatabase.child(uid).child(foodId).updateChildren(updatedImageUris)
                .addOnCompleteListener(new FirebaseRTD_OnCompleteListener("firebase/rtd/writeImages"));

        setTimestamp(uid, TimestampType.FOOD, context, setLocalTimestamp);
    }

    /**
     * Writes a Preference value to Firebase RTD. Should only be called while the user is logged in
     * or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param preference
     * @param newValue
     * @param context
     * @param setLocalTimestamp {@code true} if the local timestamp should also be set, preventing
     *                          an additional RTD refresh
     */
    public static void write_Preference(Preference preference, Object newValue, Context context,
                                        boolean setLocalTimestamp) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();

        // Get the key
        String key = preference.getKey();

        // Check connection for logging, then save the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/push_preference...");
        mDatabase_Preferences.child(uid).child(key).setValue(newValue)
                .addOnCompleteListener(new FirebaseRTD_OnCompleteListener("firebase/rtd/write_preference"));

        setTimestamp(uid, TimestampType.PREFS, context, setLocalTimestamp);
    }

    /**
     * Increments a tracking metric by 1. Works for both registered users and guests
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param key
     */
    public static void incrementUserMetricCount(String key) {
        String uid;
        if (AuthToolbox.isSignedIn()) {
            // Get the user id, to serve as first child
            uid = AuthToolbox.getUserId();
        } else {
            uid = Constants.AUTH_GUEST;
        }

        // Check connection for logging, then save the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/transaction_incrementUserMetricCount: %s", key);
        mDatabase_UserData.child(uid).child(key).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Object data = mutableData.getValue();
                if (data == null) {
                    mutableData.setValue(1);
                } else {
                    long count = (long) mutableData.getValue();
                    mutableData.setValue(count + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Timber.d("firebase/rtd/transaction_incrementUserMetricCount complete: %s", databaseError);
            }
        });
    }

    /**
     * Deletes a single food item to Firebase RTD. Should only be called while the user is
     * logged in or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param id
     * @param context
     * @param setLocalTimestamp {@code true} if the local timestamp should also be set, preventing
     *                          an additional RTD refresh
     */
    public static void delete(long id, Context context, boolean setLocalTimestamp) {
        // Get the user id, which is the child where current user's food is stored
        String uid = AuthToolbox.getUserId();
        // Check connection for logging, then remove the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/delete...");
        mDatabase.child(uid).child(String.valueOf(id)).removeValue()
                .addOnCompleteListener(new FirebaseRTD_OnCompleteListener("firebase/rtd/delete"));

        setTimestamp(uid, TimestampType.FOOD, context, setLocalTimestamp);
    }

    /**
     * Deletes all of the food in Firebase RTD associated with the current user logged in by
     * removing the uid child itself. Should only be called while the user is logged in or else
     * this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param context
     * @param setLocalTimestamp {@code true} if the local timestamp should also be set, preventing
     *                          an additional RTD refresh
     */
    public static void deleteAll(Context context, boolean setLocalTimestamp) {
        // Get the user id, which is the child where current user's food is stored
        String uid = AuthToolbox.getUserId();
        // Check connection for logging, then remove all food in the child uid
        checkConnection();
        Timber.d("firebase/rtd/deleteAll...");
        mDatabase.child(uid).removeValue().addOnCompleteListener(
                new FirebaseRTD_OnCompleteListener("firebase/rtd/deleteAll"));

        setTimestamp(uid, TimestampType.FOOD, context, setLocalTimestamp);
    }

    /**
     * Deletes all of the Preferences set in Firebase RTD associated with the current user logged
     * in by removing the uid child itself. Should only be called while the user is logged in or
     * else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     */
    public static void deleteAll_Preferences() {
        // Get the user id, which is the child where current user's food is stored
        String uid = AuthToolbox.getUserId();
        // Check connection for logging, then remove all food in the child uid
        checkConnection();
        Timber.d("firebase/rtd/deleteAll_preferences...");
        mDatabase_Preferences.child(uid).removeValue().addOnCompleteListener(
                new FirebaseRTD_OnCompleteListener("firebase/rtd/deleteAll_preferences"));
    }

    /**
     * Checks if device is currently connected to Firebase RTD. Primarily for debugging purposes,
     * so check the logs for status.
     */
    private static void checkConnection() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Timber.d("firebase/rtd: connected");
                } else {
                    Timber.e("firebase/rtd: not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Timber.e("firebase/rtd: connection cancelled");
            }
        });
    }
}
