package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import barqsoft.footballscores.API.APIHelper;
import barqsoft.footballscores.R;
import barqsoft.footballscores.utilities.Utilities;
import barqsoft.footballscores.data.ScoresColumns;
import barqsoft.footballscores.data.ScoresProvider;
import barqsoft.footballscores.model.Fixture;
import barqsoft.footballscores.model.Team;

/**
 * Created by TROD on 20151220.
 *
 * The Scores SyncAdapter
 */
public class ScoresSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = ScoresSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in form <# s/min> * <# min> (this is done in
    //  seconds, not milliseconds)
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private String mSyncCategory;
    private static boolean sInitialize = false;
    private String[] mTimeFrames = new String[] {"n2", "p2"};

    public ScoresSyncAdapter(Context c, boolean autoInitialize) {
        super(c, autoInitialize);
    }

    // AsyncTask or IntentService content goes here
    @Override public void onPerformSync(Account account, Bundle extras, String authority,
                                        ContentProviderClient provider, SyncResult syncResult) {


            Log.d(LOG_TAG, "In onPerformSync");

            // Generate APIHelper
            APIHelper apiHelper = new APIHelper(getContext());

            for (String timeFrame : mTimeFrames) {
                Log.d(LOG_TAG, "Timeframe: " + timeFrame);
                List<Fixture> fixtures = apiHelper.getFixtures(null, timeFrame);
                Log.d(LOG_TAG, "Fixture count: " + fixtures.size());

                //ContentValues to be inserted
                Vector<ContentValues> values = new Vector<>(fixtures.size());

                for (Fixture fixture : fixtures) {

                    Log.d(LOG_TAG, "Individual fixture in timeframe: " + timeFrame);

                    // Use utilities to convert raw fixture data into usable data
                    String matchId = "" + Utilities
                            .extractId(fixture.getFixturesLinks().getLinksSelf().getHref(),
                                    Utilities.FIXTURE_LINK);
                    String leagueId = "" + Utilities
                            .extractId(fixture.getFixturesLinks().getLinksSoccerseason().getHref(),
                                    Utilities.SEASON_LINK);
                    String[] dateTime = Utilities.getUserDateTime(fixture.getDate());

                    // Get teams information
//                    List<Team> teams = apiHelper.getHomeAwayTeamsFromSingleFixture(fixture);


                    /*
                        Insert into db
                     */

                    ContentResolver cr = getContext().getContentResolver();
                    Cursor cursor = cr.query(
                            ScoresProvider.Scores.CONTENT_URI,
                            new String[]{ScoresColumns.MATCH_ID},
                            ScoresColumns.MATCH_ID + " == ? ",
                            new String[]{matchId},
                            null
                    );

                    ContentValues fixtureCv = new ContentValues();
                    fixtureCv.put(ScoresColumns.DATE, dateTime[0]);
                    fixtureCv.put(ScoresColumns.TIME, dateTime[1]);
                    fixtureCv.put(ScoresColumns.HOME_NAME, fixture.getHomeTeamName());
                    fixtureCv.put(ScoresColumns.AWAY_NAME, fixture.getAwayTeamName());
//                    fixtureCv.put(ScoresColumns.HOME_NAME, teams.get(0).getShortName());
//                    fixtureCv.put(ScoresColumns.AWAY_NAME, teams.get(1).getShortName());
                    fixtureCv.put(ScoresColumns.LEAGUE_NAME, leagueId);
                    fixtureCv.put(ScoresColumns.HOME_GOALS, fixture.getResult().getGoalsHomeTeam());
                    fixtureCv.put(ScoresColumns.AWAY_GOALS, fixture.getResult().getGoalsAwayTeam());
                    fixtureCv.put(ScoresColumns.MATCH_ID, matchId);
                    fixtureCv.put(ScoresColumns.MATCH_DAY, fixture.getMatchday());
//                    fixtureCv.put(ScoresColumns.HOME_CREST_URL, teams.get(0).getCrestUrl());
//                    fixtureCv.put(ScoresColumns.AWAY_CREST_URL, teams.get(1).getCrestUrl());

                    if (cursor.moveToFirst()) {
                        cr.update(ScoresProvider.Scores.CONTENT_URI,
                                fixtureCv,
                                ScoresColumns.MATCH_ID + " == ? ",
                                new String[]{matchId}
                        );
                    } else {
                        values.add(fixtureCv);
                    }
                    cursor.close();
                }

                // Bulk insert the scores data
                ContentValues[] cv = new ContentValues[values.size()];
                values.toArray(cv);
                getContext().getContentResolver().bulkInsert(ScoresProvider.Scores.CONTENT_URI, cv);

            }
//        } else {
//            Log.d(LOG_TAG, "Did not go through sync.");
//        }
    }

    /**
     * Helper method to have the sync adapter sync immediately. The kind of sync will depend on the
     *   parameters provided during the call
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }



    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things. If this were called from
     * initializeSyncAdapter(), return the account and do nothing else. If this were called from
     * syncImmediately(), then return the account for the requestSync() method and proceed with
     * the initialization.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            // It is here through which the sync happens
            sInitialize = true;
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Configure sync settings here and start the sync
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        ScoresSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started. Here, not needed to define category since
         *  upon initialization, movies of all categories will be loaded. During initialization,
         *  a fixed number of pages of results will be queried, and specific movie details will not
         *  be fetched, so their values do not really matter.
         */
        syncImmediately(context);
    }

    /**
     * Initialize by getting the account, and then if it exists, start sync. This method is called
     *  by the MainActivity, and everything happens here (calls chain of helper methods ultimately
     *  leading to syncImmediately())
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
