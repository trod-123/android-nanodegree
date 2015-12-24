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

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.thirdarm.footballscores.provider.base.AbstractSelection;
import com.thirdarm.footballscores.provider.ateam.*;
import com.thirdarm.footballscores.provider.bteam.*;

/**
 * Selection for the {@code fixture} table.
 */
public class FixtureSelection extends AbstractSelection<FixtureSelection> {
    @Override
    protected Uri baseUri() {
        return FixtureColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FixtureCursor} object, which is positioned before the first entry, or null.
     */
    public FixtureCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FixtureCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public FixtureCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FixtureCursor} object, which is positioned before the first entry, or null.
     */
    public FixtureCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FixtureCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public FixtureCursor query(Context context) {
        return query(context, null);
    }

    public CursorLoader cursorLoader(Context context, String[] projection) {
        return new CursorLoader(context, FixtureColumns.CONTENT_URI, projection, sel(), args(), order()) {
            @Override
            public Cursor loadInBackground() {
                return new FixtureCursor(super.loadInBackground());
            }
        };
    }

    public FixtureSelection id(long... value) {
        addEquals("fixture." + FixtureColumns._ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection idNot(long... value) {
        addNotEquals("fixture." + FixtureColumns._ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection orderById(boolean desc) {
        orderBy("fixture." + FixtureColumns._ID, desc);
        return this;
    }

    public FixtureSelection orderById() {
        return orderById(false);
    }

    public FixtureSelection date(String... value) {
        addEquals(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection dateNot(String... value) {
        addNotEquals(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection dateLike(String... value) {
        addLike(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection dateContains(String... value) {
        addContains(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection dateStartsWith(String... value) {
        addStartsWith(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection dateEndsWith(String... value) {
        addEndsWith(FixtureColumns.DATE, value);
        return this;
    }

    public FixtureSelection orderByDate(boolean desc) {
        orderBy(FixtureColumns.DATE, desc);
        return this;
    }

    public FixtureSelection orderByDate() {
        orderBy(FixtureColumns.DATE, false);
        return this;
    }

    public FixtureSelection time(String... value) {
        addEquals(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection timeNot(String... value) {
        addNotEquals(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection timeLike(String... value) {
        addLike(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection timeContains(String... value) {
        addContains(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection timeStartsWith(String... value) {
        addStartsWith(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection timeEndsWith(String... value) {
        addEndsWith(FixtureColumns.TIME, value);
        return this;
    }

    public FixtureSelection orderByTime(boolean desc) {
        orderBy(FixtureColumns.TIME, desc);
        return this;
    }

    public FixtureSelection orderByTime() {
        orderBy(FixtureColumns.TIME, false);
        return this;
    }

    public FixtureSelection status(Status... value) {
        addEquals(FixtureColumns.STATUS, value);
        return this;
    }

    public FixtureSelection statusNot(Status... value) {
        addNotEquals(FixtureColumns.STATUS, value);
        return this;
    }


    public FixtureSelection orderByStatus(boolean desc) {
        orderBy(FixtureColumns.STATUS, desc);
        return this;
    }

    public FixtureSelection orderByStatus() {
        orderBy(FixtureColumns.STATUS, false);
        return this;
    }

    public FixtureSelection teamaId(long... value) {
        addEquals(FixtureColumns.TEAMA_ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection teamaIdNot(long... value) {
        addNotEquals(FixtureColumns.TEAMA_ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection teamaIdGt(long value) {
        addGreaterThan(FixtureColumns.TEAMA_ID, value);
        return this;
    }

    public FixtureSelection teamaIdGtEq(long value) {
        addGreaterThanOrEquals(FixtureColumns.TEAMA_ID, value);
        return this;
    }

    public FixtureSelection teamaIdLt(long value) {
        addLessThan(FixtureColumns.TEAMA_ID, value);
        return this;
    }

    public FixtureSelection teamaIdLtEq(long value) {
        addLessThanOrEquals(FixtureColumns.TEAMA_ID, value);
        return this;
    }

    public FixtureSelection orderByTeamaId(boolean desc) {
        orderBy(FixtureColumns.TEAMA_ID, desc);
        return this;
    }

    public FixtureSelection orderByTeamaId() {
        orderBy(FixtureColumns.TEAMA_ID, false);
        return this;
    }

    public FixtureSelection ateamName(String... value) {
        addEquals(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection ateamNameNot(String... value) {
        addNotEquals(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection ateamNameLike(String... value) {
        addLike(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection ateamNameContains(String... value) {
        addContains(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection ateamNameStartsWith(String... value) {
        addStartsWith(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection ateamNameEndsWith(String... value) {
        addEndsWith(AteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection orderByAteamName(boolean desc) {
        orderBy(AteamColumns.NAME, desc);
        return this;
    }

    public FixtureSelection orderByAteamName() {
        orderBy(AteamColumns.NAME, false);
        return this;
    }

    public FixtureSelection ateamShortname(String... value) {
        addEquals(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection ateamShortnameNot(String... value) {
        addNotEquals(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection ateamShortnameLike(String... value) {
        addLike(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection ateamShortnameContains(String... value) {
        addContains(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection ateamShortnameStartsWith(String... value) {
        addStartsWith(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection ateamShortnameEndsWith(String... value) {
        addEndsWith(AteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection orderByAteamShortname(boolean desc) {
        orderBy(AteamColumns.SHORTNAME, desc);
        return this;
    }

    public FixtureSelection orderByAteamShortname() {
        orderBy(AteamColumns.SHORTNAME, false);
        return this;
    }

    public FixtureSelection ateamValue(String... value) {
        addEquals(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection ateamValueNot(String... value) {
        addNotEquals(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection ateamValueLike(String... value) {
        addLike(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection ateamValueContains(String... value) {
        addContains(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection ateamValueStartsWith(String... value) {
        addStartsWith(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection ateamValueEndsWith(String... value) {
        addEndsWith(AteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection orderByAteamValue(boolean desc) {
        orderBy(AteamColumns.VALUE, desc);
        return this;
    }

    public FixtureSelection orderByAteamValue() {
        orderBy(AteamColumns.VALUE, false);
        return this;
    }

    public FixtureSelection ateamCresturl(String... value) {
        addEquals(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection ateamCresturlNot(String... value) {
        addNotEquals(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection ateamCresturlLike(String... value) {
        addLike(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection ateamCresturlContains(String... value) {
        addContains(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection ateamCresturlStartsWith(String... value) {
        addStartsWith(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection ateamCresturlEndsWith(String... value) {
        addEndsWith(AteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection orderByAteamCresturl(boolean desc) {
        orderBy(AteamColumns.CRESTURL, desc);
        return this;
    }

    public FixtureSelection orderByAteamCresturl() {
        orderBy(AteamColumns.CRESTURL, false);
        return this;
    }

    public FixtureSelection teambId(long... value) {
        addEquals(FixtureColumns.TEAMB_ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection teambIdNot(long... value) {
        addNotEquals(FixtureColumns.TEAMB_ID, toObjectArray(value));
        return this;
    }

    public FixtureSelection teambIdGt(long value) {
        addGreaterThan(FixtureColumns.TEAMB_ID, value);
        return this;
    }

    public FixtureSelection teambIdGtEq(long value) {
        addGreaterThanOrEquals(FixtureColumns.TEAMB_ID, value);
        return this;
    }

    public FixtureSelection teambIdLt(long value) {
        addLessThan(FixtureColumns.TEAMB_ID, value);
        return this;
    }

    public FixtureSelection teambIdLtEq(long value) {
        addLessThanOrEquals(FixtureColumns.TEAMB_ID, value);
        return this;
    }

    public FixtureSelection orderByTeambId(boolean desc) {
        orderBy(FixtureColumns.TEAMB_ID, desc);
        return this;
    }

    public FixtureSelection orderByTeambId() {
        orderBy(FixtureColumns.TEAMB_ID, false);
        return this;
    }

    public FixtureSelection bteamName(String... value) {
        addEquals(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection bteamNameNot(String... value) {
        addNotEquals(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection bteamNameLike(String... value) {
        addLike(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection bteamNameContains(String... value) {
        addContains(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection bteamNameStartsWith(String... value) {
        addStartsWith(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection bteamNameEndsWith(String... value) {
        addEndsWith(BteamColumns.NAME, value);
        return this;
    }

    public FixtureSelection orderByBteamName(boolean desc) {
        orderBy(BteamColumns.NAME, desc);
        return this;
    }

    public FixtureSelection orderByBteamName() {
        orderBy(BteamColumns.NAME, false);
        return this;
    }

    public FixtureSelection bteamShortname(String... value) {
        addEquals(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection bteamShortnameNot(String... value) {
        addNotEquals(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection bteamShortnameLike(String... value) {
        addLike(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection bteamShortnameContains(String... value) {
        addContains(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection bteamShortnameStartsWith(String... value) {
        addStartsWith(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection bteamShortnameEndsWith(String... value) {
        addEndsWith(BteamColumns.SHORTNAME, value);
        return this;
    }

    public FixtureSelection orderByBteamShortname(boolean desc) {
        orderBy(BteamColumns.SHORTNAME, desc);
        return this;
    }

    public FixtureSelection orderByBteamShortname() {
        orderBy(BteamColumns.SHORTNAME, false);
        return this;
    }

    public FixtureSelection bteamValue(String... value) {
        addEquals(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection bteamValueNot(String... value) {
        addNotEquals(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection bteamValueLike(String... value) {
        addLike(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection bteamValueContains(String... value) {
        addContains(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection bteamValueStartsWith(String... value) {
        addStartsWith(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection bteamValueEndsWith(String... value) {
        addEndsWith(BteamColumns.VALUE, value);
        return this;
    }

    public FixtureSelection orderByBteamValue(boolean desc) {
        orderBy(BteamColumns.VALUE, desc);
        return this;
    }

    public FixtureSelection orderByBteamValue() {
        orderBy(BteamColumns.VALUE, false);
        return this;
    }

    public FixtureSelection bteamCresturl(String... value) {
        addEquals(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection bteamCresturlNot(String... value) {
        addNotEquals(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection bteamCresturlLike(String... value) {
        addLike(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection bteamCresturlContains(String... value) {
        addContains(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection bteamCresturlStartsWith(String... value) {
        addStartsWith(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection bteamCresturlEndsWith(String... value) {
        addEndsWith(BteamColumns.CRESTURL, value);
        return this;
    }

    public FixtureSelection orderByBteamCresturl(boolean desc) {
        orderBy(BteamColumns.CRESTURL, desc);
        return this;
    }

    public FixtureSelection orderByBteamCresturl() {
        orderBy(BteamColumns.CRESTURL, false);
        return this;
    }

    public FixtureSelection leagueid(int... value) {
        addEquals(FixtureColumns.LEAGUEID, toObjectArray(value));
        return this;
    }

    public FixtureSelection leagueidNot(int... value) {
        addNotEquals(FixtureColumns.LEAGUEID, toObjectArray(value));
        return this;
    }

    public FixtureSelection leagueidGt(int value) {
        addGreaterThan(FixtureColumns.LEAGUEID, value);
        return this;
    }

    public FixtureSelection leagueidGtEq(int value) {
        addGreaterThanOrEquals(FixtureColumns.LEAGUEID, value);
        return this;
    }

    public FixtureSelection leagueidLt(int value) {
        addLessThan(FixtureColumns.LEAGUEID, value);
        return this;
    }

    public FixtureSelection leagueidLtEq(int value) {
        addLessThanOrEquals(FixtureColumns.LEAGUEID, value);
        return this;
    }

    public FixtureSelection orderByLeagueid(boolean desc) {
        orderBy(FixtureColumns.LEAGUEID, desc);
        return this;
    }

    public FixtureSelection orderByLeagueid() {
        orderBy(FixtureColumns.LEAGUEID, false);
        return this;
    }

    public FixtureSelection homegoals(Integer... value) {
        addEquals(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection homegoalsNot(Integer... value) {
        addNotEquals(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection homegoalsGt(int value) {
        addGreaterThan(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection homegoalsGtEq(int value) {
        addGreaterThanOrEquals(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection homegoalsLt(int value) {
        addLessThan(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection homegoalsLtEq(int value) {
        addLessThanOrEquals(FixtureColumns.HOMEGOALS, value);
        return this;
    }

    public FixtureSelection orderByHomegoals(boolean desc) {
        orderBy(FixtureColumns.HOMEGOALS, desc);
        return this;
    }

    public FixtureSelection orderByHomegoals() {
        orderBy(FixtureColumns.HOMEGOALS, false);
        return this;
    }

    public FixtureSelection awaygoals(Integer... value) {
        addEquals(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection awaygoalsNot(Integer... value) {
        addNotEquals(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection awaygoalsGt(int value) {
        addGreaterThan(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection awaygoalsGtEq(int value) {
        addGreaterThanOrEquals(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection awaygoalsLt(int value) {
        addLessThan(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection awaygoalsLtEq(int value) {
        addLessThanOrEquals(FixtureColumns.AWAYGOALS, value);
        return this;
    }

    public FixtureSelection orderByAwaygoals(boolean desc) {
        orderBy(FixtureColumns.AWAYGOALS, desc);
        return this;
    }

    public FixtureSelection orderByAwaygoals() {
        orderBy(FixtureColumns.AWAYGOALS, false);
        return this;
    }

    public FixtureSelection matchid(int... value) {
        addEquals(FixtureColumns.MATCHID, toObjectArray(value));
        return this;
    }

    public FixtureSelection matchidNot(int... value) {
        addNotEquals(FixtureColumns.MATCHID, toObjectArray(value));
        return this;
    }

    public FixtureSelection matchidGt(int value) {
        addGreaterThan(FixtureColumns.MATCHID, value);
        return this;
    }

    public FixtureSelection matchidGtEq(int value) {
        addGreaterThanOrEquals(FixtureColumns.MATCHID, value);
        return this;
    }

    public FixtureSelection matchidLt(int value) {
        addLessThan(FixtureColumns.MATCHID, value);
        return this;
    }

    public FixtureSelection matchidLtEq(int value) {
        addLessThanOrEquals(FixtureColumns.MATCHID, value);
        return this;
    }

    public FixtureSelection orderByMatchid(boolean desc) {
        orderBy(FixtureColumns.MATCHID, desc);
        return this;
    }

    public FixtureSelection orderByMatchid() {
        orderBy(FixtureColumns.MATCHID, false);
        return this;
    }

    public FixtureSelection matchday(int... value) {
        addEquals(FixtureColumns.MATCHDAY, toObjectArray(value));
        return this;
    }

    public FixtureSelection matchdayNot(int... value) {
        addNotEquals(FixtureColumns.MATCHDAY, toObjectArray(value));
        return this;
    }

    public FixtureSelection matchdayGt(int value) {
        addGreaterThan(FixtureColumns.MATCHDAY, value);
        return this;
    }

    public FixtureSelection matchdayGtEq(int value) {
        addGreaterThanOrEquals(FixtureColumns.MATCHDAY, value);
        return this;
    }

    public FixtureSelection matchdayLt(int value) {
        addLessThan(FixtureColumns.MATCHDAY, value);
        return this;
    }

    public FixtureSelection matchdayLtEq(int value) {
        addLessThanOrEquals(FixtureColumns.MATCHDAY, value);
        return this;
    }

    public FixtureSelection orderByMatchday(boolean desc) {
        orderBy(FixtureColumns.MATCHDAY, desc);
        return this;
    }

    public FixtureSelection orderByMatchday() {
        orderBy(FixtureColumns.MATCHDAY, false);
        return this;
    }
}
