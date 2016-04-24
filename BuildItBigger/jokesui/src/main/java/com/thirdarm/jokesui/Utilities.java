package com.thirdarm.jokesui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

import java.util.Random;

/**
 * Created by TROD on 20160417.
 */
public class Utilities {

    // TODO: Make the factor more complex than simple randomness
    private static final int PROBABILITY_INTER_AD = 2; // 20% chance

    /** Checks network connection */
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Shares a message
     * @param context Context used to start share activity
     * @param message The message to be shared
     */
    public static void shareText(Context context, String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(shareIntent);
    }

    /**
     * Helper method to get the Jokes Fragment instance
     */
    public static JokesFragment getJokesFragment(FragmentActivity activity) {
        return (JokesFragment) activity.getSupportFragmentManager()
                .findFragmentByTag(Constants.TAGS.FRAGMENT_JOKES);
    }

    /**
     * Randomly decides whether to show an interstitial ad before showing jokes
     * <p>
     * For free version use only
     * @return True if factor generated is less than <code>PROBABILITY_INTER_AD</code>
     */
    public static boolean decideInterstitialAd() {
        // Generate random int [0,10)
        int factor = new Random().nextInt(10);
        return factor < PROBABILITY_INTER_AD;
    }
}
