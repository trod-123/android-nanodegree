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

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.footballscores.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code player} table.
 */
public class PlayerContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return PlayerColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable PlayerSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable PlayerSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The player's first name. (String, Not nullable)
     */
    public PlayerContentValues putFirstname(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("firstname must not be null");
        mContentValues.put(PlayerColumns.FIRSTNAME, value);
        return this;
    }


    /**
     * The player's last name. (String, Not nullable)
     */
    public PlayerContentValues putLastname(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("lastname must not be null");
        mContentValues.put(PlayerColumns.LASTNAME, value);
        return this;
    }


    /**
     * The player's position. (String, Not nullable)
     */
    public PlayerContentValues putPlayerposition(@Nullable String value) {
        mContentValues.put(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerContentValues putPlayerpositionNull() {
        mContentValues.putNull(PlayerColumns.PLAYERPOSITION);
        return this;
    }

    /**
     * The player's jersey number. (Integer, Nullable)
     */
    public PlayerContentValues putJerseynumber(@Nullable Integer value) {
        mContentValues.put(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerContentValues putJerseynumberNull() {
        mContentValues.putNull(PlayerColumns.JERSEYNUMBER);
        return this;
    }

    /**
     * The player's birth date. (String, Nullable)
     */
    public PlayerContentValues putDateofbirth(@Nullable String value) {
        mContentValues.put(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerContentValues putDateofbirthNull() {
        mContentValues.putNull(PlayerColumns.DATEOFBIRTH);
        return this;
    }

    /**
     * The player's nationality. (String, Nullable)
     */
    public PlayerContentValues putNationality(@Nullable String value) {
        mContentValues.put(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerContentValues putNationalityNull() {
        mContentValues.putNull(PlayerColumns.NATIONALITY);
        return this;
    }

    /**
     * The player's contract expiry date. (String, Nullable)
     */
    public PlayerContentValues putContractuntildate(@Nullable String value) {
        mContentValues.put(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerContentValues putContractuntildateNull() {
        mContentValues.putNull(PlayerColumns.CONTRACTUNTILDATE);
        return this;
    }

    /**
     * The player's market value in the user's locale currency. (Double, Nullable)
     */
    public PlayerContentValues putMarketvalue(@Nullable Double value) {
        mContentValues.put(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerContentValues putMarketvalueNull() {
        mContentValues.putNull(PlayerColumns.MARKETVALUE);
        return this;
    }
}
