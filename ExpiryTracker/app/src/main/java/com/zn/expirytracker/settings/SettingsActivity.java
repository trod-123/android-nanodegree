package com.zn.expirytracker.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.zn.expirytracker.ui.MainActivity;
import com.zn.expirytracker.utils.Toolbox;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Override onBackPressed() to simulate going back to MainActivity if user got here via
     * widget or notification
     */
    public static final String EXTRA_LAUNCHED_EXTERNALLY = Toolbox.createStaticKeyString(
            SettingsActivity.class, "launched_externally");

    /**
     * If the user got here by widget or notification, override onBackPressed() to take the user
     * to MainActivity, mimicking a "fake" backstack to simulate a unified app experience
     */
    private boolean mLaunchedExternally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mLaunchedExternally = extras.containsKey(EXTRA_LAUNCHED_EXTERNALLY);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Emulate the back button when pressing the up button, to prevent parent activity from
            // getting recreated
            // https://stackoverflow.com/questions/22947713/make-the-up-button-behave-like-the-back-button-on-android
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLaunchedExternally) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
