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
import android.database.Cursor;
import android.net.Uri;

import com.thirdarm.footballscores.provider.base.AbstractSelection;

/**
 * Selection for the {@code player} table.
 */
public class PlayerSelection extends AbstractSelection<PlayerSelection> {
    @Override
    protected Uri baseUri() {
        return PlayerColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code PlayerCursor} object, which is positioned before the first entry, or null.
     */
    public PlayerCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new PlayerCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public PlayerCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code PlayerCursor} object, which is positioned before the first entry, or null.
     */
    public PlayerCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new PlayerCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public PlayerCursor query(Context context) {
        return query(context, null);
    }


    public PlayerSelection id(long... value) {
        addEquals("player." + PlayerColumns._ID, toObjectArray(value));
        return this;
    }

    public PlayerSelection idNot(long... value) {
        addNotEquals("player." + PlayerColumns._ID, toObjectArray(value));
        return this;
    }

    public PlayerSelection orderById(boolean desc) {
        orderBy("player." + PlayerColumns._ID, desc);
        return this;
    }

    public PlayerSelection orderById() {
        return orderById(false);
    }

    public PlayerSelection firstname(String... value) {
        addEquals(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection firstnameNot(String... value) {
        addNotEquals(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection firstnameLike(String... value) {
        addLike(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection firstnameContains(String... value) {
        addContains(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection firstnameStartsWith(String... value) {
        addStartsWith(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection firstnameEndsWith(String... value) {
        addEndsWith(PlayerColumns.FIRSTNAME, value);
        return this;
    }

    public PlayerSelection orderByFirstname(boolean desc) {
        orderBy(PlayerColumns.FIRSTNAME, desc);
        return this;
    }

    public PlayerSelection orderByFirstname() {
        orderBy(PlayerColumns.FIRSTNAME, false);
        return this;
    }

    public PlayerSelection lastname(String... value) {
        addEquals(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection lastnameNot(String... value) {
        addNotEquals(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection lastnameLike(String... value) {
        addLike(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection lastnameContains(String... value) {
        addContains(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection lastnameStartsWith(String... value) {
        addStartsWith(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection lastnameEndsWith(String... value) {
        addEndsWith(PlayerColumns.LASTNAME, value);
        return this;
    }

    public PlayerSelection orderByLastname(boolean desc) {
        orderBy(PlayerColumns.LASTNAME, desc);
        return this;
    }

    public PlayerSelection orderByLastname() {
        orderBy(PlayerColumns.LASTNAME, false);
        return this;
    }

    public PlayerSelection playerposition(String... value) {
        addEquals(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection playerpositionNot(String... value) {
        addNotEquals(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection playerpositionLike(String... value) {
        addLike(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection playerpositionContains(String... value) {
        addContains(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection playerpositionStartsWith(String... value) {
        addStartsWith(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection playerpositionEndsWith(String... value) {
        addEndsWith(PlayerColumns.PLAYERPOSITION, value);
        return this;
    }

    public PlayerSelection orderByPlayerposition(boolean desc) {
        orderBy(PlayerColumns.PLAYERPOSITION, desc);
        return this;
    }

    public PlayerSelection orderByPlayerposition() {
        orderBy(PlayerColumns.PLAYERPOSITION, false);
        return this;
    }

    public PlayerSelection jerseynumber(Integer... value) {
        addEquals(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection jerseynumberNot(Integer... value) {
        addNotEquals(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection jerseynumberGt(int value) {
        addGreaterThan(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection jerseynumberGtEq(int value) {
        addGreaterThanOrEquals(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection jerseynumberLt(int value) {
        addLessThan(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection jerseynumberLtEq(int value) {
        addLessThanOrEquals(PlayerColumns.JERSEYNUMBER, value);
        return this;
    }

    public PlayerSelection orderByJerseynumber(boolean desc) {
        orderBy(PlayerColumns.JERSEYNUMBER, desc);
        return this;
    }

    public PlayerSelection orderByJerseynumber() {
        orderBy(PlayerColumns.JERSEYNUMBER, false);
        return this;
    }

    public PlayerSelection dateofbirth(String... value) {
        addEquals(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection dateofbirthNot(String... value) {
        addNotEquals(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection dateofbirthLike(String... value) {
        addLike(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection dateofbirthContains(String... value) {
        addContains(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection dateofbirthStartsWith(String... value) {
        addStartsWith(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection dateofbirthEndsWith(String... value) {
        addEndsWith(PlayerColumns.DATEOFBIRTH, value);
        return this;
    }

    public PlayerSelection orderByDateofbirth(boolean desc) {
        orderBy(PlayerColumns.DATEOFBIRTH, desc);
        return this;
    }

    public PlayerSelection orderByDateofbirth() {
        orderBy(PlayerColumns.DATEOFBIRTH, false);
        return this;
    }

    public PlayerSelection nationality(String... value) {
        addEquals(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection nationalityNot(String... value) {
        addNotEquals(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection nationalityLike(String... value) {
        addLike(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection nationalityContains(String... value) {
        addContains(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection nationalityStartsWith(String... value) {
        addStartsWith(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection nationalityEndsWith(String... value) {
        addEndsWith(PlayerColumns.NATIONALITY, value);
        return this;
    }

    public PlayerSelection orderByNationality(boolean desc) {
        orderBy(PlayerColumns.NATIONALITY, desc);
        return this;
    }

    public PlayerSelection orderByNationality() {
        orderBy(PlayerColumns.NATIONALITY, false);
        return this;
    }

    public PlayerSelection contractuntildate(String... value) {
        addEquals(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection contractuntildateNot(String... value) {
        addNotEquals(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection contractuntildateLike(String... value) {
        addLike(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection contractuntildateContains(String... value) {
        addContains(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection contractuntildateStartsWith(String... value) {
        addStartsWith(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection contractuntildateEndsWith(String... value) {
        addEndsWith(PlayerColumns.CONTRACTUNTILDATE, value);
        return this;
    }

    public PlayerSelection orderByContractuntildate(boolean desc) {
        orderBy(PlayerColumns.CONTRACTUNTILDATE, desc);
        return this;
    }

    public PlayerSelection orderByContractuntildate() {
        orderBy(PlayerColumns.CONTRACTUNTILDATE, false);
        return this;
    }

    public PlayerSelection marketvalue(Double... value) {
        addEquals(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection marketvalueNot(Double... value) {
        addNotEquals(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection marketvalueGt(double value) {
        addGreaterThan(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection marketvalueGtEq(double value) {
        addGreaterThanOrEquals(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection marketvalueLt(double value) {
        addLessThan(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection marketvalueLtEq(double value) {
        addLessThanOrEquals(PlayerColumns.MARKETVALUE, value);
        return this;
    }

    public PlayerSelection orderByMarketvalue(boolean desc) {
        orderBy(PlayerColumns.MARKETVALUE, desc);
        return this;
    }

    public PlayerSelection orderByMarketvalue() {
        orderBy(PlayerColumns.MARKETVALUE, false);
        return this;
    }
}
