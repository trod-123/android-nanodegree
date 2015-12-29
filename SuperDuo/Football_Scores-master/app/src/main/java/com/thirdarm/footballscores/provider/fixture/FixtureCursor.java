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

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractCursor;
import com.thirdarm.footballscores.provider.ateam.*;
import com.thirdarm.footballscores.provider.bteam.*;

/**
 * Cursor wrapper for the {@code fixture} table.
 */
public class FixtureCursor extends AbstractCursor implements FixtureModel {
    public FixtureCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(FixtureColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The date of the match. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getDate() {
        String res = getStringOrNull(FixtureColumns.DATE);
        if (res == null)
            throw new NullPointerException("The value of 'date' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The time of the match. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTime() {
        String res = getStringOrNull(FixtureColumns.TIME);
        if (res == null)
            throw new NullPointerException("The value of 'time' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The status of the match. (Enumeration: Status ["FINISHED", "TIMED"], Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public Status getStatus() {
        Integer intValue = getIntegerOrNull(FixtureColumns.STATUS);
        if (intValue == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return Status.values()[intValue];
    }

    /**
     * The name of the home team. Makes a REFERENCE to the home team table (aTeam).
     */
    public long getTeamaId() {
        Long res = getLongOrNull(FixtureColumns.TEAMA_ID);
        if (res == null)
            throw new NullPointerException("The value of 'teama_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The name of the team. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getAteamName() {
        String res = getStringOrNull(AteamColumns.NAME);
        if (res == null)
            throw new NullPointerException("The value of 'name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The short name of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getAteamShortname() {
        String res = getStringOrNull(AteamColumns.SHORTNAME);
        return res;
    }

    /**
     * The code name of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getAteamCode() {
        String res = getStringOrNull(AteamColumns.CODE);
        return res;
    }

    /**
     * The squad market value of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getAteamValue() {
        String res = getStringOrNull(AteamColumns.VALUE);
        return res;
    }

    /**
     * The team's crest url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getAteamCresturl() {
        String res = getStringOrNull(AteamColumns.CRESTURL);
        return res;
    }

    /**
     * The name of the away team. Makes a REFERENCE to the away team table (bTeam).
     */
    public long getTeambId() {
        Long res = getLongOrNull(FixtureColumns.TEAMB_ID);
        if (res == null)
            throw new NullPointerException("The value of 'teamb_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The name of the team. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getBteamName() {
        String res = getStringOrNull(BteamColumns.NAME);
        if (res == null)
            throw new NullPointerException("The value of 'name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The short name of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getBteamShortname() {
        String res = getStringOrNull(BteamColumns.SHORTNAME);
        return res;
    }

    /**
     * The code name of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getBteamCode() {
        String res = getStringOrNull(BteamColumns.CODE);
        return res;
    }

    /**
     * The squad market value of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getBteamValue() {
        String res = getStringOrNull(BteamColumns.VALUE);
        return res;
    }

    /**
     * The team's crest url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getBteamCresturl() {
        String res = getStringOrNull(BteamColumns.CRESTURL);
        return res;
    }

    /**
     * The id of the soccerseason/league. (Integer, Not nullable)
     */
    public int getLeagueid() {
        Integer res = getIntegerOrNull(FixtureColumns.LEAGUEID);
        if (res == null)
            throw new NullPointerException("The value of 'leagueid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Number of goals made by the home team. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Integer getHomegoals() {
        Integer res = getIntegerOrNull(FixtureColumns.HOMEGOALS);
        return res;
    }

    /**
     * Number of goals made by the away team. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Integer getAwaygoals() {
        Integer res = getIntegerOrNull(FixtureColumns.AWAYGOALS);
        return res;
    }

    /**
     * The id of the match. (Integer, Not nullable)
     */
    public int getMatchid() {
        Integer res = getIntegerOrNull(FixtureColumns.MATCHID);
        if (res == null)
            throw new NullPointerException("The value of 'matchid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The number of the match. (Integer, Not nullable)
     */
    public int getMatchday() {
        Integer res = getIntegerOrNull(FixtureColumns.MATCHDAY);
        if (res == null)
            throw new NullPointerException("The value of 'matchday' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
