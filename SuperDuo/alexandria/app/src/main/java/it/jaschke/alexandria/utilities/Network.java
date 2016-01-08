package it.jaschke.alexandria.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.jaschke.alexandria.R;

/**
 * Created by TROD on 20160105.
 *
 * Class containing networking methods.
 */
public class Network {

    // For transmitting sync status messages with the ui. Stored in and accessed through
    //  SharedPreferences.
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SYNC_STATUS_OK, SYNC_STATUS_NO_NETWORK, SYNC_STATUS_SERVER_DOWN, SYNC_STATUS_SERVER_INVALID,
            SYNC_STATUS_UNKNOWN, SYNC_STATUS_NO_RESULTS})
    public @interface SyncStatus {}

    public static final int SYNC_STATUS_OK = 0;             /* Status okay. Fetch successful */
    public static final int SYNC_STATUS_NO_NETWORK = 1;     /* User has no internet connection */
    public static final int SYNC_STATUS_SERVER_DOWN = 2;    /* Server is slow or down */
    public static final int SYNC_STATUS_SERVER_INVALID = 3; /* Server not returning valid data */
    public static final int SYNC_STATUS_NO_RESULTS = 4;     /* User's query returned no results */
    public static final int SYNC_STATUS_UNKNOWN = 5;        /* ... I just don't know */


    /** Checks network connection */
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Sets the sync status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     *
     * @param c Context to get the PreferenceManager from.
     * @param syncStatus The IntDef value to set
     */
    public static void setSyncStatus(Context c, @SyncStatus int syncStatus){

        // Force a change in sync status stored in the shared preferences to an arbitrary value
        //  to call the OnSharedPreferenceChangeListener in the pertaining fragment that requested
        //  the sync to disable the swipe refresh animation.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp
                .edit()
                .putInt(c.getString(R.string.sp_sync_status_key), 100)
                .commit();
        // Set the sync status to the correct status
        sp
                .edit()
                .putInt(c.getString(R.string.sp_sync_status_key), syncStatus)
                .commit(); /* Use commit since this will be called in bg thread */
    }

    /**
     * Gets the sync status
     * @param c Context used to get the SharedPreferences
     * @return the sync status integer type
     */
    @SuppressWarnings("ResourceType")
    public static @Network.SyncStatus int getSyncStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.sp_sync_status_key), Network.SYNC_STATUS_UNKNOWN);
    }

    /**
     * Shares a message
     * @param context Context used to start share activity
     * @param shareText The text to be shared
     */
    public static void shareText(Context context, String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(shareIntent);
    }

    /**
     * Launches url in browser
     * @param context Context used to start browser activity
     * @param url The url
     */
    public static void openInBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
