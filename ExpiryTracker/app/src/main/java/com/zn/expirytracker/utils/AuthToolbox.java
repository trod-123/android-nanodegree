package com.zn.expirytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.zn.expirytracker.GlideApp;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.notifications.NotificationHelper;
import com.zn.expirytracker.ui.MainActivity;
import com.zn.expirytracker.ui.SignInActivity;
import com.zn.expirytracker.ui.widget.FoodWidget;
import com.zn.expirytracker.ui.widget.UpdateWidgetService;

import timber.log.Timber;

/**
 * Set of helper methods for user authentication
 */
public class AuthToolbox {

    public static final int DEFAULT_MIN_PASSWORD_LENGTH = 8; // number of characters

    // region Auth fields validation

    /**
     * Name must not be empty
     *
     * @param name
     * @param tilName
     * @return
     */
    public static boolean isNameValid(String name, TextInputLayout tilName, Context context) {
        if (name.trim().isEmpty()) {
            tilName.setError(context.getString(R.string.auth_error_no_name));
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    /**
     * E-mail address must be in a valid form: ***@***.***
     *
     * @param email
     * @param tilEmail
     * @return
     */
    public static boolean isEmailValid(String email, TextInputLayout tilEmail, Context context) {
        if (email.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(context.getString(R.string.auth_error_invalid_email));
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }

    /**
     * Password must have at least 8 characters and must not contain any spaces
     *
     * @param password
     * @param tilPassword
     * @return
     */
    public static boolean isPasswordValid(String password, TextInputLayout tilPassword, Context context) {
        if (password.trim().isEmpty() ||
                password.trim().length() < AuthToolbox.DEFAULT_MIN_PASSWORD_LENGTH ||
                password.contains(" ")) {
            tilPassword.setError(context.getString(R.string.auth_error_weak_password));
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    // endregion

    /**
     * Helper to show or hide the SignIn loading overlay
     *
     * @param show
     * @param noClickOverlay
     * @param progressBar
     */
    public static void showLoadingOverlay(boolean show, View noClickOverlay, View progressBar) {
        Toolbox.showView(noClickOverlay, show, true);
        Toolbox.showView(progressBar, show, false);
    }

    /**
     * Starts the Sign-in activity and clears the existing backstack. Updates any widgets to show
     * the signed-out view
     *
     * @param context
     */
    public static void startSignInActivity(Context context) {
        startActivityAndClearBackstack(context, SignInActivity.class);
        UpdateWidgetService.updateFoodWidget(context);
    }

    /**
     * Helper to start the main activity
     */
    public static void startMainActivity(Context context) {
        startActivityAndClearBackstack(context, MainActivity.class);
    }

    /**
     * Helper to start any activity and clears the existing backstack
     * <p>
     * https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
     *
     * @param context
     * @param activity
     */
    private static void startActivityAndClearBackstack(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
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
     * Custom Exception that throws a message in the form:
     * {@code Attempted to [ACTION] when no user is logged in}
     */
    public static class FirebaseAuthNotLoggedInException extends IllegalStateException {

        public FirebaseAuthNotLoggedInException() {
            super("Attempted to do a Firebase Auth user action when no user is logged in");
        }

        public FirebaseAuthNotLoggedInException(String action) {
            super(String.format("Attempted to %s when no user is logged in", action));
        }

        public FirebaseAuthNotLoggedInException(String action, Throwable cause) {
            super(String.format("Attempted to %s when no user is logged in", action), cause);
        }
    }

    /**
     * Gets the id of the currently logged in user
     *
     * @return
     * @throws FirebaseAuthNotLoggedInException
     */
    public static String getUserId() throws FirebaseAuthNotLoggedInException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("get user id");
        }
        return user.getUid();
    }

    /**
     * Gets the email address of the currently logged in user. For security, we do not store
     * e-mail addresses in Firebase RTD, or in Shared Preferences
     *
     * @return
     * @throws FirebaseAuthNotLoggedInException
     */
    public static String getUserEmail() throws FirebaseAuthNotLoggedInException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("get user email");
        }
        return user.getEmail();
    }

    /**
     * Link the recently signed-in user with the app, including the user's information and their
     * data.
     * <p>
     * Note: This should only be used for Federated identity providers (e.g. Google, Facebook,
     * Twitter, GitHub)
     *
     * @param context
     * @throws FirebaseAuthNotLoggedInException
     */
    public static void syncSignInWithDevice_FederatedProvidersAuth(Context context, FirebaseUser user)
            throws FirebaseAuthNotLoggedInException {
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("update display name");
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
    }

    /**
     * Link the recently signed-in user with the app, including the user's information and their
     * data.
     * <p>
     * Note: This should only be used for Email and Password based authentication
     *
     * @param context
     * @throws FirebaseAuthNotLoggedInException
     */
    public static void syncSignInWithDevice_EmailAuth(Context context, FirebaseUser user, String name)
            throws FirebaseAuthNotLoggedInException {
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("update display name");
        }
        // sync user details
        updateDisplayName(context, name);
    }

    /**
     * Updates the user's display name in Firebase, then uses a listener to update the display
     * name in SharedPrefrences.
     * <p>
     * Throws an error if user is not signed in
     *
     * @param displayName
     * @throws FirebaseAuthNotLoggedInException
     */
    public static void updateDisplayName(final Context context, final String displayName)
            throws FirebaseAuthNotLoggedInException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("update display name");
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
     * Signs the user out of Firebase and Google, if applicable. Resets SharedPreferences and
     * launches {@link SignInActivity}. Clears the existing backstack
     * <p>
     * Throws an error if user is not signed in
     *
     * @param context
     */
    public static void signOut(final Context context, GoogleSignInClient client)
            throws FirebaseAuthNotLoggedInException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("sign out");
        }
        // Check if user is signed into Google sign-in
        // https://stackoverflow.com/questions/38190253/android-google-sign-in-check-if-user-is-signed-in
        if (client != null && GoogleSignIn.getLastSignedInAccount(context) != null) {
            // Attempt sign out from Google first, if available, before signing out of Firebase
            client.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Timber.d("Signing out the user");
                        signOutFromFirebase(context);
                    } else {
                        Timber.e(task.getException(), "GoogleApiClient sign out failure");
                        Toolbox.showToast(context, "There was a problem signing you out");
                    }
                }
            });
        } else {
            // If user had not authenticated with Google, then just sign out of Firebase
            signOutFromFirebase(context);
        }
    }

    /**
     * Helper for signing the user out of Firebase, resetting SharedPreferences, and launching
     * {@link SignInActivity}. Clears the existing backstack
     *
     * @param context
     */
    private static void signOutFromFirebase(Context context) {
        FirebaseAuth.getInstance().signOut();
        resetSharedPreferences(context);
        startSignInActivity(context);
    }

    /**
     * Deletes the user's Firebase account and revokes access from user's Google account, if
     * applicable. Also resets SharedPreferences. Once delete is successful, launches
     * {@link SignInActivity}. Clears the existing backstack
     *
     * @param context
     */
    public static void deleteAccount(final Context context, GoogleSignInClient client)
            throws FirebaseAuthNotLoggedInException {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new FirebaseAuthNotLoggedInException("sign out");
        }
        if (client != null && GoogleSignIn.getLastSignedInAccount(context) != null) {
            // Attempt deleting Google account first before deleting Firebase
            client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Timber.d("Deleting user account");
                        deleteFirebaseAccount(user, context);
                    } else {
                        Timber.e(task.getException(), "GoogleApiClient revoke access failure");
                        Toolbox.showToast(context, "There was a problem deleting your account");
                    }
                }
            });
        } else {
            // If user had not authenticated with Google, then just delete Firebase account
            deleteFirebaseAccount(user, context);
        }
    }

    /**
     * Helper for deleting the user's account from Firebase, and resetting SharedPreferences.
     * Once delete is successful, launches {@link SignInActivity}. Clears the existing backstack
     *
     * @param user
     * @param context
     */
    private static void deleteFirebaseAccount(@NonNull FirebaseUser user, final Context context) {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Deleted the user");
                            resetSharedPreferences(context);
                            startSignInActivity(context);
                        } else {
                            Timber.e(task.getException(), "Firebase delete account failure");
                            Toolbox.showToast(context,
                                    "There was a problem deleting your account");
                        }
                    }
                });
    }

    /**
     * Helper for resetting all values in shared preferences to their defaults and cancels
     * all notifications and automatic widget updates
     *
     * @param context
     */
    private static void resetSharedPreferences(Context context) {
        NotificationHelper.cancelNotificationJob(context);
        FoodWidget.cancelNextUpdate(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }

    /**
     * Deletes all data from device: food and app images. Does NOT delete cloud data
     * <p>
     * Note: This needs to be called from a background thread since we're getting food data from
     * the view model
     *
     * @param viewModel
     * @param context
     */
    public static void deleteDeviceData(FoodViewModel viewModel, final Context context) {
        Timber.d("Deleting all device data: foods, images, and Glide cache");
        deleteData(viewModel, context, false);
    }

    /**
     * Deletes all data from device AND cloud: food and app images
     * <p>
     * Note: This needs to be called from a background thread since we're getting food data from
     * the view model
     *
     * @param viewModel
     * @param context
     */
    public static void deleteDeviceAndCloudData(FoodViewModel viewModel, final Context context) {
        Timber.d("Deleting all device AND cloud data: foods, images, and Glide cache");
        deleteData(viewModel, context, true);
        FirebaseDatabaseHelper.deleteAll_Preferences();
    }

    /**
     * Deletes device data. Pass in {@code wipeCloudData == true} to also wipe all cloud data.
     * <p>
     * Note: This needs to be called from a background thread since we're getting food data from
     * the view model. Food is deleted one at a time because we need to ensure Firebase Storage is
     * being updated properly, and this can only be done by iterating each of the uris of the food
     * image lists. Firebase Storage does not have capability to delete directories, only individual
     * files.
     *
     * @param viewModel
     * @param context
     * @param wipeCloudData
     */
    private static void deleteData(FoodViewModel viewModel, final Context context, boolean wipeCloudData) {
        viewModel.delete(wipeCloudData, viewModel.getAllFoods_List().toArray(new Food[]{}));
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
