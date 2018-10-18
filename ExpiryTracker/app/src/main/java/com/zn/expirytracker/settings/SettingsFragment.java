package com.zn.expirytracker.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.dialog.ConfirmDeleteDialogFragment;
import com.zn.expirytracker.ui.notifications.NotificationHelper;
import com.zn.expirytracker.ui.widget.UpdateWidgetService;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DebugFields;
import com.zn.expirytracker.utils.Toolbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

public class SettingsFragment extends PreferenceFragmentCompat
        implements ConfirmDeleteDialogFragment.OnConfirmDeleteButtonClickListener {

    static Preference mPreferenceNotifications;
    static Preference mPreferenceNotificationsNumDays;
    static Preference mPreferenceNotificationsTod;
    static Preference mPreferenceWidget;
    static Preference mPreferenceCaptureBeep;
    static Preference mPreferenceCaptureVibrate;
    static Preference mPreferenceCaptureVoice;
    static Preference mPreferenceAccountSignIn;
    static Preference mPreferenceAccountSignOut;
    // TODO: Hide for now
//    static Preference mPreferenceAccountSync;
    static Preference mPreferenceAccountDelete;
    static Preference mPreferenceDisplayName;
    static Preference mPreferenceWipeDeviceData;

    private static FoodViewModel mViewModel;
    private Activity mHostActivity;
    private GoogleSignInClient mGoogleSignInClient;
    private static SharedPreferences mSp;
    private static boolean mIsSignedIn;

    /**
     * Guard for preventing OnPreferenceChange actions to run when we're just setting up the
     * OnPreferenceChangeListener. {@code true} means such actions will not occur.
     * <p>
     * Between device and Firebase RTD, guard helps prevent
     * (1) Initial SP defaults from overwriting user-set preferences in RTD
     * (2) Infinite update loop between read and write between device and RTD
     * <p>
     * Regarding the above, while guard prevents initial SP values from being loaded to RTD for
     * the first time, this does not matter since these will always be the same. Only when default
     * preferences are changed are they written to and preserved in RTD
     */
    static boolean mInitializeGuard = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mHostActivity = getActivity();

        // For signing out and revoking access from Google authenticated accounts
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mHostActivity, gso);

        mSp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);

        if (mIsSignedIn = AuthToolbox.isSignedIn()) {
            FirebaseUpdaterHelper.setPrefsChildEventListener(createNewChildEventListener());
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Keep a reference to preferences that will be enabled or disabled
        mPreferenceNotifications = findPreference(
                getString(R.string.pref_notifications_receive_key));
        mPreferenceNotificationsNumDays =
                findPreference(getString(R.string.pref_notifications_days_key));
        mPreferenceNotificationsTod =
                findPreference(getString(R.string.pref_notifications_tod_key));
        mPreferenceWidget = findPreference(getString(R.string.pref_widget_num_days_key));
        mPreferenceCaptureBeep = findPreference(getString(R.string.pref_capture_beep_key));
        mPreferenceCaptureVibrate = findPreference(getString(R.string.pref_capture_vibrate_key));
        mPreferenceCaptureVoice = findPreference(getString(R.string.pref_capture_voice_input_key));
        mPreferenceAccountSignIn = findPreference(getString(R.string.pref_account_sign_in_key));
        mPreferenceAccountSignOut = findPreference(getString(R.string.pref_account_sign_out_key));
//        mPreferenceAccountSync = findPreference(getString(R.string.pref_account_sync_key));
        mPreferenceAccountDelete = findPreference(getString(R.string.pref_account_delete_key));
        mPreferenceDisplayName = findPreference(getString(R.string.pref_account_display_name_key));
        mPreferenceWipeDeviceData = findPreference(getString(R.string.pref_account_wipe_data_key));

        // Set summaries and enabled based on switches or checkboxes
        setOnPreferenceChangeListener(mPreferenceNotifications);
        setOnPreferenceChangeListener(mPreferenceNotificationsNumDays);
        setOnPreferenceChangeListener(mPreferenceNotificationsTod);
        setOnPreferenceChangeListener(mPreferenceWidget);
        setOnPreferenceChangeListener(mPreferenceCaptureBeep);
        setOnPreferenceChangeListener(mPreferenceCaptureVibrate);
        setOnPreferenceChangeListener(mPreferenceCaptureVoice);
        setOnPreferenceChangeListener(mPreferenceDisplayName);

        // Set the behavior for the custom preferences
        setAccountPreferencesActions();
        if (AuthToolbox.isSignedIn()) disableDemoAccountSettings(AuthToolbox.getUserId());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show account settings only if the user is signed in
        showAccountSettings(AuthToolbox.isSignedIn());
        if (mIsSignedIn) {
            FirebaseUpdaterHelper.listenForPrefsChanges(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (AuthToolbox.isSignedIn()) {
            // Ensure we only run this while user is signed in. User can potentially
            // sign out before onPause() is called
            FirebaseUpdaterHelper.listenForPrefsChanges(false);
        }
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sOnPreferenceChangeListener
     */
    private static void setOnPreferenceChangeListener(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sOnPreferenceChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        mInitializeGuard = true;
        updatePreferenceValue(preference);
    }

    /**
     * Updates the UI with the newly updated preference value. See
     * {@link SettingsFragment#sOnPreferenceChangeListener}
     *
     * @param preference
     * @see SettingsFragment#sOnPreferenceChangeListener
     */
    private static void updatePreferenceValue(Preference preference) {
        if (preference instanceof MultiSelectListPreference) {
            sOnPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getStringSet(preference.getKey(), new HashSet<String>()));
        } else if (preference instanceof SwitchPreference ||
                preference instanceof CheckBoxPreference) {
            sOnPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        } else {
            sOnPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    /**
     * Helper to show account settings only if user is currently logged in
     */
    private void showAccountSettings(boolean show) {
//        mPreferenceDisplayName.setVisible(show);
        mPreferenceAccountSignIn.setVisible(!show);
        mPreferenceAccountSignOut.setVisible(show);
        boolean hasAccount = mPreferenceAccountSignOut.isVisible();
//        mPreferenceAccountSync.setVisible(hasAccount);
        mPreferenceAccountDelete.setVisible(hasAccount);
        mPreferenceWipeDeviceData.setVisible(!show);
    }

    /**
     * Disable particular account settings if demo account is signed in, to prevent tampering
     *
     * @param userId
     */
    private void disableDemoAccountSettings(String userId) {
        if (userId.equals(DebugFields.DEMO_TEST_ACCOUNT_ID)) {
            mPreferenceDisplayName.setEnabled(false);
            mPreferenceAccountDelete.setEnabled(false);
        }
    }

    /**
     * Set actions of custom preferences
     * <p>
     * https://stackoverflow.com/questions/5298370/how-to-add-a-button-to-a-preferencescreen
     */
    private void setAccountPreferencesActions() {
        mPreferenceAccountSignIn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AuthToolbox.startSignInActivity(mHostActivity, false); // Closes settings and clears back stack
                return true;
            }
        });
        mPreferenceAccountSignOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startDeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType.SIGN_OUT,
                        mGoogleSignInClient);
                // TODO: Do not exit the app when user just wants to sign out
                return true;
            }
        });
        if (AuthToolbox.isSignedIn())
            mPreferenceAccountSignOut.setSummary(AuthToolbox.getUserEmail());
