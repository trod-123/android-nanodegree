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

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.thirdarm.footballscores.provider.base.AbstractSelection;

/**
 * Selection for the {@code ateam} table.
 */
public class AteamSelection extends AbstractSelection<AteamSelection> {
    @Override
    protected Uri baseUri() {
        return AteamColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AteamCursor} object, which is positioned before the first entry, or null.
     */
    public AteamCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AteamCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public AteamCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AteamCursor} object, which is positioned before the first entry, or null.
     */
    public AteamCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AteamCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public AteamCursor query(Context context) {
        return query(context, null);
    }


    public AteamSelection id(long... value) {
        addEquals("ateam." + AteamColumns._ID, toObjectArray(value));
        return this;
    }

    public AteamSelection idNot(long... value) {
        addNotEquals("ateam." + AteamColumns._ID, toObjectArray(value));
        return this;
    }

    public AteamSelection orderById(boolean desc) {
        orderBy("ateam." + AteamColumns._ID, desc);
        return this;
    }

    public AteamSelection orderById() {
        return orderById(false);
    }

    public AteamSelection name(String... value) {
        addEquals(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection nameNot(String... value) {
        addNotEquals(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection nameLike(String... value) {
        addLike(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection nameContains(String... value) {
        addContains(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection nameStartsWith(String... value) {
        addStartsWith(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection nameEndsWith(String... value) {
        addEndsWith(AteamColumns.NAME, value);
        return this;
    }

    public AteamSelection orderByName(boolean desc) {
        orderBy(AteamColumns.NAME, desc);
        return this;
    }

    public AteamSelection orderByName() {
        orderBy(AteamColumns.NAME, false);
        return this;
    }

    public AteamSelection shortname(String... value) {
        addEquals(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection shortnameNot(String... value) {
        addNotEquals(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection shortnameLike(String... value) {
        addLike(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection shortnameContains(String... value) {
        addContains(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection shortnameStartsWith(String... value) {
        addStartsWith(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection shortnameEndsWith(String... value) {
        addEndsWith(AteamColumns.SHORTNAME, value);
        return this;
    }

    public AteamSelection orderByShortname(boolean desc) {
        orderBy(AteamColumns.SHORTNAME, desc);
        return this;
    }

    public AteamSelection orderByShortname() {
        orderBy(AteamColumns.SHORTNAME, false);
        return this;
    }

    public AteamSelection code(String... value) {
        addEquals(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection codeNot(String... value) {
        addNotEquals(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection codeLike(String... value) {
        addLike(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection codeContains(String... value) {
        addContains(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection codeStartsWith(String... value) {
        addStartsWith(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection codeEndsWith(String... value) {
        addEndsWith(AteamColumns.CODE, value);
        return this;
    }

    public AteamSelection orderByCode(boolean desc) {
        orderBy(AteamColumns.CODE, desc);
        return this;
    }

    public AteamSelection orderByCode() {
        orderBy(AteamColumns.CODE, false);
        return this;
    }

    public AteamSelection value(String... value) {
        addEquals(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection valueNot(String... value) {
        addNotEquals(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection valueLike(String... value) {
        addLike(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection valueContains(String... value) {
        addContains(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection valueStartsWith(String... value) {
        addStartsWith(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection valueEndsWith(String... value) {
        addEndsWith(AteamColumns.VALUE, value);
        return this;
    }

    public AteamSelection orderByValue(boolean desc) {
        orderBy(AteamColumns.VALUE, desc);
        return this;
    }

    public AteamSelection orderByValue() {
        orderBy(AteamColumns.VALUE, false);
        return this;
    }

    public AteamSelection cresturl(String... value) {
        addEquals(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection cresturlNot(String... value) {
        addNotEquals(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection cresturlLike(String... value) {
        addLike(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection cresturlContains(String... value) {
        addContains(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection cresturlStartsWith(String... value) {
        addStartsWith(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection cresturlEndsWith(String... value) {
        addEndsWith(AteamColumns.CRESTURL, value);
        return this;
    }

    public AteamSelection orderByCresturl(boolean desc) {
        orderBy(AteamColumns.CRESTURL, desc);
        return this;
    }

    public AteamSelection orderByCresturl() {
        orderBy(AteamColumns.CRESTURL, false);
        return this;
    }
}
