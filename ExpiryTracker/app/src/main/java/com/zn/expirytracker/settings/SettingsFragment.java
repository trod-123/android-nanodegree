package com.zn.expirytracker.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.firebase.FirebaseUpdaterHelper;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.IntroActivity;
import com.zn.expirytracker.ui.dialog.ConfirmDeleteDialogFragment;
import com.zn.expirytracker.ui.notifications.NotificationHelper;
import com.zn.expirytracker.ui.widget.UpdateWidgetService;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.constants.DebugFields;
import com.zn.expirytracker.utils.Toolbox;
import com.zn.expirytracker.constants.Urls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import timber.log.Timber;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

/**
 * App settings interface
 * <p>
 * <p>
 * Note: {@link SettingsActivity} manifest declares
 * {@code configChanges="orientation|keyboardHidden|screenSize"}
 * to ensure Activity does not recreate itself after rotations. This is to ensure that any
 * Account or Data delete operations do not occur multiple times when user manages to rotate device
 * during delete operations. It is OK to do this here since we do not have alternate resource
 * layouts for a Preference fragment
 */
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
    static Preference mPreferenceClearCache;
    static Preference mPreferenceAccountDelete;
    static Preference mPreferenceDisplayName;
    static Preference mPreferenceWipeDeviceData;
//    static Preference mPreferenceWelcome;
    static Preference mPreferencePrivacyPolicy;
    static Preference mPreferenceEula;
    static Preference mPreferenceOpenSourceLicenses;
    static Preference mPreferenceCrashlyticsEnabled;
    static Preference mPreferenceContact;
    static Preference mPreferenceVersion;

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
        mPreferenceClearCache = findPreference(getString(R.string.pref_delete_cache_key));
        mPreferenceAccountDelete = findPreference(getString(R.string.pref_account_delete_key));
        mPreferenceDisplayName = findPreference(getString(R.string.pref_account_display_name_key));
        mPreferenceWipeDeviceData = findPreference(getString(R.string.pref_account_wipe_data_key));
//        mPreferenceWelcome = findPreference(getString(R.string.pref_about_welcome_key));
        mPreferencePrivacyPolicy = findPreference(getString(R.string.pref_about_privacy_policy_key));
        mPreferenceEula = findPreference(getString(R.string.pref_about_eula_key));
        mPreferenceOpenSourceLicenses = findPreference(getString(R.string.pref_about_licenses_key));
        mPreferenceCrashlyticsEnabled = findPreference(getString(R.string.pref_about_crashlytics_optin_key));
        mPreferenceContact = findPreference(getString(R.string.pref_about_contact_key));
        mPreferenceVersion = findPreference(getString(R.string.pref_about_version_key));

        // Set icons, tinted
        Context context = getContext();
        int tint = ContextCompat.getColor(context, R.color.icon_tint_light_background);
        setPreferenceIcon(mPreferenceNotifications, context, tint, R.drawable.ic_alert_outline_white_24dp);
        setPreferenceIcon(mPreferenceNotificationsNumDays, context, tint, R.drawable.ic_calendar_white_24dp);
        setPreferenceIcon(mPreferenceNotificationsTod, context, tint, R.drawable.ic_clock_outline_white_24dp);
        setPreferenceIcon(mPreferenceWidget, context, tint, R.drawable.ic_widget_white_24dp);
        setPreferenceIcon(mPreferenceCaptureBeep, context, tint, R.drawable.ic_volume_high_white_24dp);
        setPreferenceIcon(mPreferenceCaptureVibrate, context, tint, R.drawable.ic_vibrate_white_24dp);
        setPreferenceIcon(mPreferenceCaptureVoice, context, tint, R.drawable.ic_mic_white_24dp);
        setPreferenceIcon(mPreferenceAccountSignOut, context, tint, R.drawable.ic_logout_white_24dp);
        setPreferenceIcon(mPreferenceAccountSignIn, context, tint, R.drawable.ic_login_white_24dp);
        setPreferenceIcon(mPreferenceDisplayName, context, tint, R.drawable.ic_contact_outline_white_24dp);
        setPreferenceIcon(mPreferenceClearCache, context, tint, R.drawable.ic_delete_folder_outline_white_24dp);
        setPreferenceIcon(mPreferenceWipeDeviceData, context, tint, R.drawable.ic_delete_outline_white_24dp);
        setPreferenceIcon(mPreferenceAccountDelete, context, tint, R.drawable.ic_delete_forever_outline_white_24dp);
