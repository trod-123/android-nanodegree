package com.thirdarm.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import com.thirdarm.footballscores.API.APIHelper;
import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.model.Team;
import com.thirdarm.footballscores.provider.ateam.AteamContentValues;
import com.thirdarm.footballscores.provider.ateam.AteamCursor;
import com.thirdarm.footballscores.provider.ateam.AteamSelection;
import com.thirdarm.footballscores.provider.bteam.BteamContentValues;
import com.thirdarm.footballscores.provider.bteam.BteamCursor;
import com.thirdarm.footballscores.provider.bteam.BteamSelection;
import com.thirdarm.footballscores.provider.fixture.FixtureContentValues;
import com.thirdarm.footballscores.provider.fixture.Status;
import com.thirdarm.footballscores.utilities.Network;
import com.thirdarm.footballscores.utilities.Utilities;
import com.thirdarm.footballscores.model.Fixture;

/**
 * Created by TROD on 20151220.
 * <p/>
 * The Scores SyncAdapter
 */
public class ScoresSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = ScoresSyncAdapter.class.getSimpleName();

    // For notifying the widget provider the sync is complete and data has changed
    public static final String ACTION_DATA_UPDATED =
            "com.thirdarm.footballscores.app.ACTION_DATA_UPDATED";

    // Interval at which to sync with the weather, in form <# s/min> * <# min> (this is done in
    //  seconds, not milliseconds)
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    // The range of season ids (inclusive)
    private static final int SEASON_MIN = 394;
    private static final int SEASON_MAX = 405;

    // Time frames for fixtures to be fetched (see API for more info)
    private static final String RANGE_UPCOMING = "n2";
    private static final String RANGE_FINISHED = "p2";
    private String[] mTimeFrames = new String[]{RANGE_UPCOMING, RANGE_FINISHED};

    public ScoresSyncAdapter(Context c, boolean autoInitialize) {
        super(c, autoInitialize);
    }

    // AsyncTask or IntentService content goes here
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Network.setSyncStatus(getContext(), Network.SYNC_STATUS_SYNCING);

        // Generate APIHelper
        APIHelper apiHelper = new APIHelper(getContext());

        // Check to see first if teams database exists because next procedure depends on it
        ContentResolver cr = getContext().getContentResolver();
        Cursor c = cr.query((new AteamSelection()).uri(), null, null, null, null);
        if (c != null) {
            if (!c.moveToFirst()) {
                setTeamsCollectedState(getContext(), false);
            }
            c.close();
        }
        if (!getTeamsCollectedState(getContext())) {
            // If not, initialize teams database
            getAllSeasonsTeams(apiHelper);
        }
        // If teams sync still failed, terminate sync and set sync status
        if (!getTeamsCollectedState(getContext())) {
            Log.e(LOG_TAG, "Teams failed to fetch. Terminating sync.");
            Network.setSyncStatus(getContext(), Network.SYNC_STATUS_SERVER_DOWN);
            return;
        }

        // Proceed in fetching fixtures
        for (String timeFrame : mTimeFrames) {
            List<Fixture> fixtures = apiHelper.getFixtures(null, timeFrame);
            // Make sure fixtures isn't null first. Terminate sync and set sync status if null.
            if (fixtures == null) {
                Log.e(LOG_TAG, "Fixtures failed to fetch. Terminating sync.");
                Network.setSyncStatus(getContext(), Network.SYNC_STATUS_SERVER_DOWN);
            }
            for (Fixture fixture : fixtures) {

                // Get the status
                Status status;
                switch (fixture.getStatus()) {
                    case "FINISHED":
                        status = Status.FINISHED;
                        break;
                    case "TIMED":
                        status = Status.TIMED;
                        break;
                    default:
                        status = Status.OTHER;
                        break;
                }

                // Use utilities to convert raw fixture data into usable data
                int matchId = Utilities
                        .extractId(fixture.getFixturesLinks().getLinksSelf().getHref(),
                                Utilities.FIXTURE_LINK);
                int leagueId = Utilities
                        .extractId(fixture.getFixturesLinks().getLinksSoccerseason().getHref(),
                                Utilities.SEASON_LINK);
                String[] dateTime = Utilities.convertUserDateTime(fixture.getDate());

                int homeGoals, awayGoals;
                if (fixture.getResult().getGoalsHomeTeam() != null) {
                    homeGoals = fixture.getResult().getGoalsHomeTeam();
                } else {
                    homeGoals = -1;
                }
                if (fixture.getResult().getGoalsAwayTeam() != null) {
                    awayGoals = fixture.getResult().getGoalsAwayTeam();
                } else {
                    awayGoals = -1;
                }

                /*
                    Insert into db
                 */

                insertFixture(dateTime[0], dateTime[1], status,
                        fixture.getHomeTeamName(), fixture.getAwayTeamName(), leagueId,
                        homeGoals, awayGoals, matchId, fixture.getMatchday()
                );
            }
        }
        Network.setSyncStatus(getContext(), Network.SYNC_STATUS_OK);
    }

    public void getAllSeasonsTeams(APIHelper apiHelper) {
        for (int i = SEASON_MIN; i <= SEASON_MAX; i++) {
            List<Team> seasonTeams = apiHelper.getSingleSoccerseasonTeams(i);
            // make sure that the teams list is not null due to server timeouts.
            if (seasonTeams == null) {
                Log.e(LOG_TAG, "The Teams list was null due to timeout. Terminating...");
                return;
            }
            for (Team team : seasonTeams) {
                String name = team.getName();
                String shortName = team.getShortName();
                String code = team.getCode();
                String marketValue = team.getSquadMarketValue();
                String crestUrl = team.getCrestUrl();

                insertTeamA(name, shortName, code, marketValue, crestUrl);
                insertTeamB(name, shortName, code, marketValue, crestUrl);
            }
        }
        setTeamsCollectedState(getContext(), true);
    }

    /**
     * Insert a fixture (match).
     *
     * @return The id of the inserted fixture.
     */
    public long insertFixture(@NonNull String date, @NonNull String time, @NonNull Status status,
                              @NonNull String homeName, @NonNull String awayName, @NonNull int leagueId,
                              int homeGoals, int awayGoals, @NonNull int matchId,
                              @NonNull int matchDay) {
        // Get the location of the teams in the content provider to which the fixtures point
        AteamCursor ac = (new AteamSelection()).name(homeName).query(getContext().getContentResolver());
        BteamCursor bc = (new BteamSelection()).name(awayName).query(getContext().getContentResolver());
        long teamA_id = 0;
        long teamB_id = 0;
        if (ac.moveToFirst() && bc.moveToFirst()) {
            teamA_id = ac.getId();
            teamB_id = bc.getId();
        }
        ac.close();
        bc.close();

        // Store the content values and insert it in provider
        FixtureContentValues cv = new FixtureContentValues();
        cv.putDate(date);
        cv.putTime(time);
        cv.putStatus(status);
        cv.putTeamaId(teamA_id);
        cv.putTeambId(teamB_id);
        cv.putLeagueid(leagueId);
        cv.putHomegoals(homeGoals);
        cv.putAwaygoals(awayGoals);
        cv.putMatchid(matchId);
        cv.putMatchday(matchDay);

        Uri uri = cv.insert(getContext().getContentResolver());
        return ContentUris.parseId(uri);
    }

    /**
     * Insert a team. This goes hand-in-hand with insertTeamB(). Another team table is needed
     * so that a fixture can point to both home and away teams. This table will essentially
     * contain exactly the same elements as teamB.
     *
     * @return The id of the inserted team.
     */
    public long insertTeamA(@NonNull String name, String shortName, String code, String value, String crestUrl) {
        AteamContentValues aCv = new AteamContentValues();
        aCv.putName(name);
        aCv.putShortname(shortName);
        aCv.putCode(code);
        aCv.putValue(value);
        aCv.putCresturl(crestUrl);

        Uri uri = aCv.insert(getContext().getContentResolver());
        return ContentUris.parseId(uri);
    }

    /**
     * Insert a team. This goes hand-in-hand with insertTeamA(). Another team table is needed
     * so that a fixture can point to both home and away teams. This table will essentially
     * contain exactly the same elements as teamA.
     *
     * @return The id of the inserted team.
     */
    public long insertTeamB(@NonNull String name, String shortName, String code, String value, String crestUrl) {
        BteamContentValues bCv = new BteamContentValues();
        bCv.putName(name);
        bCv.putShortname(shortName);
        bCv.putCode(code);
        bCv.putValue(value);
        bCv.putCresturl(crestUrl);

        Uri uri = bCv.insert(getContext().getContentResolver());
        return ContentUris.parseId(uri);
    }

    /**
     * Helper method to have the sync adapter sync immediately. The kind of sync will depend on the
     * parameters provided during the call
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
        if (null == accountManager.getPassword(newAccount)) {

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
            setTeamsCollectedState(context, false);
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
     * by the MainActivity, and everything happens here (calls chain of helper methods ultimately
     * leading to syncImmediately())
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Sets the state of the teams content provider. This is so that the state can be preserved
     * even when app restarts.
     *
     * @param c      Context from which to get the PreferenceManager
     * @param status True if teams has been successfully filled. False if teams fetch failed.
     */
    private static void setTeamsCollectedState(Context c, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(c.getString(R.string.sp_teams_collected_key), status);
        spe.commit();
    }

    /**
     * Gets the state of the teams content provider.
     *
     * @param c Context from which to get the PreferenceManager
     * @return True if teams has been successfully filled. False if teams fetch failed.
     */
    private static boolean getTeamsCollectedState(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getBoolean(c.getString(R.string.sp_teams_collected_key), false);
    }

}
