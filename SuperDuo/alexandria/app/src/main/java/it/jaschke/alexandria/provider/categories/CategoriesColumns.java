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

import android.net.Uri;
import android.provider.BaseColumns;

import it.jaschke.alexandria.provider.BooksProvider;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;

/**
 * Table containing categories. Each entry is an category, book pairing.
 */
public class CategoriesColumns implements BaseColumns {
    public static final String TABLE_NAME = "categories";
    public static final Uri CONTENT_URI = Uri.parse(BooksProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Category name. (String, Not nullable)
     */
    public static final String NAME = "name";

    /**
     * The volume corresponding to the category. (String, Not nullable)
     */
    public static final String CATEGORYVOLUMEID = "categoryVolumeId";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME,
            CATEGORYVOLUMEID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(CATEGORYVOLUMEID) || c.contains("." + CATEGORYVOLUMEID)) return true;
        }
        return false;
    }

}
