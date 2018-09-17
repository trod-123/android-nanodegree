package com.zn.expirytracker.settings;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.dialog.ConfirmDeleteDialogFragment;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.widget.UpdateWidgetService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat
        implements ConfirmDeleteDialogFragment.OnConfirmDeleteButtonClickListener {

    static Preference mPreferenceNotificationsNumDays;
    static Preference mPreferenceNotificationsTod;
    static Preference mPreferenceWidget;
    static Preference mPreferenceAccountSignIn;
    static Preference mPreferenceAccountSignOut;
    static Preference mPreferenceAccountSync;
    static Preference mPreferenceAccountDelete;
    static Preference mPreferenceDisplayName;
    static Preference mPreferenceWipeDeviceData;

    private static FoodViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Keep a reference to preferences that will be enabled or disabled
        mPreferenceNotificationsNumDays =
                findPreference(getString(R.string.pref_notifications_days_key));
        mPreferenceNotificationsTod =
                findPreference(getString(R.string.pref_notifications_tod_key));
        mPreferenceWidget = findPreference(getString(R.string.pref_widget_num_days_key));
        mPreferenceAccountSignIn = findPreference(getString(R.string.pref_account_sign_in_key));
        mPreferenceAccountSignOut = findPreference(getString(R.string.pref_account_sign_out_key));
        mPreferenceAccountSync = findPreference(getString(R.string.pref_account_sync_key));
        mPreferenceAccountDelete = findPreference(getString(R.string.pref_account_delete_key));
        mPreferenceDisplayName = findPreference(getString(R.string.pref_account_display_name_key));
        mPreferenceWipeDeviceData = findPreference(getString(R.string.pref_account_wipe_data_key));

        // Set summaries and enabled based on switches or checkboxes
        setOnPreferenceChangeListener(findPreference(
                getString(R.string.pref_notifications_receive_key)));
        setOnPreferenceChangeListener(mPreferenceNotificationsNumDays);
        setOnPreferenceChangeListener(mPreferenceNotificationsTod);
        setOnPreferenceChangeListener(findPreference(
                getString(R.string.pref_notifications_days_key)));
        setOnPreferenceChangeListener(findPreference(
                getString(R.string.pref_widget_num_days_key)));
        setOnPreferenceChangeListener(findPreference(
                getString(R.string.pref_account_display_name_key)));

        // Refresh widget
        mPreferenceWidget.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Request update
                UpdateWidgetService.updateFoodWidget(preference.getContext());
                return true;
            }
        });

        // Set the behavior for the custom preferences
        setAccountPreferencesActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show account settings only if the user is signed in
        showAccountSettings(AuthToolbox.checkIfSignedIn());
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
        // TODO: Only show display name if user is logged in
        mPreferenceDisplayName.setVisible(show);
        mPreferenceAccountSignIn.setVisible(!show);
        mPreferenceAccountSignOut.setVisible(show);
        boolean hasAccount = mPreferenceAccountSignOut.isVisible();
        mPreferenceAccountSync.setVisible(hasAccount);
        mPreferenceAccountDelete.setVisible(hasAccount);
        // TODO: Scroll automatically to the newly visible settings
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
                // TODO: Launch the Sign-In activity here
                AuthToolbox.signIn(preference.getContext(), true);
                showAccountSettings(AuthToolbox.checkIfSignedIn());
                return false;
            }
        });
        mPreferenceAccountSignOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: Sign out the user here
                AuthToolbox.signIn(preference.getContext(), false);
                showAccountSettings(AuthToolbox.checkIfSignedIn());
                return false;
            }
        });
        mPreferenceAccountSync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: Sync Room and Firebase here
                return false;
            }
        });
        mPreferenceAccountDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: Delete the user's account here (including all local and remote data)
                return false;
            }
        });
        mPreferenceWipeDeviceData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showWipeDeviceDataConfirmationDialog();
                return true;
            }
        });
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
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
                        enablePreference(preference, value, context);
                    }

                    if (preference.getKey()
                            .equals(context.getString(R.string.pref_account_display_name_key))) {
                        // Sync the display name with the database
                        String displayName = (String) value;
                        AuthToolbox.updateDisplayName(displayName);

                        // If there is no name, set the summary to the default
                        if (displayName.trim().isEmpty())
                            preference.setSummary(R.string.pref_account_display_name_summary);
                    }
                    return true;
                }
            };

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
    private static void enablePreference(Preference preference, Object value, Context context) {
        if (preference.getKey()
                .equals(context.getString(R.string.pref_notifications_receive_key))) {
            // Enable notification settings only if notifications are enabled
            boolean enabled = (boolean) value;
            mPreferenceNotificationsNumDays.setEnabled(enabled);
            mPreferenceNotificationsTod.setEnabled(enabled);
        }
    }

    /**
     * Shows a dialog for the user to confirm wiping device data
     */
    private void showWipeDeviceDataConfirmationDialog() {
        ConfirmDeleteDialogFragment dialog = ConfirmDeleteDialogFragment
                .newInstance(null, false, true);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ConfirmDeleteDialogFragment.class.getSimpleName());
    }

    @Override
    public void onConfirmDeleteButtonClicked(int position) {
        switch (position) {
            case Dialog.BUTTON_POSITIVE:
                mViewModel.deleteAllFoods();
                break;
        }
    }
}