//        mPreferenceAccountSync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                // TODO: Sync Room and Firebase here
//                return false;
//            }
//        });
        // Only show the wipe feature if current account is not the demo one
        mPreferenceAccountDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showWipeDataConfirmationDialog(ConfirmDeleteDialogFragment.DeleteType.ACCOUNT);
                // Delete handled in ConfirmDeleteDialogFragment.onConfirmDeleteButtonClicked
                return true;
            }
        });
        // Only show wipe device data if user is not logged in
        mPreferenceWipeDeviceData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showWipeDataConfirmationDialog(ConfirmDeleteDialogFragment.DeleteType.DEVICE);
                // Delete handled in ConfirmDeleteDialogFragment.onConfirmDeleteButtonClicked
                return true;
            }
        });
    }

    /**
     * Helper to start the delete async task. Removes all Firebase RTD listeners first before
     * proceeding
     *
     * @param deleteType
     * @param signInClient
     */
    private void startDeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType deleteType,
                                          GoogleSignInClient signInClient) {
        if (mIsSignedIn) {
            FirebaseUpdaterHelper.listenForPrefsTimestampChanges(false, mHostActivity);
            FirebaseUpdaterHelper.listenForFoodTimestampChanges(false, mHostActivity);
            FirebaseUpdaterHelper.listenForPrefsChanges(false);
            FirebaseUpdaterHelper.listenForFoodChanges(false);
        }
        new DeleteDataAsyncTask(deleteType, signInClient).execute();
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value. Also adds individual preference-specific actions
     * <p>
     * Note: This listener is created BEFORE onCreate() is called. So anu auth status checking
     * needs to read from AuthToolbox directly and NOT from {@link SettingsFragment#mIsSignedIn}
     */
    private static Preference.OnPreferenceChangeListener sOnPreferenceChangeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    Context context = preference.getContext();
                    if (!(preference instanceof SwitchPreference ||
                            preference instanceof CheckBoxPreference)) {
                        // Only change summaries for EditTexts and lists
                        preference.setSummary(getPreferenceSummary(preference, value));
                    } else {
                        // Enable preferences based on values for SwitchPreferences and 
                        // CheckBoxPreferences
                        enablePreference(preference, value);
                    }

                    /*
                        Individual preference-specific actions. Do an action right away when the
                        preference's value changes
                    */
                    handleIndividualPreferenceChangeActions(preference, context, value);

                    // Update Firebase RTD
                    if (!mInitializeGuard && AuthToolbox.isSignedIn())
                        // Guard is needed to prevent
                        // (1) Initial SP defaults from overwriting user-set preferences in RTD
                        // (2) Infinite update loop between read and write between device and RTD
                        updatePreferencesToFirebaseRTD(preference, value, context);

                    mInitializeGuard = false;
                    return true;
                }
            };

    // region Firebase RTD ChildEventListener

    private static ChildEventListener createNewChildEventListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (mIsSignedIn) {
                    Timber.d("Preference added from RTD: %s", dataSnapshot.getKey());
                    updatePreferencesFromFirebaseRTD(dataSnapshot);
                } else {
                    Timber.d("PreferenceChildEventListener/onChildAdded Called while user is not signed in. Doing nothing...");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (mIsSignedIn) {
                    Timber.d("Preference changed from RTD: %s", dataSnapshot.getKey());
                    updatePreferencesFromFirebaseRTD(dataSnapshot);
                } else {
                    Timber.d("PreferenceChildEventListener/onChildChanged Called while user is not signed in. Doing nothing...");
                }
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
        };
    }

    /**
     * Update Firebase RTD with the newly set preferences
     *
     * @param preference
     * @param newValue
     */
    private static void updatePreferencesToFirebaseRTD(Preference preference, Object newValue,
                                                       Context context) {
        if (AuthToolbox.isSignedIn()) {
            FirebaseDatabaseHelper.write_Preference(preference, newValue, context, true);
        }
    }

    /**
     * Updates SharedPreferences downloaded from Firebase RTD, and updates the UI of the new value
     *
     * @param snapshot
     */
    @SuppressLint("RestrictedApi")
    // https://stackoverflow.com/questions/41150995/appcompatactivity-oncreate-can-only-be-called-from-within-the-same-library-group
    private static void updatePreferencesFromFirebaseRTD(@NonNull DataSnapshot snapshot) {
        String key = snapshot.getKey();
        boolean click = false;
        if (key != null) {
            if (key.equals(mPreferenceNotifications.getKey()) ||
                    key.equals(mPreferenceCaptureBeep.getKey()) ||
                    key.equals(mPreferenceCaptureVibrate.getKey()) ||
                    key.equals(mPreferenceCaptureVoice.getKey())) {
                // Boolean cases. Auto-click the toggle only if old and new values are different
                try {
                    boolean newValue = (boolean) snapshot.getValue();
                    boolean oldValue = mSp.getBoolean(key, true);
                    if (newValue != oldValue) {
                        click = true;
                    }
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            } else if (key.equals(mPreferenceNotificationsNumDays.getKey()) ||
                    key.equals(mPreferenceNotificationsTod.getKey()) ||
                    key.equals(mPreferenceWidget.getKey()) ||
                    key.equals(mPreferenceDisplayName.getKey())) {
                // String cases
                try {
                    String value = (String) snapshot.getValue();
                    mSp.edit().putString(key, value).apply();
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            }

            // Update the preference UI

            // For the toggles, update the button state (and preference value) if changed.
            // performClick() also updates the Preference value, so no need to write the new value
            // to SP like we do for Strings
            if (key.equals(mPreferenceNotifications.getKey())) {
                if (click) mPreferenceNotifications.performClick();
            } else if (key.equals(mPreferenceCaptureBeep.getKey())) {
                if (click) mPreferenceCaptureBeep.performClick();
            } else if (key.equals(mPreferenceCaptureVibrate.getKey())) {
                if (click) mPreferenceCaptureVibrate.performClick();
            } else if (key.equals(mPreferenceCaptureVoice.getKey())) {
                if (click) mPreferenceCaptureVoice.performClick();
            }

            // For the Strings, update the summaries
            else if (key.equals(mPreferenceNotificationsNumDays.getKey())) {
                updatePreferenceValue(mPreferenceNotificationsNumDays);
            } else if (key.equals(mPreferenceNotificationsTod.getKey())) {
                updatePreferenceValue(mPreferenceNotificationsTod);
            } else if (key.equals(mPreferenceWidget.getKey())) {
                updatePreferenceValue(mPreferenceWidget);
            } else if (key.equals(mPreferenceDisplayName.getKey())) {
                updatePreferenceValue(mPreferenceDisplayName);
            }
        } else {
            Timber.d("Preference key was null");
        }
    }

    // endregion

    /**
     * Perform action right away based on the specific preference updated. Note at this point,
     * the new preference value has NOT yet been saved to the preference, so getting the
     * preference value directly from SharedPreferences will return the old value
     *
     * @param preference
     * @param context
     * @param value
     */
    private static void handleIndividualPreferenceChangeActions(Preference preference,
                                                                Context context, Object value) {
        if (preference.equals(mPreferenceDisplayName)) {
            String displayName = ((String) value).trim();
            if (AuthToolbox.isSignedIn()) {
                // Update to cloud only if signed in
                AuthToolbox.updateDisplayName(context, displayName);
            } else {
                AuthToolbox.updateDisplayName_SharedPreferences(context, displayName);
            }

            // If there is no name, set the summary to the default
            if (displayName.trim().isEmpty())
                preference.setSummary(R.string.pref_account_display_name_summary);
            else
                // Show the trimmed display name
                preference.setSummary(displayName);
        } else if (preference.equals(mPreferenceWidget)) {
            // Request update
            UpdateWidgetService.updateFoodWidget(preference.getContext());
        } else if (preference.equals(mPreferenceNotifications)) {
            // Update notification jobscheduler from switch toggle
            if (!mInitializeGuard)
                // Do not call when we've just initialized the OnPreferenceChangeListener
                scheduleNotifications(context, (boolean) value);
        } else if (preference.equals(mPreferenceNotificationsTod)) {
            // Handle notification jobschedule from tod change
            if (!mInitializeGuard)
                // Do not call when we've just initialized the OnPreferenceChangeListener
                scheduleNotifications(context, (String) value);
        }
    }

    /**
     * Helper to schedule the app's notifications only if they're enabled. Cancel scheduling
     * otherwise. Handle showing the first notification here, and then handle recurring
     * notifications through {@link NotificationHelper}
     */
    private static void scheduleNotifications(Context context, boolean enabled) {
        if (!enabled) {
            // Cancel the job if notifications are not enabled
            NotificationHelper.cancelNotificationJob(context);
        } else {
            NotificationHelper.scheduleNotificationJob(context, false);
        }
    }

    /**
     * Helper to schedule the app's notifications. Handle showing the first notification here, and
     * then handle recurring notifications through {@link NotificationHelper}
     */
    private static void scheduleNotifications(Context context, String todValue) {
        NotificationHelper.scheduleNotificationJob(context, false, todValue);
    }

    /**
     * Helper for setting the PreferenceSummary
     *
     * @param preference
     * @param value
     * @return
     */
    private static String getPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof MultiSelectListPreference) {
            // List all selected values
            List<String> selectedValues = new ArrayList<>((HashSet<String>) value);
            if (selectedValues.size() == 0) {
                // No values set
                return preference.getContext().getString(R.string.pref_multi_select_empty);
            } else {
                MultiSelectListPreference msListPreference = (MultiSelectListPreference) preference;
                List<CharSequence> summaryList = new ArrayList<>();
                CharSequence[] summaryValues = msListPreference.getEntries();
                CharSequence[] entryValues = msListPreference.getEntryValues();
                // Iterate through and only add selected values to summary
                for (int i = 0; i < entryValues.length; i++) {
                    for (String selectedValue : selectedValues) {
                        if (entryValues[i].equals(selectedValue)) {
                            summaryList.add(summaryValues[i]);
                        }
                    }
                }
                return TextUtils.join(", ", summaryList);
            }
        } else if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            return index >= 0
                    ? listPreference.getEntries()[index].toString()
                    : null;

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            return stringValue;
        }
    }

    /**
     * Helper for enabling or disabling a preference based on other preferences
     *
     * @param preference
     * @param value
     */
    private static void enablePreference(Preference preference, Object value) {
        if (preference.equals(mPreferenceNotifications)) {
            // Enable notification settings only if notifications are enabled
            boolean enabled = (boolean) value;
            mPreferenceNotificationsNumDays.setEnabled(enabled);
            mPreferenceNotificationsTod.setEnabled(enabled);
        }
    }

    /**
     * Shows a dialog for the user to confirm wiping device data or the user account
     */
    private void showWipeDataConfirmationDialog(ConfirmDeleteDialogFragment.DeleteType deleteType) {
        ConfirmDeleteDialogFragment dialog = ConfirmDeleteDialogFragment
                .newInstance(null, false, deleteType);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ConfirmDeleteDialogFragment.class.getSimpleName());
    }

    @Override
    public void onConfirmDeleteButtonClicked(int position, boolean isLoggedIn,
                                             ConfirmDeleteDialogFragment.DeleteType deleteType) {
        switch (position) {
            case Dialog.BUTTON_POSITIVE:
                // position or isLoggedIn does not matter here
                switch (deleteType) {
                    case ACCOUNT:
                        startDeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType.ACCOUNT,
                                mGoogleSignInClient);
                        break;
                    case DEVICE:
                        startDeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType.DEVICE,
                                mGoogleSignInClient);
                        break;
                }
        }
    }

    /**
     * AsyncTask that handles deleting food data in a background thread. Subsequent actions depend
     * on the {@link com.zn.expirytracker.ui.dialog.ConfirmDeleteDialogFragment.DeleteType} passed
     * into the constructor, as follows:
     * <ul>
     * <li>{@code ACCOUNT} - Deletes all device and cloud data, and deletes the user account</li>
     * <li>{@code DEVICE} - Deletes device data only, and shows a toast</li>
     * <li>{@code SIGN_OUT} - Deletes device data only, and signs the user out</li>
     * </ul>
     */
    private static class DeleteDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private ConfirmDeleteDialogFragment.DeleteType mDeleteType;
        private GoogleSignInClient mClient;

        DeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType deleteType,
                            GoogleSignInClient client) {
            mDeleteType = deleteType;
            mClient = client;
        }

        @Override
        protected void onPreExecute() {
            // TODO: Disable all view clicks and dim the activity while this is happening
            switch (mDeleteType) {
                case ACCOUNT:
                    Toolbox.showToast(mClient.getApplicationContext(),
                            "Deleting account...");
                    break;
                case DEVICE:
                    Toolbox.showToast(mClient.getApplicationContext(),
                            "Deleting app data on device...");
                    break;
            }
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mDeleteType) {
                case ACCOUNT:
                    AuthToolbox.deleteDeviceAndCloudData(mViewModel,
                            mClient.getApplicationContext());
                    break;
                default:
                    AuthToolbox.deleteDeviceData(mViewModel, mClient.getApplicationContext());
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            switch (mDeleteType) {
                case ACCOUNT:
                    AuthToolbox.deleteAccount(mClient.getApplicationContext(), mClient);
                    break;
                case DEVICE:
                    Toolbox.showToast(mClient.getApplicationContext(),
                            "All app data deleted from device");
                    break;
                case SIGN_OUT:
                    // Closes settings and clears back stack
                    AuthToolbox.signOut(mClient.getApplicationContext(), mClient);
                    break;
            }
        }
    }
}
