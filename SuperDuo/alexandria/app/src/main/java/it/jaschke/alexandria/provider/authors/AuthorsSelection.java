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
package it.jaschke.alexandria.provider.authors;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import it.jaschke.alexandria.provider.base.AbstractSelection;

/**
 * Selection for the {@code authors} table.
 */
public class AuthorsSelection extends AbstractSelection<AuthorsSelection> {
    @Override
    protected Uri baseUri() {
        return AuthorsColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AuthorsCursor} object, which is positioned before the first entry, or null.
     */
    public AuthorsCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AuthorsCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public AuthorsCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code AuthorsCursor} object, which is positioned before the first entry, or null.
     */
    public AuthorsCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new AuthorsCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public AuthorsCursor query(Context context) {
        return query(context, null);
    }


    public AuthorsSelection id(long... value) {
        addEquals("authors." + AuthorsColumns._ID, toObjectArray(value));
        return this;
    }

    public AuthorsSelection idNot(long... value) {
        addNotEquals("authors." + AuthorsColumns._ID, toObjectArray(value));
        return this;
    }

    public AuthorsSelection orderById(boolean desc) {
        orderBy("authors." + AuthorsColumns._ID, desc);
        return this;
    }

    public AuthorsSelection orderById() {
        return orderById(false);
    }

    public AuthorsSelection name(String... value) {
        addEquals(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection nameNot(String... value) {
        addNotEquals(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection nameLike(String... value) {
        addLike(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection nameContains(String... value) {
        addContains(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection nameStartsWith(String... value) {
        addStartsWith(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection nameEndsWith(String... value) {
        addEndsWith(AuthorsColumns.NAME, value);
        return this;
    }

    public AuthorsSelection orderByName(boolean desc) {
        orderBy(AuthorsColumns.NAME, desc);
        return this;
    }

    public AuthorsSelection orderByName() {
        orderBy(AuthorsColumns.NAME, false);
        return this;
    }

    public AuthorsSelection authorvolumeid(String... value) {
        addEquals(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection authorvolumeidNot(String... value) {
        addNotEquals(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection authorvolumeidLike(String... value) {
        addLike(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection authorvolumeidContains(String... value) {
        addContains(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection authorvolumeidStartsWith(String... value) {
        addStartsWith(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection authorvolumeidEndsWith(String... value) {
        addEndsWith(AuthorsColumns.AUTHORVOLUMEID, value);
        return this;
    }

    public AuthorsSelection orderByAuthorvolumeid(boolean desc) {
        orderBy(AuthorsColumns.AUTHORVOLUMEID, desc);
        return this;
    }

    public AuthorsSelection orderByAuthorvolumeid() {
        orderBy(AuthorsColumns.AUTHORVOLUMEID, false);
        return this;
    }
}
