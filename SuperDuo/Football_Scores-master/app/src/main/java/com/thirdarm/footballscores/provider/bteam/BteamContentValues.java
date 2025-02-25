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
package com.thirdarm.footballscores.provider.bteam;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code bteam} table.
 */
public class BteamContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return BteamColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable BteamSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable BteamSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The name of the team. (String, Not nullable)
     */
    public BteamContentValues putName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("name must not be null");
        mContentValues.put(BteamColumns.NAME, value);
        return this;
    }


    /**
     * The short name of the team. (String, Nullable)
     */
    public BteamContentValues putShortname(@Nullable String value) {
        mContentValues.put(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamContentValues putShortnameNull() {
        mContentValues.putNull(BteamColumns.SHORTNAME);
        return this;
    }

    /**
     * The code name of the team. (String, Nullable)
     */
    public BteamContentValues putCode(@Nullable String value) {
        mContentValues.put(BteamColumns.CODE, value);
        return this;
    }

    public BteamContentValues putCodeNull() {
        mContentValues.putNull(BteamColumns.CODE);
        return this;
    }

    /**
     * The squad market value of the team. (String, Nullable)
     */
    public BteamContentValues putValue(@Nullable String value) {
        mContentValues.put(BteamColumns.VALUE, value);
        return this;
    }

    public BteamContentValues putValueNull() {
        mContentValues.putNull(BteamColumns.VALUE);
        return this;
    }

    /**
     * The team's crest url. (String, Nullable)
     */
    public BteamContentValues putCresturl(@Nullable String value) {
        mContentValues.put(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamContentValues putCresturlNull() {
        mContentValues.putNull(BteamColumns.CRESTURL);
        return this;
    }
}
