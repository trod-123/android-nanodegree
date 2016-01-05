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
package it.jaschke.alexandria.provider.categories;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import it.jaschke.alexandria.provider.base.AbstractSelection;

/**
 * Selection for the {@code categories} table.
 */
public class CategoriesSelection extends AbstractSelection<CategoriesSelection> {
    @Override
    protected Uri baseUri() {
        return CategoriesColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code CategoriesCursor} object, which is positioned before the first entry, or null.
     */
    public CategoriesCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new CategoriesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public CategoriesCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code CategoriesCursor} object, which is positioned before the first entry, or null.
     */
    public CategoriesCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new CategoriesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public CategoriesCursor query(Context context) {
        return query(context, null);
    }


    public CategoriesSelection id(long... value) {
        addEquals("categories." + CategoriesColumns._ID, toObjectArray(value));
        return this;
    }

    public CategoriesSelection idNot(long... value) {
        addNotEquals("categories." + CategoriesColumns._ID, toObjectArray(value));
        return this;
    }

    public CategoriesSelection orderById(boolean desc) {
        orderBy("categories." + CategoriesColumns._ID, desc);
        return this;
    }

    public CategoriesSelection orderById() {
        return orderById(false);
    }

    public CategoriesSelection name(String... value) {
        addEquals(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection nameNot(String... value) {
        addNotEquals(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection nameLike(String... value) {
        addLike(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection nameContains(String... value) {
        addContains(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection nameStartsWith(String... value) {
        addStartsWith(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection nameEndsWith(String... value) {
        addEndsWith(CategoriesColumns.NAME, value);
        return this;
    }

    public CategoriesSelection orderByName(boolean desc) {
        orderBy(CategoriesColumns.NAME, desc);
        return this;
    }

    public CategoriesSelection orderByName() {
        orderBy(CategoriesColumns.NAME, false);
        return this;
    }
}
