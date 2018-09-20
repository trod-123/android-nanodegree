package com.zn.expirytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.AuthToolbox;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(SignInActivity.class.getSimpleName());
        setContentView(R.layout.activity_sign_in);

        // Choose auth providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // Create and launch sign-in intent
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN);
        } else {
            // User is signed in, go to MainActivity
            startMainActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successful sign-in
                AuthToolbox.syncSignInWithDevice(this);
                startMainActivity();
            } else {
                // Sign in failed
                if (response == null) {
                    // user cancelled using the back button. Force leave the app
                    finish();
                } else {
                    // handle the error
                    Timber.e(response.getError(), "There was an error while signing in: %s",
                            response.getError().getMessage());
                    int errorCode = response.getError().getErrorCode();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
