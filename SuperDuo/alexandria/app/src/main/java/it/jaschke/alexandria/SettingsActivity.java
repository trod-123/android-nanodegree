package it.jaschke.alexandria;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by saj on 27/01/15.
 *
 * TODO: Add more settings and redesign the layout to that it also includes a title bar. Also,
 * display the current value of a setting as a subtitle.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


    }
}
