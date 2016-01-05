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

import android.net.Uri;
import android.provider.BaseColumns;

import it.jaschke.alexandria.provider.BooksProvider;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;

/**
 * Table containing authors. Each entry is an author, book pairing.
 */
public class AuthorsColumns implements BaseColumns {
    public static final String TABLE_NAME = "authors";
    public static final Uri CONTENT_URI = Uri.parse(BooksProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Author's name. (String, Nullable)
     */
    public static final String NAME = "name";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
        }
        return false;
    }

}
