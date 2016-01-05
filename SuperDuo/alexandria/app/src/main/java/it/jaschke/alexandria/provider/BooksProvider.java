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
package it.jaschke.alexandria.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import it.jaschke.alexandria.BuildConfig;
import it.jaschke.alexandria.provider.base.BaseContentProvider;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;

public class BooksProvider extends BaseContentProvider {
    private static final String TAG = BooksProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "it.jaschke.alexandria.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_AUTHORS = 0;
    private static final int URI_TYPE_AUTHORS_ID = 1;

    private static final int URI_TYPE_BOOKS = 2;
    private static final int URI_TYPE_BOOKS_ID = 3;

    private static final int URI_TYPE_CATEGORIES = 4;
    private static final int URI_TYPE_CATEGORIES_ID = 5;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AuthorsColumns.TABLE_NAME, URI_TYPE_AUTHORS);
        URI_MATCHER.addURI(AUTHORITY, AuthorsColumns.TABLE_NAME + "/#", URI_TYPE_AUTHORS_ID);
        URI_MATCHER.addURI(AUTHORITY, BooksColumns.TABLE_NAME, URI_TYPE_BOOKS);
        URI_MATCHER.addURI(AUTHORITY, BooksColumns.TABLE_NAME + "/#", URI_TYPE_BOOKS_ID);
        URI_MATCHER.addURI(AUTHORITY, CategoriesColumns.TABLE_NAME, URI_TYPE_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, CategoriesColumns.TABLE_NAME + "/#", URI_TYPE_CATEGORIES_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return BooksSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_AUTHORS:
                return TYPE_CURSOR_DIR + AuthorsColumns.TABLE_NAME;
            case URI_TYPE_AUTHORS_ID:
                return TYPE_CURSOR_ITEM + AuthorsColumns.TABLE_NAME;

            case URI_TYPE_BOOKS:
                return TYPE_CURSOR_DIR + BooksColumns.TABLE_NAME;
            case URI_TYPE_BOOKS_ID:
                return TYPE_CURSOR_ITEM + BooksColumns.TABLE_NAME;

            case URI_TYPE_CATEGORIES:
                return TYPE_CURSOR_DIR + CategoriesColumns.TABLE_NAME;
            case URI_TYPE_CATEGORIES_ID:
                return TYPE_CURSOR_ITEM + CategoriesColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_AUTHORS:
            case URI_TYPE_AUTHORS_ID:
                res.table = AuthorsColumns.TABLE_NAME;
                res.idColumn = AuthorsColumns._ID;
                res.tablesWithJoins = AuthorsColumns.TABLE_NAME;
                res.orderBy = AuthorsColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_BOOKS:
            case URI_TYPE_BOOKS_ID:
                res.table = BooksColumns.TABLE_NAME;
                res.idColumn = BooksColumns._ID;
                res.tablesWithJoins = BooksColumns.TABLE_NAME;
                res.orderBy = BooksColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_CATEGORIES:
            case URI_TYPE_CATEGORIES_ID:
                res.table = CategoriesColumns.TABLE_NAME;
                res.idColumn = CategoriesColumns._ID;
                res.tablesWithJoins = CategoriesColumns.TABLE_NAME;
                res.orderBy = CategoriesColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_AUTHORS_ID:
            case URI_TYPE_BOOKS_ID:
            case URI_TYPE_CATEGORIES_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
