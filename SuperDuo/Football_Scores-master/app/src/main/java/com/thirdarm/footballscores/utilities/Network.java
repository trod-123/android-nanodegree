package com.thirdarm.footballscores.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.sync.ScoresSyncAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by TROD on 20151221.
 *
 * Class for Networking
 */
public class Network {

    /** Checks network connection */
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Gets the sync status
     *
     * @param c Context used to get the SharedPreferences
     * @return the sync status integer type
     */
    @SuppressWarnings("ResourceType")
    public static @SyncStatus
    int getSyncStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.sp_sync_status_key), SYNC_STATUS_UNKNOWN);
    }

    /**
     * Sets the sync status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     *
     * @param c          Context to get the PreferenceManager from.
     * @param syncStatus The IntDef value to set
     */
    public static void setSyncStatus(Context c, @SyncStatus int syncStatus) {
        // Notify the ScoresWidgetProvider that the sync is complete
        Intent dataUpdatedIntent = new Intent(ScoresSyncAdapter.ACTION_DATA_UPDATED);
        c.sendBroadcast(dataUpdatedIntent);

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

    // For transmitting sync status messages with the ui. Stored in and accessed through
    //  SharedPreferences.
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Network.SYNC_STATUS_OK, Network.SYNC_STATUS_NO_NETWORK,
            Network.SYNC_STATUS_SERVER_DOWN, Network.SYNC_STATUS_SERVER_INVALID,
            Network.SYNC_STATUS_UNKNOWN, Network.SYNC_STATUS_SYNCING})
    public @interface SyncStatus {
    }

    public static final int SYNC_STATUS_OK = 0;             /* Status okay. Fetch successful */
    public static final int SYNC_STATUS_NO_NETWORK = 1;     /* User has no internet connection */
    public static final int SYNC_STATUS_SERVER_DOWN = 2;    /* Server is slow or down */
    public static final int SYNC_STATUS_SERVER_INVALID = 3; /* Server not returning valid data */
    public static final int SYNC_STATUS_UNKNOWN = 4;        /* ... I just don't know */
    public static final int SYNC_STATUS_SYNCING = 5;        /* Currently syncing */

}
