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

import com.thirdarm.footballscores.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Information about a single football game.
 */
public interface FixtureModel extends BaseModel {

    /**
     * The date of the match. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getDate();

    /**
     * The time of the match. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getTime();

    /**
     * The status of the match. (Enumeration: Status ["FINISHED", "TIMED"], Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    Status getStatus();

    /**
     * The name of the home team. Makes a REFERENCE to the home team table (aTeam).
     */
    long getTeamaId();

    /**
     * The name of the away team. Makes a REFERENCE to the away team table (bTeam).
     */
    long getTeambId();

    /**
     * The id of the soccerseason/league. (Integer, Not nullable)
     */
    int getLeagueid();

    /**
     * Number of goals made by the home team. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Integer getHomegoals();

    /**
     * Number of goals made by the away team. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Integer getAwaygoals();

    /**
     * The id of the match. (Integer, Not nullable)
     */
    int getMatchid();

    /**
     * The number of the match. (Integer, Not nullable)
     */
    int getMatchday();
}
