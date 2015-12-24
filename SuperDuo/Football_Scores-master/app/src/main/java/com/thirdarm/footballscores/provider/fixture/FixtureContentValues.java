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
package com.thirdarm.footballscores.provider.fixture;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code fixture} table.
 */
public class FixtureContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FixtureColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FixtureSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable FixtureSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The date of the match. (String, Not nullable)
     */
    public FixtureContentValues putDate(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("date must not be null");
        mContentValues.put(FixtureColumns.DATE, value);
        return this;
    }


    /**
     * The time of the match. (String, Not nullable)
     */
    public FixtureContentValues putTime(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("time must not be null");
        mContentValues.put(FixtureColumns.TIME, value);
        return this;
    }


    /**
     * The status of the match. (Enumeration: Status ["FINISHED", "TIMED"], Not nullable)
     */
    public FixtureContentValues putStatus(@NonNull Status value) {
        if (value == null) throw new IllegalArgumentException("status must not be null");
        mContentValues.put(FixtureColumns.STATUS, value.ordinal());
        return this;
    }


    /**
     * The name of the home team. Makes a REFERENCE to the home team table (aTeam).
     */
    public FixtureContentValues putTeamaId(long value) {
        mContentValues.put(FixtureColumns.TEAMA_ID, value);
        return this;
    }


    /**
     * The name of the away team. Makes a REFERENCE to the away team table (bTeam).
     */
    public FixtureContentValues putTeambId(long value) {
        mContentValues.put(FixtureColumns.TEAMB_ID, value);
        return this;
    }


    /**
     * The id of the soccerseason/league. (Integer, Not nullable)
     */
    public FixtureContentValues putLeagueid(int value) {
        mContentValues.put(FixtureColumns.LEAGUEID, value);
        return this;
    }


    /**
     * Number of goals made by the home team. (Integer, Nullable)
     */
    public FixtureContentValues putHomegoals(@Nullable Integer value) {
        mContentValues.put(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureContentValues putHomegoalsNull() {
        mContentValues.putNull(FixtureColumns.HOMEGOALS);
        return this;
    }

    /**
     * Number of goals made by the away team. (Integer, Nullable)
     */
    public FixtureContentValues putAwaygoals(@Nullable Integer value) {
        mContentValues.put(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureContentValues putAwaygoalsNull() {
        mContentValues.putNull(FixtureColumns.AWAYGOALS);
        return this;
    }

    /**
     * The id of the match. (Integer, Not nullable)
     */
    public FixtureContentValues putMatchid(int value) {
        mContentValues.put(FixtureColumns.MATCHID, value);
        return this;
    }


    /**
     * The number of the match. (Integer, Not nullable)
     */
    public FixtureContentValues putMatchday(int value) {
        mContentValues.put(FixtureColumns.MATCHDAY, value);
        return this;
    }

}
