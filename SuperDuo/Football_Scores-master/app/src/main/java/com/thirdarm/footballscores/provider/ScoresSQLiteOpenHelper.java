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

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.thirdarm.footballscores.BuildConfig;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.player.PlayerColumns;

public class ScoresSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = ScoresSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "footballscores.db";
    private static final int DATABASE_VERSION = 1;
    private static ScoresSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final ScoresSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_ATEAM = "CREATE TABLE IF NOT EXISTS "
            + AteamColumns.TABLE_NAME + " ( "
            + AteamColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AteamColumns.NAME + " TEXT NOT NULL, "
            + AteamColumns.SHORTNAME + " TEXT, "
            + AteamColumns.VALUE + " TEXT, "
            + AteamColumns.CRESTURL + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (ateam__name) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_TABLE_BTEAM = "CREATE TABLE IF NOT EXISTS "
            + BteamColumns.TABLE_NAME + " ( "
            + BteamColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BteamColumns.NAME + " TEXT NOT NULL, "
            + BteamColumns.SHORTNAME + " TEXT, "
            + BteamColumns.VALUE + " TEXT, "
            + BteamColumns.CRESTURL + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (bteam__name) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_TABLE_FIXTURE = "CREATE TABLE IF NOT EXISTS "
            + FixtureColumns.TABLE_NAME + " ( "
            + FixtureColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FixtureColumns.DATE + " TEXT NOT NULL, "
            + FixtureColumns.TIME + " TEXT NOT NULL, "
            + FixtureColumns.STATUS + " INTEGER NOT NULL, "
            + FixtureColumns.TEAMA_ID + " INTEGER NOT NULL, "
            + FixtureColumns.TEAMB_ID + " INTEGER NOT NULL, "
            + FixtureColumns.LEAGUEID + " INTEGER NOT NULL, "
            + FixtureColumns.HOMEGOALS + " INTEGER, "
            + FixtureColumns.AWAYGOALS + " INTEGER, "
            + FixtureColumns.MATCHID + " INTEGER NOT NULL, "
            + FixtureColumns.MATCHDAY + " INTEGER NOT NULL "
            + ", CONSTRAINT fk_teama_id FOREIGN KEY (" + FixtureColumns.TEAMA_ID + ") REFERENCES ateam (_id) ON DELETE CASCADE"
            + ", CONSTRAINT fk_teamb_id FOREIGN KEY (" + FixtureColumns.TEAMB_ID + ") REFERENCES bteam (_id) ON DELETE CASCADE"
            + ", CONSTRAINT unique_id UNIQUE (matchId) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_TABLE_PLAYER = "CREATE TABLE IF NOT EXISTS "
            + PlayerColumns.TABLE_NAME + " ( "
            + PlayerColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PlayerColumns.FIRSTNAME + " TEXT NOT NULL, "
            + PlayerColumns.LASTNAME + " TEXT NOT NULL, "
            + PlayerColumns.PLAYERPOSITION + " TEXT, "
            + PlayerColumns.JERSEYNUMBER + " INTEGER, "
            + PlayerColumns.DATEOFBIRTH + " TEXT, "
            + PlayerColumns.NATIONALITY + " TEXT, "
            + PlayerColumns.CONTRACTUNTILDATE + " TEXT, "
            + PlayerColumns.MARKETVALUE + " REAL "
            + ", CONSTRAINT unique_name UNIQUE (firstName, lastName) ON CONFLICT REPLACE"
            + " );";

    // @formatter:on

    public static ScoresSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static ScoresSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static ScoresSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new ScoresSQLiteOpenHelper(context);
    }

    private ScoresSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new ScoresSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static ScoresSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new ScoresSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ScoresSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new ScoresSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_ATEAM);
        db.execSQL(SQL_CREATE_TABLE_BTEAM);
        db.execSQL(SQL_CREATE_TABLE_FIXTURE);
        db.execSQL(SQL_CREATE_TABLE_PLAYER);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
