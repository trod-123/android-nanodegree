package com.thirdarm.footballscores.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by yehya khaled on 2/25/2015.
 *
 * Updated by TROD on 20151216 to use Schematic implementation of Content Provider
 */
@ContentProvider(
        authority = ScoresProvider.AUTHORITY,
        database = ScoresDatabase.class,
        packageName = "com.thirdarm.footballscores"
)
public class ScoresProvider {

    public static final String AUTHORITY = "com.thirdarm.footballscores";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // For setting the URI paths
    interface Path {
        String SCORES = "scores"; // global referential
        String TEAMS = "teams"; // list of teams
        String FROM_TEAM = "fromTeam"; // for id'ing team
    }

    // For building URIs for each of the URI paths in the interface above
    //  (direct copy of original schematic implementation)
    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    // Table for storing teams information
    @TableEndpoint(table = ScoresDatabase.Tables.TEAMS) public static class Teams {

        @ContentUri(
                path = Path.TEAMS,
                type = "vnd.android.cursor.dir/teams",
                defaultSort = TeamsColumns.NAME + " ASC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.TEAMS);

        @InexactContentUri(
                path = Path.TEAMS + "/#",
                name = "TEAM_NAME",
                type = "vnd.android.cursor.item/teams",
                whereColumn = TeamsColumns.NAME,
                pathSegment = 1
        )
        public static Uri withName(String name) {
            return buildUri(Path.TEAMS, name);
        }
    }

    // Table for storing individual fixtures' scores information
    @TableEndpoint(table = ScoresDatabase.Tables.SCORES) public static class Scores {

        // The base URI for the entire directory of scores. This will be the URI with which you
        //  will refer to to grab the cursor
        // content://com.thirdarm.footballscores/scores [dir]
        @ContentUri(
                path = Path.SCORES,
                type = "vnd.android.cursor.dir/scores"
        )
        public static final Uri CONTENT_URI = buildUri(Path.SCORES);

        // The URI for a single score item
        // content://com.thirdarm.footballscores/scores/# [item]
        //
        // An InexactContentUri is used for all additional URIs built from the CONTENT_URI to
        //  access select data from the database
        @InexactContentUri(
                name = "MATCH_ID",
                path = Path.SCORES + "/#",
                type = "vnd.android.cursor.item/scores",
                whereColumn = ScoresColumns.MATCH_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.SCORES, String.valueOf(id));
        }

        // For the home team
        @InexactContentUri(
                name = "TEAM_FROM_SCORES",
                path = Path.SCORES + "/" + Path.FROM_TEAM + "/#",
                type = "vnd.android.cursor.dir/teams",
                whereColumn = {ScoresColumns.HOME_NAME, ScoresColumns.AWAY_NAME},
                pathSegment = {2, 3}
        )
        public static Uri fromTeam(String name) {
            return buildUri(Path.SCORES, Path.FROM_TEAM, name);
        }

        // For overriding the onInsert() method
        @NotifyInsert(paths = Path.SCORES)
        public static Uri[] onInsert(ContentValues cv) {
            final String teamName = cv.getAsString(TeamsColumns.NAME);
            return new Uri[] {
                    Teams.withName(teamName), fromTeam(teamName)
            };
        }

        // For overriding the onBulkInsert() method
        @NotifyBulkInsert(paths = Path.SCORES)
        public static Uri[] onBulkInsert(Context c, Uri uri, ContentValues[] cv, long[] ids) {
            return new Uri[] {
                    uri,
            };
        }

        // For overriding the onUpdate() method
        @NotifyUpdate(paths = Path.SCORES + "/#")
        public static Uri[] onUpdate(Context c, Uri uri, String where, String[] whereArgs) {
            final long matchId = Long.valueOf(uri.getPathSegments().get(1));
            Cursor cursor = c.getContentResolver().query(uri, new String[]{
                    ScoresColumns.MATCH_ID,}, null, null, null);
            cursor.moveToFirst();
            final String homeTeam = cursor.getString(Projections.SCORES.COL_HOME_NAME);
            final String awayTeam = cursor.getString(Projections.SCORES.COL_AWAY_NAME);
            cursor.close();

            return new Uri[] {
                    withId(matchId),
                    fromTeam(homeTeam), fromTeam(awayTeam),
                    Teams.withName(homeTeam), Teams.withName(awayTeam)
            };
        }

