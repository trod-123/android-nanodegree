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
import android.database.Cursor;
import android.net.Uri;

import com.thirdarm.footballscores.provider.base.AbstractSelection;

/**
 * Selection for the {@code bteam} table.
 */
public class BteamSelection extends AbstractSelection<BteamSelection> {
    @Override
    protected Uri baseUri() {
        return BteamColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BteamCursor} object, which is positioned before the first entry, or null.
     */
    public BteamCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BteamCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public BteamCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BteamCursor} object, which is positioned before the first entry, or null.
     */
    public BteamCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BteamCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public BteamCursor query(Context context) {
        return query(context, null);
    }


    public BteamSelection id(long... value) {
        addEquals("bteam." + BteamColumns._ID, toObjectArray(value));
        return this;
    }

    public BteamSelection idNot(long... value) {
        addNotEquals("bteam." + BteamColumns._ID, toObjectArray(value));
        return this;
    }

    public BteamSelection orderById(boolean desc) {
        orderBy("bteam." + BteamColumns._ID, desc);
        return this;
    }

    public BteamSelection orderById() {
        return orderById(false);
    }

    public BteamSelection name(String... value) {
        addEquals(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection nameNot(String... value) {
        addNotEquals(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection nameLike(String... value) {
        addLike(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection nameContains(String... value) {
        addContains(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection nameStartsWith(String... value) {
        addStartsWith(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection nameEndsWith(String... value) {
        addEndsWith(BteamColumns.NAME, value);
        return this;
    }

    public BteamSelection orderByName(boolean desc) {
        orderBy(BteamColumns.NAME, desc);
        return this;
    }

    public BteamSelection orderByName() {
        orderBy(BteamColumns.NAME, false);
        return this;
    }

    public BteamSelection shortname(String... value) {
        addEquals(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection shortnameNot(String... value) {
        addNotEquals(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection shortnameLike(String... value) {
        addLike(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection shortnameContains(String... value) {
        addContains(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection shortnameStartsWith(String... value) {
        addStartsWith(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection shortnameEndsWith(String... value) {
        addEndsWith(BteamColumns.SHORTNAME, value);
        return this;
    }

    public BteamSelection orderByShortname(boolean desc) {
        orderBy(BteamColumns.SHORTNAME, desc);
        return this;
    }

    public BteamSelection orderByShortname() {
        orderBy(BteamColumns.SHORTNAME, false);
        return this;
    }

    public BteamSelection code(String... value) {
        addEquals(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection codeNot(String... value) {
        addNotEquals(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection codeLike(String... value) {
        addLike(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection codeContains(String... value) {
        addContains(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection codeStartsWith(String... value) {
        addStartsWith(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection codeEndsWith(String... value) {
        addEndsWith(BteamColumns.CODE, value);
        return this;
    }

    public BteamSelection orderByCode(boolean desc) {
        orderBy(BteamColumns.CODE, desc);
        return this;
    }

    public BteamSelection orderByCode() {
        orderBy(BteamColumns.CODE, false);
        return this;
    }

    public BteamSelection value(String... value) {
        addEquals(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection valueNot(String... value) {
        addNotEquals(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection valueLike(String... value) {
        addLike(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection valueContains(String... value) {
        addContains(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection valueStartsWith(String... value) {
        addStartsWith(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection valueEndsWith(String... value) {
        addEndsWith(BteamColumns.VALUE, value);
        return this;
    }

    public BteamSelection orderByValue(boolean desc) {
        orderBy(BteamColumns.VALUE, desc);
        return this;
    }

    public BteamSelection orderByValue() {
        orderBy(BteamColumns.VALUE, false);
        return this;
    }

    public BteamSelection cresturl(String... value) {
        addEquals(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection cresturlNot(String... value) {
        addNotEquals(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection cresturlLike(String... value) {
        addLike(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection cresturlContains(String... value) {
        addContains(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection cresturlStartsWith(String... value) {
        addStartsWith(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection cresturlEndsWith(String... value) {
        addEndsWith(BteamColumns.CRESTURL, value);
        return this;
    }

    public BteamSelection orderByCresturl(boolean desc) {
        orderBy(BteamColumns.CRESTURL, desc);
        return this;
    }

    public BteamSelection orderByCresturl() {
        orderBy(BteamColumns.CRESTURL, false);
        return this;
    }
}
