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
package com.thirdarm.footballscores.provider.player;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thirdarm.footballscores.provider.ScoresProvider;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.player.PlayerColumns;

/**
 * A player who is part of a team
 */
public class PlayerColumns implements BaseColumns {
    public static final String TABLE_NAME = "player";
    public static final Uri CONTENT_URI = Uri.parse(ScoresProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The player's first name. (String, Not nullable)
     */
    public static final String FIRSTNAME = "firstName";

    /**
     * The player's last name. (String, Not nullable)
     */
    public static final String LASTNAME = "lastName";

    /**
     * The player's position. (String, Not nullable)
     */
    public static final String PLAYERPOSITION = "playerPosition";

    /**
     * The player's jersey number. (Integer, Nullable)
     */
    public static final String JERSEYNUMBER = "jerseyNumber";

    /**
     * The player's birth date. (String, Nullable)
     */
    public static final String DATEOFBIRTH = "dateOfBirth";

    /**
     * The player's nationality. (String, Nullable)
     */
    public static final String NATIONALITY = "nationality";

    /**
     * The player's contract expiry date. (String, Nullable)
     */
    public static final String CONTRACTUNTILDATE = "contractUntilDate";

    /**
     * The player's market value in the user's locale currency. (Double, Nullable)
     */
    public static final String MARKETVALUE = "marketValue";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            FIRSTNAME,
            LASTNAME,
            PLAYERPOSITION,
            JERSEYNUMBER,
            DATEOFBIRTH,
            NATIONALITY,
            CONTRACTUNTILDATE,
            MARKETVALUE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(FIRSTNAME) || c.contains("." + FIRSTNAME)) return true;
            if (c.equals(LASTNAME) || c.contains("." + LASTNAME)) return true;
            if (c.equals(PLAYERPOSITION) || c.contains("." + PLAYERPOSITION)) return true;
            if (c.equals(JERSEYNUMBER) || c.contains("." + JERSEYNUMBER)) return true;
            if (c.equals(DATEOFBIRTH) || c.contains("." + DATEOFBIRTH)) return true;
            if (c.equals(NATIONALITY) || c.contains("." + NATIONALITY)) return true;
            if (c.equals(CONTRACTUNTILDATE) || c.contains("." + CONTRACTUNTILDATE)) return true;
            if (c.equals(MARKETVALUE) || c.contains("." + MARKETVALUE)) return true;
        }
        return false;
    }

}
