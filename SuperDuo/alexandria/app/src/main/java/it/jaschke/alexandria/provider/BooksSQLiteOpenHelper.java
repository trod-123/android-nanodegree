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

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import it.jaschke.alexandria.BuildConfig;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;

public class BooksSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = BooksSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "alexandria.db";
    private static final int DATABASE_VERSION = 1;
    private static BooksSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final BooksSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_AUTHORS = "CREATE TABLE IF NOT EXISTS "
            + AuthorsColumns.TABLE_NAME + " ( "
            + AuthorsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AuthorsColumns.NAME + " TEXT NOT NULL, "
            + AuthorsColumns.AUTHORVOLUMEID + " TEXT NOT NULL "
            + " );";

    public static final String SQL_CREATE_TABLE_BOOKS = "CREATE TABLE IF NOT EXISTS "
            + BooksColumns.TABLE_NAME + " ( "
            + BooksColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BooksColumns.BOOKID + " TEXT NOT NULL, "
            + BooksColumns.TITLE + " TEXT NOT NULL, "
            + BooksColumns.SUBTITLE + " TEXT, "
            + BooksColumns.AUTHORS + " TEXT, "
            + BooksColumns.PUBLISHER + " TEXT, "
            + BooksColumns.PUBLISHEDDATE + " TEXT, "
            + BooksColumns.DESCRIPTION + " TEXT, "
            + BooksColumns.ISBN_10 + " TEXT, "
            + BooksColumns.ISBN_13 + " TEXT, "
            + BooksColumns.PAGECOUNT + " INTEGER, "
            + BooksColumns.PRINTTYPE + " TEXT, "
            + BooksColumns.CATEGORIES + " TEXT, "
            + BooksColumns.AVERAGERATING + " REAL, "
            + BooksColumns.RATINGSCOUNT + " INTEGER, "
            + BooksColumns.MATURITYRATING + " TEXT, "
            + BooksColumns.SMALLTHUMBNAILURL + " TEXT, "
            + BooksColumns.THUMBNAILURL + " TEXT, "
            + BooksColumns.LANGUAGE + " TEXT, "
            + BooksColumns.PREVIEWLINK + " TEXT, "
            + BooksColumns.INFOLINK + " TEXT, "
            + BooksColumns.CANONICALVOLUMELINK + " TEXT, "
            + BooksColumns.DESCRIPTIONSNIPPET + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS "
            + CategoriesColumns.TABLE_NAME + " ( "
            + CategoriesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CategoriesColumns.NAME + " TEXT NOT NULL, "
            + CategoriesColumns.CATEGORYVOLUMEID + " TEXT NOT NULL "
            + " );";

    // @formatter:on

    public static BooksSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static BooksSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static BooksSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new BooksSQLiteOpenHelper(context);
    }

    private BooksSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new BooksSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static BooksSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new BooksSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private BooksSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new BooksSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_AUTHORS);
        db.execSQL(SQL_CREATE_TABLE_BOOKS);
        db.execSQL(SQL_CREATE_TABLE_CATEGORIES);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
