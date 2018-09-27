package com.zn.expirytracker.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zn.expirytracker.utils.AuthToolbox;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start sign-in if user is not signed-in
        if (!AuthToolbox.isSignedIn()) {
            AuthToolbox.startSignInActivity(this);

            return;
        }

        // Typical pattern to add fragment as main content in activity
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
