/*
 *  Copyright (C) 2016 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P3: SuperDuo
 *
 *  Currently for educational purposes only.
 *
 *  Content provider files generated using Benoit Lubek's (BoD)
 *    Android ContentProvider Generator.
 *    (url: https://github.com/BoD/android-contentprovider-generator)
 */
package com.thirdarm.footballscores.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thirdarm.footballscores.BuildConfig;
import com.thirdarm.footballscores.provider.base.BaseContentProvider;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.player.PlayerColumns;

public class ScoresProvider extends BaseContentProvider {
    private static final String TAG = ScoresProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "com.thirdarm.footballscores.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_ATEAM = 0;
    private static final int URI_TYPE_ATEAM_ID = 1;

    private static final int URI_TYPE_BTEAM = 2;
    private static final int URI_TYPE_BTEAM_ID = 3;

    private static final int URI_TYPE_FIXTURE = 4;
    private static final int URI_TYPE_FIXTURE_ID = 5;

    private static final int URI_TYPE_PLAYER = 6;
    private static final int URI_TYPE_PLAYER_ID = 7;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AteamColumns.TABLE_NAME, URI_TYPE_ATEAM);
        URI_MATCHER.addURI(AUTHORITY, AteamColumns.TABLE_NAME + "/#", URI_TYPE_ATEAM_ID);
        URI_MATCHER.addURI(AUTHORITY, BteamColumns.TABLE_NAME, URI_TYPE_BTEAM);
        URI_MATCHER.addURI(AUTHORITY, BteamColumns.TABLE_NAME + "/#", URI_TYPE_BTEAM_ID);
        URI_MATCHER.addURI(AUTHORITY, FixtureColumns.TABLE_NAME, URI_TYPE_FIXTURE);
        URI_MATCHER.addURI(AUTHORITY, FixtureColumns.TABLE_NAME + "/#", URI_TYPE_FIXTURE_ID);
        URI_MATCHER.addURI(AUTHORITY, PlayerColumns.TABLE_NAME, URI_TYPE_PLAYER);
        URI_MATCHER.addURI(AUTHORITY, PlayerColumns.TABLE_NAME + "/#", URI_TYPE_PLAYER_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return ScoresSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_ATEAM:
                return TYPE_CURSOR_DIR + AteamColumns.TABLE_NAME;
            case URI_TYPE_ATEAM_ID:
                return TYPE_CURSOR_ITEM + AteamColumns.TABLE_NAME;

            case URI_TYPE_BTEAM:
                return TYPE_CURSOR_DIR + BteamColumns.TABLE_NAME;
            case URI_TYPE_BTEAM_ID:
                return TYPE_CURSOR_ITEM + BteamColumns.TABLE_NAME;

            case URI_TYPE_FIXTURE:
                return TYPE_CURSOR_DIR + FixtureColumns.TABLE_NAME;
            case URI_TYPE_FIXTURE_ID:
                return TYPE_CURSOR_ITEM + FixtureColumns.TABLE_NAME;

            case URI_TYPE_PLAYER:
                return TYPE_CURSOR_DIR + PlayerColumns.TABLE_NAME;
            case URI_TYPE_PLAYER_ID:
                return TYPE_CURSOR_ITEM + PlayerColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_ATEAM:
            case URI_TYPE_ATEAM_ID:
                res.table = AteamColumns.TABLE_NAME;
                res.idColumn = AteamColumns._ID;
                res.tablesWithJoins = AteamColumns.TABLE_NAME;
                res.orderBy = AteamColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_BTEAM:
            case URI_TYPE_BTEAM_ID:
                res.table = BteamColumns.TABLE_NAME;
                res.idColumn = BteamColumns._ID;
                res.tablesWithJoins = BteamColumns.TABLE_NAME;
                res.orderBy = BteamColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_FIXTURE:
            case URI_TYPE_FIXTURE_ID:
                res.table = FixtureColumns.TABLE_NAME;
                res.idColumn = FixtureColumns._ID;
                res.tablesWithJoins = FixtureColumns.TABLE_NAME;
                if (AteamColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + AteamColumns.TABLE_NAME + " AS " + FixtureColumns.PREFIX_ATEAM + " ON " + FixtureColumns.TABLE_NAME + "." + FixtureColumns.TEAMA_ID + "=" + FixtureColumns.PREFIX_ATEAM + "." + AteamColumns._ID;
                }
                if (BteamColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + BteamColumns.TABLE_NAME + " AS " + FixtureColumns.PREFIX_BTEAM + " ON " + FixtureColumns.TABLE_NAME + "." + FixtureColumns.TEAMB_ID + "=" + FixtureColumns.PREFIX_BTEAM + "." + BteamColumns._ID;
                }
                res.orderBy = FixtureColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_PLAYER:
            case URI_TYPE_PLAYER_ID:
                res.table = PlayerColumns.TABLE_NAME;
                res.idColumn = PlayerColumns._ID;
                res.tablesWithJoins = PlayerColumns.TABLE_NAME;
                res.orderBy = PlayerColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_ATEAM_ID:
            case URI_TYPE_BTEAM_ID:
            case URI_TYPE_FIXTURE_ID:
            case URI_TYPE_PLAYER_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
