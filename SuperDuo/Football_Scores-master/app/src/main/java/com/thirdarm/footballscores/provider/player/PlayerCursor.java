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

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code player} table.
 */
public class PlayerCursor extends AbstractCursor implements PlayerModel {
    public PlayerCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(PlayerColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The player's first name. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getFirstname() {
        String res = getStringOrNull(PlayerColumns.FIRSTNAME);
        if (res == null)
            throw new NullPointerException("The value of 'firstname' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The player's last name. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLastname() {
        String res = getStringOrNull(PlayerColumns.LASTNAME);
        if (res == null)
            throw new NullPointerException("The value of 'lastname' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The player's position. (String, Not nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getPlayerposition() {
        String res = getStringOrNull(PlayerColumns.PLAYERPOSITION);
        return res;
    }

    /**
     * The player's jersey number. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Integer getJerseynumber() {
        Integer res = getIntegerOrNull(PlayerColumns.JERSEYNUMBER);
        return res;
    }

    /**
     * The player's birth date. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getDateofbirth() {
        String res = getStringOrNull(PlayerColumns.DATEOFBIRTH);
        return res;
    }

    /**
     * The player's nationality. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getNationality() {
        String res = getStringOrNull(PlayerColumns.NATIONALITY);
        return res;
    }

    /**
     * The player's contract expiry date. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getContractuntildate() {
        String res = getStringOrNull(PlayerColumns.CONTRACTUNTILDATE);
        return res;
    }

    /**
     * The player's market value in the user's locale currency. (Double, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Double getMarketvalue() {
        Double res = getDoubleOrNull(PlayerColumns.MARKETVALUE);
        return res;
    }
}
