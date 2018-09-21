package com.zn.expirytracker.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.OnEditClearErrorsTextWatcher;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @BindView(R.id.layout_sign_up_root)
    View mRootView;
    @BindView(R.id.overlay_sign_up_no_click)
    View mNoClickOverlay;
    @BindView(R.id.til_sign_up_name)
    TextInputLayout mTilName;
    @BindView(R.id.tiEt_sign_up_name)
    TextInputEditText mEtName;
    @BindView(R.id.til_sign_up_email)
    TextInputLayout mTilEmail;
    @BindView(R.id.tiEt_sign_up_email)
    TextInputEditText mEtEmail;
    @BindView(R.id.til_sign_up_password)
    TextInputLayout mTilPassword;
    @BindView(R.id.tiEt_sign_up_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.btn_sign_up_signup)
    Button mBtnSignup;
    @BindView(R.id.pb_sign_up_signup)
    ProgressBar mPbSignup;
    @BindView(R.id.btn_sign_up_existing_account)
    Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(SignUpActivity.class.getSimpleName());
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        mBtnSignup.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mNoClickOverlay.setOnClickListener(this);

        mEtName.addTextChangedListener(new OnEditClearErrorsTextWatcher(mTilName));
        mEtEmail.addTextChangedListener(new OnEditClearErrorsTextWatcher(mTilEmail));
        mEtPassword.addTextChangedListener(new OnEditClearErrorsTextWatcher(mTilPassword));

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up_signup:
                String name = mEtName.getText().toString();
                String email = mEtEmail.getText().toString();
                String password = mEtPassword.getText().toString();
                if (areInputsValid(name, email, password)) {
                    signUpWithEmail(name, email, password);
                }
                break;
            case R.id.btn_sign_up_existing_account:
                AuthToolbox.startSignInActivity(this);
                break;
            case R.id.overlay_sign_up_no_click:
                // Prevent click-handling for root view
                break;
        }
    }

    /**
     * Validates name, e-mail and password fields and shows an error if not valid
     * <p>
     * 1) Name must not be empty
     * <p>
     * 2) E-mail address must be in a valid form: ***@***.***
     * <p>
     * 3) Password must have at least 8 characters and must not contain any spaces
     *
     * @return {@code true} if all inputs are valid
     */
    private boolean areInputsValid(String name, String email, String password) {
        boolean valid = true;
        if (!AuthToolbox.isNameValid(name, mTilName, this)) valid = false;
        if (!AuthToolbox.isEmailValid(email, mTilEmail, this)) valid = false;
        if (!AuthToolbox.isPasswordValid(password, mTilPassword, this)) valid = false;

        return valid;
    }

    /**
     * Creates a new account
     *
     * @param email
     * @param password
     */
    private void signUpWithEmail(final String name, String email, String password) {
        AuthToolbox.showLoadingOverlay(true, mNoClickOverlay, mPbSignup);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // New account is created successfully, also sign the user in
                            Timber.d("Create user with email: success!");
                            AuthToolbox.syncSignInWithDevice_EmailAuth(getApplicationContext(),
                                    mAuth.getCurrentUser(), name);
                            AuthToolbox.startMainActivity(getApplicationContext());
                        } else {
                            // Sign up failed
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mTilPassword.setError(getString(R.string.auth_error_weak_password));
                                mTilPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mTilEmail.setError(getString(R.string.auth_error_invalid_email));
                                mTilEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                mTilEmail.setError(getString(R.string.auth_error_user_exists));
                                mTilEmail.requestFocus();
                            } catch (Exception e) {
                                Timber.w(e, "Sign up with email: failed");
                            }
                            Toolbox.showSnackbarMessage(mRootView,
                                    "There was a problem creating your account.");
                        }
                        AuthToolbox.showLoadingOverlay(false, mNoClickOverlay, mPbSignup);
                    }
                });
    }
}