//        setPreferenceIcon(mPreferenceWelcome, context, tint, R.drawable.ic_human_greeting_white_24dp);
        setPreferenceIcon(mPreferencePrivacyPolicy, context, tint, R.drawable.ic_vpn_white_24dp);
        setPreferenceIcon(mPreferenceEula, context, tint, R.drawable.ic_clipboard_outline_white_24dp);
        setPreferenceIcon(mPreferenceOpenSourceLicenses, context, tint, R.drawable.ic_contact_multiple_outline_white_24dp);
        setPreferenceIcon(mPreferenceCrashlyticsEnabled, context, tint, R.drawable.ic_bug_white_24dp);
        setPreferenceIcon(mPreferenceContact, context, tint, R.drawable.ic_message_outline_white_24dp);

        // Set summaries and enabled based on switches or checkboxes
        setOnPreferenceChangeListener(mPreferenceNotifications);
        setOnPreferenceChangeListener(mPreferenceNotificationsNumDays);
        setOnPreferenceChangeListener(mPreferenceNotificationsTod);
        setOnPreferenceChangeListener(mPreferenceWidget);
        setOnPreferenceChangeListener(mPreferenceCaptureBeep);
        setOnPreferenceChangeListener(mPreferenceCaptureVibrate);
        setOnPreferenceChangeListener(mPreferenceCaptureVoice);
        setOnPreferenceChangeListener(mPreferenceDisplayName);
        setOnPreferenceChangeListener(mPreferenceCrashlyticsEnabled);

        // Set the behavior for the custom preferences
        setAccountPreferencesActions();
        if (AuthToolbox.isSignedIn()) disableDemoAccountSettings(AuthToolbox.getUserId());

        setVersionInfo();
    }

    /**
     * Sets an icon for a preference
     *
     * @param preference
     * @param context
     * @param tintColor
     * @param iconResId
     */
    private void setPreferenceIcon(Preference preference, Context context, int tintColor, int iconResId) {
        Drawable icon = context.getDrawable(iconResId);
        icon.setTint(tintColor);
        preference.setIcon(icon);
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
     * Helper to activate all account settings
     *
     * @param activate
     */
    private static void activateAccountSettings(boolean activate) {
        mPreferenceDisplayName.setEnabled(activate);
        mPreferenceAccountSignIn.setEnabled(activate);
        mPreferenceAccountSignOut.setEnabled(activate);
//        mPreferenceAccountSync.setEnabled(activate);
        mPreferenceAccountDelete.setEnabled(activate);
        mPreferenceWipeDeviceData.setEnabled(activate);
        mPreferenceClearCache.setEnabled(activate);
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
        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (preference.equals(mPreferenceAccountSignIn)) {
                    AuthToolbox.startSignInActivity(mHostActivity, false);
                    // Closes settings and clears back stack
                    return true;
                } else if (preference.equals(mPreferenceAccountSignOut)) {
                    mPreferenceAccountSignOut.setEnabled(false);
                    startDeleteDataAsyncTask(ConfirmDeleteDialogFragment.DeleteType.SIGN_OUT,
                            mGoogleSignInClient);
                    return true;
                } else if (preference.equals(mPreferenceClearCache)) {
                    clearImageCache();
                    return true;
                } else if (preference.equals(mPreferenceAccountDelete)) {
                    // Only show the wipe feature if current account is not the demo one
                    showWipeDataConfirmationDialog(ConfirmDeleteDialogFragment.DeleteType.ACCOUNT);
                    // Delete handled in ConfirmDeleteDialogFragment.onConfirmDeleteButtonClicked
                    return true;
                } else if (preference.equals(mPreferenceWipeDeviceData)) {
                    // Only show wipe device data if user is not logged in
                    showWipeDataConfirmationDialog(ConfirmDeleteDialogFragment.DeleteType.DEVICE);
                    // Delete handled in ConfirmDeleteDialogFragment.onConfirmDeleteButtonClicked
                    return true;
//                } else if (preference.equals(mPreferenceWelcome)) {
//                    showWelcomeActivity(mPreferenceWelcome);
//                    return true;
                } else if (preference.equals(mPreferencePrivacyPolicy)) {
                    Toolbox.startWebViewActivity(preference.getContext(),
                            preference.getTitle().toString(),
                            Urls.URL_PRIVACY_POLICY);
                    return true;
                } else if (preference.equals(mPreferenceEula)) {
                    Toolbox.startWebViewActivity(preference.getContext(),
                            preference.getTitle().toString(),
                            Urls.URL_EULA);
                    return true;
                } else if (preference.equals(mPreferenceOpenSourceLicenses)) {
                    viewOpenSourceLicenses(preference);
                    return true;
                } else if (preference.equals(mPreferenceContact)) {
                    contactDeveloper(preference);
                    return true;
                }
                return false;
            }
        };
        mPreferenceAccountSignIn.setOnPreferenceClickListener(listener);
        mPreferenceAccountSignOut.setOnPreferenceClickListener(listener);
        mPreferenceClearCache.setOnPreferenceClickListener(listener);
        mPreferenceAccountDelete.setOnPreferenceClickListener(listener);
        mPreferenceWipeDeviceData.setOnPreferenceClickListener(listener);
