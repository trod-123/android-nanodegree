package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.zn.expirytracker.GlideApp;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.SignInActivity;

import timber.log.Timber;

/**
 * Set of helper methods for user authentication
 */
public class AuthToolbox {

    /**
     * Starts the Sign-in activity and clears the existing backstack
     * <p>
     * https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
     *
     * @param context
     */
    public static void startSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Checks Firebase Auth to see if the user is currently signed in
     *
     * @return {@code true} - if the user is signed in
     */
    public static boolean isSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    /**
     * Link the recently signed-in user with the app, including the user's information and their
     * data
     *
     * @param context
     * @throws IllegalStateException
     */
    public static void syncSignInWithDevice(Context context) throws IllegalStateException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Attempted to update display name when no user is logged in");
        }
        // sync user details
        for (UserInfo profile : user.getProviderData()) {
            // Id of the provider (ex: google.com)
            String providerId = profile.getProviderId();

            // UID specific to the provider
            String uid = profile.getUid();

            // Name, email address, and profile photo Url
            String name = profile.getDisplayName();
            updateDisplayName_SharedPreferences(context, name);

            String email = profile.getEmail();
            Uri photoUrl = profile.getPhotoUrl();
        }

        // TODO: Download data from Firestore
    }

    /**
     * Updates the user's display name in Firebase, then uses a listener to update the display
     * name in SharedPrefrences.
     * <p>
     * Throws an error if user is not signed in
     *
     * @param displayName
     * @throws IllegalStateException
     */
    public static void updateDisplayName(final Context context, final String displayName)
            throws IllegalStateException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Attempted to update display name when no user is logged in");
        }
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Updating user's display name");
                            updateDisplayName_SharedPreferences(context, displayName);
                        } else {
                            Timber.e("There was a problem updating the user's display name");
                            Toolbox.showToast(context, "There was a problem updating your display name");
                        }
                    }
                });
    }

    /**
     * Updates the user's display name in SharedPreferences
     *
     * @param context
     * @param name
     */
    private static void updateDisplayName_SharedPreferences(Context context, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putString(context.getString(R.string.pref_account_display_name_key), name)
                .apply();
    }

    /**
     * Signs the user out, updates SharedPreferences with the sign in status, and launches
     * {@link SignInActivity}.  Clears the existing backstack
     * <p>
     * Throws an error if user is not signed in
     *
     * @param context
     */
    public static void signOut(final Context context) throws IllegalStateException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Attempted to sign out when no user is logged in");
        }
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Signing out the user");

                            resetSharedPreferences(context);
                            startSignInActivity(context);
                        } else {
                            Timber.e("There was a problem signing the user out");
                            Toolbox.showToast(context, "There was a problem signing you out");
                        }
                    }
                });
    }

    /**
     * Helper for deleting the user's account from Firebase. Once delete is successful, launches
     * {@link SignInActivity}. Clears the existing backstack
     *
     * @param context
     */
    public static void deleteAccount(final Context context) throws IllegalStateException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Attempted to sign out when no user is logged in");
        }
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Deleted the user");
                            // TODO: Make sure this also deletes all images from Cloud Firestore

                            resetSharedPreferences(context);
                            startSignInActivity(context);
                        } else {
                            Timber.e("There was a problem deleting the user's account");
                            Toolbox.showToast(context, "There was a problem deleting your account");
                        }
                    }
                });

    }

    /**
     * Helper for resetting all values in shared preferences to their defaults
     *
     * @param context
     */
    private static void resetSharedPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }

    /**
     * Deletes all data from device, food and app images
     *
     */
    public static void deleteDeviceData(FoodViewModel viewModel, final Context context) {
        Timber.d("Deleting all device data: foods, images, and Glide cache");
        viewModel.deleteAllFoods();
        // Remove all images from app's images directory
        new Thread(new Runnable() {
            @Override
            public void run() {
                Toolbox.deleteBitmapDirectory(context);
                GlideApp.get(context).clearDiskCache();
            }
        }).start();
    }
}
