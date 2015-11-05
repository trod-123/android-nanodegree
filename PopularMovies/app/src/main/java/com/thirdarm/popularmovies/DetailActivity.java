/*
 * Copyright (C) 2015 Teddy Rodriguez (TROD)
 *   email: cia.123trod@gmail.com
 *   github: TROD-123
 *
 * For Udacity's Android Developer Nanodegree
 * P1-2: Popular Movies
 *
 * Currently for educational purposes only.
 */

package com.thirdarm.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This activity is only used in single-pane mode, for handsets. It is not loaded in dual-pane
 *  mode, for larger screen devices such as tablets.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create detail fragment
            Bundle arguments = new Bundle();
            // Load intent data into parcelable
            arguments.putParcelable(DetailFragment.MOVIE_URI, getIntent().getData());
            // Create fragment
            DetailFragment fragment = new DetailFragment();
            // Bind data into fragment
            fragment.setArguments(arguments);
            // Add fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_fragment_movie_detail, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * System calls this method to get parent activity intent for up button behavior. By adding
     *  the Intent.FLAG_ACTIVITY_CLEAR_TOP flag, allows system to check if main activity is already
     *  running in task, and to use that main activity instead of creating a new instance when user
     *  presses the back button.
     *
     * The annotation TargetApi is displayed because this method did not exist before Jelly Bean.
     *
     * @return Intent directing to the main activity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