//        mPreferenceWelcome.setOnPreferenceClickListener(listener);
        mPreferencePrivacyPolicy.setOnPreferenceClickListener(listener);
        mPreferenceEula.setOnPreferenceClickListener(listener);
        mPreferenceOpenSourceLicenses.setOnPreferenceClickListener(listener);
        mPreferenceContact.setOnPreferenceClickListener(listener);

        if (AuthToolbox.isSignedIn())
            mPreferenceAccountSignOut.setSummary(AuthToolbox.getUserEmail());
//        mPreferenceAccountSync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                // TODO: Sync Room and Firebase here
//                return false;
//            }
//        });
    }

    /**
     * Sets the version name for {@link SettingsFragment#mPreferenceVersion}
     */
    private void setVersionInfo() {
        Context context = mPreferenceVersion.getContext();
        try {
            String version = Toolbox.getAppVersionName(context);
            mPreferenceVersion.setSummary(getString(R.string.version_num, version));
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Error thrown while setting version in SettingsFragment");
        }
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
        }

        if (!mInitializeGuard) {
            // Do not call when we've just initialized the OnPreferenceChangeListener
            if (preference.equals(mPreferenceNotifications)) {
                // Update notification jobscheduler from switch toggle
                scheduleNotifications(context, (boolean) value);
            } else if (preference.equals(mPreferenceNotificationsTod)) {
                // Handle notification jobschedule from tod change
                scheduleNotifications(context, (String) value);
            } else if (preference.equals(mPreferenceCrashlyticsEnabled)) {
                // Do not call when we've just initialized the OnPreferenceChangeListener
                Toolbox.showToast(context, context.getString(R.string.message_restart_required));
            }
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
            Toolbox.showToast(context, context.getString(R.string.notification_disable));
        } else {
            NotificationHelper.scheduleNotificationJob(context, false);
            Toolbox.showToast(context, context.getString(R.string.notification_enable));
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

    /**
     * Clears the glide image cache and displays a message when finished
     */
    private void clearImageCache() {
        mPreferenceClearCache.setEnabled(false);
        AuthToolbox.deleteImageCache(mHostActivity, new AuthToolbox.ImageCacheClearedListener() {
            @Override
            public void onImageCacheCleared() {
                Toolbox.showToast(mHostActivity,
                        getString(R.string.message_image_cache_deleted));
                mPreferenceClearCache.setEnabled(true);
            }
        });
    }

    private static void showWelcomeActivity(Preference preference) {
        Context context = preference.getContext();
        context.startActivity(new Intent(context, IntroActivity.class));
    }

    /**
     * Helper for starting the {@link OssLicensesMenuActivity}
     *
     * @param preference
     */
    private static void viewOpenSourceLicenses(Preference preference) {
        Context context = preference.getContext();
        OssLicensesMenuActivity.setActivityTitle(preference.getTitle().toString());
        context.startActivity(new Intent(context, OssLicensesMenuActivity.class));
    }

    /**
     * Helper for starting an e-mail activity for contacting the developer
     *
     * @param preference
     */
    private void contactDeveloper(Preference preference) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String emailData = "mailto:" + preference.getSummary() +
                "?subject=" + Uri.encode(getString(R.string.contact_email_subject)) +
                "&body=" + Uri.encode(generateEmailBody());
        intent.setData(Uri.parse(emailData));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toolbox.showToast(mHostActivity, getString(R.string.contact_email_no_app));
        }
    }

    /**
     * Generates the e-mail body for contact
     *
     * @return
     */
    private String generateEmailBody() {
        // Get hardware metrics
        DisplayMetrics dm = new DisplayMetrics();
        mHostActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int densityDpi = (int) (dm.density * 160f);

        String app_version;
        try {
            app_version = "App version: " + Toolbox.getAppVersionName(mHostActivity);
        } catch (PackageManager.NameNotFoundException e) {
            app_version = "App version: error";
            Timber.e(e);
        }
        String manufacturer = "Manufacturer: " + Build.MANUFACTURER;
        String brand = "Brand: " + Build.BRAND;
        String model = "Model: " + Build.MODEL;
        String board = "Board: " + Build.BOARD;
        String hardware = "Hardware: " + Build.HARDWARE;
        String screen_density = "Screen density: " + String.valueOf(densityDpi) + " dpi";
        String version = "Version: " + Build.VERSION.RELEASE;
        String api_level = "SDK: " + Build.VERSION.SDK_INT;

        return new StringBuilder()
                .append("\n\n\n")
                .append(getString(R.string.contact_email_body))
                .append("\n\n")
                .append(app_version)
                .append("\n")
                .append(manufacturer)
                .append("\n")
                .append(brand)
                .append("\n")
                .append(model)
                .append("\n")
                .append(board)
                .append("\n")
                .append(hardware)
                .append("\n")
                .append(screen_density)
                .append("\n")
                .append(version)
                .append("\n")
                .append(api_level).toString();
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
            activateAccountSettings(false);
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
                    activateAccountSettings(true);
                    break;
                case SIGN_OUT:
                    // Closes settings and clears back stack
                    AuthToolbox.signOut(mClient.getApplicationContext(), mClient);
                    break;
            }
        }
    }
}
