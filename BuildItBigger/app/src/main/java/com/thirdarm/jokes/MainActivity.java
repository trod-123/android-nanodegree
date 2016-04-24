package com.thirdarm.jokes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.thirdarm.jokesui.Constants;
import com.thirdarm.jokesui.JokesFragment;
import com.thirdarm.jokesui.Utilities;

public class MainActivity extends AppCompatActivity
        implements JokesFragment.JokeClickCallback {

    private Toast mToast;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Load the fragment from the jokesui library
            JokesFragment fragment = JokesFragment
                    .newInstance(Integer.parseInt(getString(R.string.flavorVariant)));
            fragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, Constants.TAGS.FRAGMENT_JOKES)
                    .commit();

            // Load the ad only in the free flavor
            if (checkIfFreeFlavor()) {
                // Load the banner ad
                AdView mAdView = (AdView) findViewById(R.id.adView);
                // Create an ad request. Check logcat output for the hashed device ID to
                // get test ads on a physical device. e.g.
                // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                if (mAdView != null) {
                    mAdView.loadAd(adRequest);
                }
            }
        }

        // TODO: Create a nav drawer that displays a list of all the jokes which the user can freely
        // choose from for his/her own viewing pleasure.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load the interstitial ad whenever activity resumes focus
        if (checkIfFreeFlavor()) {
            loadInterstitialAd();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jokes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                String joke = Utilities.getJokesFragment(this).getCurrentJoke();
                if (joke != null) {
                    Utilities.shareText(this, joke);
                } else {
                    showToast(getString(R.string.network_share_joke_get_null));
                }
                return true;
            case R.id.action_reset:
                Utilities.getJokesFragment(this).openResetDialog();
                return true;
            case R.id.action_about:
                Utilities.getJokesFragment(this).openAboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method for making a toast
     * @param message Message to be toasted
     */
    public void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }


    /*
        Interstitial Ad stuff
     */

    /**
     * Checks to see if flavor is free or paid
     * @return <code>True</code> if free
     */
    public boolean checkIfFreeFlavor() {
        return getString(R.string.flavorVariant).equals("0");
    }

    /**
     * <p>
     * Callback method received from JokesFragment from the Jokes UI library
     * </p>
     * Randomly loads an interstitial ad, if free version, then loads a new activity
     * containing the joke without all that added clutter
     *
     * @param intent Intent containing joke and new activity data
     */
    @Override
    public void onJokeClick(Intent intent) {
        if (checkIfFreeFlavor() && Utilities.decideInterstitialAd()) {
            showInterstitialAd();
        }
        startActivity(intent);
    }

    /**
     * Helper method to load the interstitial ad
     */
    public void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Show the interstitial ad
     */
    public void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}