        // For overriding the onDelete() method
        @NotifyDelete(paths = Path.SCORES + "/#")
        public static Uri[] onDelete(Context c, Uri uri) {
            final long matchId = Long.valueOf(uri.getPathSegments().get(1));
            Cursor cursor = c.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            final String homeTeam = cursor.getString(Projections.SCORES.COL_HOME_NAME);
            final String awayTeam = cursor.getString(Projections.SCORES.COL_AWAY_NAME);
            cursor.close();

            return new Uri[] {
                    withId(matchId),
                    fromTeam(homeTeam), fromTeam(awayTeam),
                    Teams.withName(homeTeam), Teams.withName(awayTeam)
            };
        }
    }

//        public static Uri buildScoreWithLeague()
//        {
//            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
//        }
//        public static Uri buildScoreWithId()
//        {
//            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
//        }
//        public static Uri buildScoreWithDate()
//        {
//            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
//        }
//    }
//
//    private static final int MATCHES = 100;
//    private static final int MATCHES_WITH_LEAGUE = 101;
//    private static final int MATCHES_WITH_ID = 102;
//    private static final int MATCHES_WITH_DATE = 103;

//    private UriMatcher muriMatcher = buildUriMatcher();

//    private static final SQLiteQueryBuilder ScoreQuery =
//            new SQLiteQueryBuilder();

//    private static final String SCORES_BY_LEAGUE = DatabaseContract.scores_table.LEAGUE_COL + " = ?";
//    private static final String SCORES_BY_DATE =
//            DatabaseContract.scores_table.DATE_COL + " LIKE ?";
//    private static final String SCORES_BY_ID =
//            DatabaseContract.scores_table.MATCH_ID + " = ?";
//
//
//    static UriMatcher buildUriMatcher() {
//        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
//        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
//        matcher.addURI(authority, null , MATCHES);
//        matcher.addURI(authority, "league" , MATCHES_WITH_LEAGUE);
//        matcher.addURI(authority, "id" , MATCHES_WITH_ID);
//        matcher.addURI(authority, "date" , MATCHES_WITH_DATE);
//        return matcher;
//    }
//
//    private int match_uri(Uri uri)
//    {
//        String link = uri.toString();
//        {
//           if(link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString()))
//           {
//               return MATCHES;
//           }
//           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithDate().toString()))
//           {
//               return MATCHES_WITH_DATE;
//           }
//           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithId().toString()))
//           {
//               return MATCHES_WITH_ID;
//           }
//           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithLeague().toString()))
//           {
//               return MATCHES_WITH_LEAGUE;
//           }
//        }
//        return -1;
//    }
//    @Override
//    public boolean onCreate()
//    {
//        mOpenHelper = new ScoresDBHelper(getContext());
//        return false;
//    }
//
//    @Override
//    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
//    {
//        return 0;
//    }
//
//    @Override
//    public String getType(Uri uri)
//    {
//        final int match = muriMatcher.match(uri);
//        switch (match) {
//            case MATCHES:
//                return DatabaseContract.scores_table.CONTENT_TYPE;
//            case MATCHES_WITH_LEAGUE:
//                return DatabaseContract.scores_table.CONTENT_TYPE;
//            case MATCHES_WITH_ID:
//                return DatabaseContract.scores_table.CONTENT_ITEM_TYPE;
//            case MATCHES_WITH_DATE:
//                return DatabaseContract.scores_table.CONTENT_TYPE;
//            default:
//                throw new UnsupportedOperationException("Unknown uri :" + uri );
//        }
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
//    {
//        Cursor retCursor;
//        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
//        int match = match_uri(uri);
//        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
//        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
//        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));
//        switch (match)
//        {
//            case MATCHES: retCursor = mOpenHelper.getReadableDatabase().query(
//                    DatabaseContract.SCORES_TABLE,
//                    projection,null,null,null,null,sortOrder); break;
//            case MATCHES_WITH_DATE:
//                    retCursor = mOpenHelper.getReadableDatabase().query(
//                    DatabaseContract.SCORES_TABLE,
//                    projection,SCORES_BY_DATE,selectionArgs,null,null,sortOrder); break;
//            case MATCHES_WITH_ID: retCursor = mOpenHelper.getReadableDatabase().query(
//                    DatabaseContract.SCORES_TABLE,
//                    projection,SCORES_BY_ID,selectionArgs,null,null,sortOrder); break;
//            case MATCHES_WITH_LEAGUE: retCursor = mOpenHelper.getReadableDatabase().query(
//                    DatabaseContract.SCORES_TABLE,
//                    projection,SCORES_BY_LEAGUE,selectionArgs,null,null,sortOrder); break;
//            default: throw new UnsupportedOperationException("Unknown Uri" + uri);
//        }
//        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
//        return retCursor;
//    }
//
//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//
//        return null;
//    }
//
//    @Override
//    public int bulkInsert(Uri uri, ContentValues[] values)
//    {
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        //db.delete(DatabaseContract.SCORES_TABLE,null,null);
//        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(muriMatcher.match(uri)));
//        switch (match_uri(uri))
//        {
//            case MATCHES:
//                db.beginTransaction();
//                int returncount = 0;
//                try
//                {
//                    for(ContentValues value : values)
//                    {
//                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
//                                SQLiteDatabase.CONFLICT_REPLACE);
//                        if (_id != -1)
//                        {
//                            returncount++;
//                        }
//                    }
//                    db.setTransactionSuccessful();
//                } finally {
//                    db.endTransaction();
//                }
//                getContext().getContentResolver().notifyChange(uri,null);
//                return returncount;
//            default:
//                return super.bulkInsert(uri,values);
//        }
//    }
//
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        return 0;
//    }
}
