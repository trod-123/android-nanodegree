package com.thirdarm.footballscores;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutFragment())
                    .commit();
        }
    }

    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_about, container, false);
        }
    }

    /**
     * System calls this method to get parent activity intent for up button behavior. By adding
     *  the Intent.FLAG_ACTIVITY_CLEAR_TOP flag, allows system to check if main activity is already
     *  running in task, and to use that main activity instead of creating a new instance.
     * The annotation TargetApi is displayed because this method did not exist before Jelly Bean.
     * @return Intent directing to the main activity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
