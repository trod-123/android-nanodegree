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
package com.thirdarm.footballscores.provider.ateam;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code ateam} table.
 */
public class AteamCursor extends AbstractCursor implements AteamModel {
    public AteamCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(AteamColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The name of the team. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getName() {
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
    public String getShortname() {
        String res = getStringOrNull(AteamColumns.SHORTNAME);
        return res;
    }

    /**
     * The code name of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getCode() {
        String res = getStringOrNull(AteamColumns.CODE);
        return res;
    }

    /**
     * The squad market value of the team. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getValue() {
        String res = getStringOrNull(AteamColumns.VALUE);
        return res;
    }

    /**
     * The team's crest url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getCresturl() {
        String res = getStringOrNull(AteamColumns.CRESTURL);
        return res;
    }
}
