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

import android.net.Uri;
import android.provider.BaseColumns;

import com.thirdarm.footballscores.provider.ScoresProvider;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.player.PlayerColumns;

/**
 * Information about a single football game.
 */
public class FixtureColumns implements BaseColumns {
    public static final String TABLE_NAME = "fixture";
    public static final Uri CONTENT_URI = Uri.parse(ScoresProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The date of the match. (String, Not nullable)
     */
    public static final String DATE = "date";

    /**
     * The time of the match. (String, Not nullable)
     */
    public static final String TIME = "time";

    /**
     * The status of the match. (Enumeration: Status ["FINISHED", "TIMED"], Not nullable)
     */
    public static final String STATUS = "status";

    /**
     * The name of the home team. Makes a REFERENCE to the home team table (aTeam).
     */
    public static final String TEAMA_ID = "teamA_id";

    /**
     * The name of the away team. Makes a REFERENCE to the away team table (bTeam).
     */
    public static final String TEAMB_ID = "teamB_id";

    /**
     * The id of the soccerseason/league. (Integer, Not nullable)
     */
    public static final String LEAGUEID = "leagueId";

    /**
     * Number of goals made by the home team. (Integer, Nullable)
     */
    public static final String HOMEGOALS = "homeGoals";

    /**
     * Number of goals made by the away team. (Integer, Nullable)
     */
    public static final String AWAYGOALS = "awayGoals";

    /**
     * The id of the match. (Integer, Not nullable)
     */
    public static final String MATCHID = "matchId";

    /**
     * The number of the match. (Integer, Not nullable)
     */
    public static final String MATCHDAY = "matchDay";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            DATE,
            TIME,
            STATUS,
            TEAMA_ID,
            TEAMB_ID,
            LEAGUEID,
            HOMEGOALS,
            AWAYGOALS,
            MATCHID,
            MATCHDAY
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(DATE) || c.contains("." + DATE)) return true;
            if (c.equals(TIME) || c.contains("." + TIME)) return true;
            if (c.equals(STATUS) || c.contains("." + STATUS)) return true;
            if (c.equals(TEAMA_ID) || c.contains("." + TEAMA_ID)) return true;
            if (c.equals(TEAMB_ID) || c.contains("." + TEAMB_ID)) return true;
            if (c.equals(LEAGUEID) || c.contains("." + LEAGUEID)) return true;
            if (c.equals(HOMEGOALS) || c.contains("." + HOMEGOALS)) return true;
            if (c.equals(AWAYGOALS) || c.contains("." + AWAYGOALS)) return true;
            if (c.equals(MATCHID) || c.contains("." + MATCHID)) return true;
            if (c.equals(MATCHDAY) || c.contains("." + MATCHDAY)) return true;
        }
        return false;
    }

    public static final String PREFIX_ATEAM = TABLE_NAME + "__" + AteamColumns.TABLE_NAME;
    public static final String PREFIX_BTEAM = TABLE_NAME + "__" + BteamColumns.TABLE_NAME;
}
