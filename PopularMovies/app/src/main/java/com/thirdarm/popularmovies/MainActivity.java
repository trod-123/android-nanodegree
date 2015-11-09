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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thirdarm.popularmovies.sync.MoviesSyncAdapter;
import com.thirdarm.popularmovies.utilities.Network;

public class MainActivity extends AppCompatActivity
        implements PostersFragment.Callback {

    private static final String LOG_TAG = "MainActivity";

    // Will be used in onResume()
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    // To store whether UI is single- or dual-pane
    private static final String TWO_PANE = "twoPane";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if single- or dual-pane layout. If dual, load up DetailFragment on right pane
        if (findViewById(R.id.container_fragment_movie_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment_movie_detail, new EmptyDetailFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        // Make sure there is internet connection first before going to sync
        if (Network.isNetworkAvailable(this)) {
            MoviesSyncAdapter.initializeSyncAdapter(this);
        } else {
            Toast.makeText(this, getString(R.string.status_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Implement preferences activity later.

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Handles posters fragment callbacks and loads up appropriate detail activity based on
     *  movie poster selection in the right panel, or in a new activity
     * @param movieUri The uri of the movie selected by the user
     */
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // Store uri in a parcelable which is stored in a bundle
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.MOVIE_URI, movieUri);
            // Create fragment and store bundle in fragment
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            // Add the fragment and tag fragment (only done so if fragment is created through main
            //  activity so it can communicate with the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_movie_detail, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            // Launch detail activity and add fragment there instead
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.MOVIE_URI, movieUri);
            startActivity(intent);
        }
    }
}
