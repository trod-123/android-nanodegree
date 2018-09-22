package com.zn.expirytracker.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zn.expirytracker.data.model.DatabaseContract;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.AuthToolbox;

import timber.log.Timber;

/**
 * Set of functions used to interface with the Firebase RTD
 */
public class FirebaseDatabaseHelper {

    // Food data stored in: https://expirytracker-94b90.firebaseio.com/food_table
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance()
            .getReference(DatabaseContract.FOOD_TABLE_NAME);

    /**
     * Custom {@link OnCompleteListener} for Firebase RTD that accepts a tag
     */
    private static class FirebaseOnCompleteListener implements OnCompleteListener<Void> {
        private String TAG;

        FirebaseOnCompleteListener(String tag) {
            TAG = tag;
        }

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Timber.e("In %s: Push success", TAG);
            } else {
                Timber.e("In %s: Push failed", TAG);
            }
        }
    }

    public static void addChildEventListener(ChildEventListener listener) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();
        mDatabase.child(uid).addChildEventListener(listener);
    }

    public static void removeChildEventListener(ChildEventListener listener) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();
        mDatabase.child(uid).removeEventListener(listener);
    }

    /**
     * Writes a single food item to Firebase RTD. Should only be called while the user is
     * logged in or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param food
     */
    public static void write(Food food) {
        // Get the user id, to serve as first child
        String uid = AuthToolbox.getUserId();

        // Get food id
        String foodId = String.valueOf(food.get_id());

        // Check connection for logging, then save the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/push...");
        mDatabase.child(uid).child(foodId).setValue(food)
                .addOnCompleteListener(new FirebaseOnCompleteListener("firebase/rtd/write"));
    }

    /**
     * Deletes a single food item to Firebase RTD. Should only be called while the user is
     * logged in or else this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     *
     * @param id
     */
    public static void delete(long id) {
        // Get the user id, which is the child where current user's food is stored
        String uid = AuthToolbox.getUserId();
        // Check connection for logging, then remove the food. Use food id as RTD id
        checkConnection();
        Timber.d("firebase/rtd/delete...");
        mDatabase.child(uid).child(String.valueOf(id)).removeValue()
                .addOnCompleteListener(new FirebaseOnCompleteListener("firebase/rtd/delete"));
    }

    /**
     * Deletes all of the food in Firebase RTD associated with the current user logged in by
     * removing the uid child itself. Should only be called while the user is logged in or else
     * this throws an error
     * <p>
     * Needs to be called on the main thread since we get user info here
     */
    public static void deleteAll() {
        // Get the user id, which is the child where current user's food is stored
        String uid = AuthToolbox.getUserId();
        // Check connection for logging, then remove all food in the child uid
        checkConnection();
        Timber.d("firebase/rtd/deleteAll...");
        mDatabase.child(uid).removeValue().addOnCompleteListener(
                new FirebaseOnCompleteListener("firebase/rtd/deleteAll"));
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
