package com.zn.expirytracker.data.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Constants;

import timber.log.Timber;

/**
 * For updating local data from Firebase changes
 */
public class FirebaseUpdaterHelper {

    private static ChildEventListener mFoodChildEventListener;
    private static ChildEventListener mPrefsChildEventListener;

    public static void setFoodChildEventListener(ChildEventListener foodChildEventListener) {
        mFoodChildEventListener = foodChildEventListener;
    }

    public static void setPrefsChildEventListener(Context context) {
        mPrefsChildEventListener = new PrefsChildEventListener(context);
    }

    public static void setPrefsChildEventListener(ChildEventListener listener) {
        mPrefsChildEventListener = listener;
    }

    /**
     * One-way reading from Firebase RTD to get the freshest Preference values stored. Only takes
     * action from {@link ChildEventListener#onChildAdded(DataSnapshot, String)}. All Preference
     * changes are managed in {@link com.zn.expirytracker.settings.SettingsFragment}
     */
    private static class PrefsChildEventListener implements ChildEventListener {
        private Context mContext;

        PrefsChildEventListener(Context context) {
            mContext = context;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Timber.d("Preference added from RTD: %s", dataSnapshot.getKey());
            updateSharedPreferencesFromFirebase(dataSnapshot, mContext);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Timber.d("Preference changed from RTD: %s", dataSnapshot.getKey());
            updateSharedPreferencesFromFirebase(dataSnapshot, mContext);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            // Not used here
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            // Not used here
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("Error pulling Preference from RTD: %s", databaseError.getMessage());
        }
    }

    /**
     * Writes the updated Preference value from Firebase to SharedPreferences
     *
     * @param snapshot
     */
    private static void updateSharedPreferencesFromFirebase(DataSnapshot snapshot, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = snapshot.getKey();
        Object value = snapshot.getValue();

        if (key != null) {
            if (key.equals(context.getString(R.string.pref_notifications_receive_key)) ||
                    key.equals(context.getString(R.string.pref_capture_beep_key)) ||
                    key.equals(context.getString(R.string.pref_capture_vibrate_key)) ||
                    key.equals(context.getString(R.string.pref_capture_voice_input_key))) {
                // Handle booleans
                try {
                    sp.edit().putBoolean(key, (boolean) value).apply();
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            } else if (key.equals(context.getString(R.string.pref_notifications_days_key)) ||
                    key.equals(context.getString(R.string.pref_notifications_tod_key)) ||
                    key.equals(context.getString(R.string.pref_widget_num_days_key)) ||
                    key.equals(context.getString(R.string.pref_account_display_name_key))) {
                try {
                    sp.edit().putString(key, (String) value).apply();
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            }
        }
    }

    /**
     * Listens to RTD food timestamp change events
     */
    private static class TimestampValueEventListener implements ValueEventListener {
        private final String TAG = "TimestampValueEventListener";
        private FirebaseDatabaseHelper.TimestampType mType;
        private Context mContext;

        TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType type, Context context) {
            mType = type;
            mContext = context;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            checkTimestamp(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("%s/Cancelled error pulling from RTD: %s", TAG, databaseError.getMessage());
        }

        /**
         * Compares the timestamp on RTD vs the timestamp stored in SharedPreferences,
         * then starts the appropriate listener if they're different, and then finally updates
         * the SharedPreferences timestamp with RTD's.
         * <p>
         * If the timestamp is the same between RTD and SharedPreferences, this does nothing
         *
         * @param dataSnapshot
         */
        private void checkTimestamp(DataSnapshot dataSnapshot) {
            String type;
            String tag;
            switch (mType) {
                case FOOD:
                    type = Constants.FOOD_TIMESTAMP;
                    tag = "FOOD_TIMESTAMP";
                    break;
                case PREFS:
                    type = Constants.PREFS_TIMESTAMP;
                    tag = "PREFS_TIMESTAMP";
                    break;
                default:
                    throw new IllegalArgumentException(String.format(
                            "Invalid TimestampType passed: %s", mType));
            }
            Timber.d("%s: checking timestamp...", tag);
            try {
                String timestamp_rtd = dataSnapshot.getValue(String.class);
                if (timestamp_rtd != null) {
                    SharedPreferences sp = mContext.getSharedPreferences(
                            Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                    String timestamp_sp = sp.getString(type, "");
                    Timber.d("SP: %s, RTD: %s", timestamp_sp, timestamp_rtd);
                    if (!timestamp_rtd.equals(timestamp_sp)) {
                        // RTD has been updated, so sync and update the internal timestamp
                        Timber.d("%s: Didn't match, so started listening", tag);
                        switch (mType) {
                            case FOOD:
                                listenForFoodChanges(true);
                                break;
                            case PREFS:
                                listenForPrefsChanges(true);
                                break;
                        }
                        sp.edit().putString(type, timestamp_rtd).apply();
                    } else {
                        // Don't sync if the timestamp is the same
                        Timber.d("%s: Matched, so not listening", tag);
                    }
                } else {
                    Timber.d("%s: Was null, so not listening", tag);
                }
            } catch (DatabaseException e) {
                Timber.e(e, "Timestamp in RTD was of wrong type. Not setting TimestampValueEventListener");
            }
        }
    }

    /**
     * Enable listening for food timestamp changes in RTD
     *
     * @param start
     * @param context
     */
    public static void listenForFoodTimestampChanges(boolean start, Context context) {
        if (start) {
            FirebaseDatabaseHelper.addValueEventListener_FoodTimestamp(
                    new TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType.FOOD, context));
        } else {
            FirebaseDatabaseHelper.removeValueEventListener_FoodTimestamp();
        }
    }

    /**
     * Enable listening for preference timestamp changes in RTD
     *
     * @param start
     * @param context
     */
    public static void listenForPrefsTimestampChanges(boolean start, Context context) {
        if (start) {
            FirebaseDatabaseHelper.addValueEventListener_PrefsTimestamp(
                    new TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType.PREFS, context));
        } else {
            FirebaseDatabaseHelper.removeValueEventListener_PrefsTimestamp();
        }
    }

    public static void listenForFoodChanges(boolean start) {
        if (start) {
            if (mFoodChildEventListener != null) {
                // Only listen to changes to food_database/food_table/uid/{child}
                FirebaseDatabaseHelper.addChildEventListener(mFoodChildEventListener);
            } else {
                Timber.d("FOOD: The listener was null. Doing nothing...");
            }
        } else {
            FirebaseDatabaseHelper.removeChildEventListener();
        }
    }

    public static void listenForPrefsChanges(boolean start) {
        if (start) {
            if (mPrefsChildEventListener != null) {
                FirebaseDatabaseHelper.addChildEventListener_Preferences(mPrefsChildEventListener);
            } else {
                Timber.d("PREFS: The listener was null. Doing nothing...");
            }
        } else {
            FirebaseDatabaseHelper.removeChildEventListener_Preferences();
        }
    }
}
