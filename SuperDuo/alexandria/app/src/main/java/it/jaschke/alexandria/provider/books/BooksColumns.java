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
package it.jaschke.alexandria.provider.books;

import android.net.Uri;
import android.provider.BaseColumns;

import it.jaschke.alexandria.provider.BooksProvider;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;

/**
 * Table containing volumes. Each entry in table is complete information about a single volume (book).
 */
public class BooksColumns implements BaseColumns {
    public static final String TABLE_NAME = "books";
    public static final Uri CONTENT_URI = Uri.parse(BooksProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The id of the volume. (String, Not nullable)
     */
    public static final String BOOKID = "bookId";

    /**
     * The title of the volume. (String, Not nullable)
     */
    public static final String TITLE = "title";

    /**
     * The subtitle of the volume. (String, Nullable)
     */
    public static final String SUBTITLE = "subtitle";

    /**
     * The authors. (String, Nullable)
     */
    public static final String AUTHORS = "authors";

    /**
     * Publishers. (String, Nullable)
     */
    public static final String PUBLISHER = "publisher";

    /**
     * The date of publishing. (String, Nullable)
     */
    public static final String PUBLISHEDDATE = "publishedDate";

    /**
     * The description. (String, Nullable)
     */
    public static final String DESCRIPTION = "description";

    /**
     * ISBN 10. (String, Nullable)
     */
    public static final String ISBN_10 = "isbn_10";

    /**
     * ISBN 13. (String, Nullable)
     */
    public static final String ISBN_13 = "isbn_13";

    /**
     * Number of pages. (Integer, Nullable)
     */
    public static final String PAGECOUNT = "pageCount";

    /**
     * The volume's print type. (String, Nullable)
     */
    public static final String PRINTTYPE = "printType";

    /**
     * The volume's categories. (String, Nullable)
     */
    public static final String CATEGORIES = "categories";

    /**
     * Average rating. (Double, Nullable)
     */
    public static final String AVERAGERATING = "averageRating";

    /**
     * Number of ratings. (Integer, Nullable)
     */
    public static final String RATINGSCOUNT = "ratingsCount";

    /**
     * Maturity rating. (String, Nullable)
     */
    public static final String MATURITYRATING = "maturityRating";

    /**
     * Small thumbnail url. (String, Nullable)
     */
    public static final String SMALLTHUMBNAILURL = "smallThumbnailUrl";

    /**
     * Large thumbnail url. (String, Nullable)
     */
    public static final String THUMBNAILURL = "thumbnailUrl";

    /**
     * Language. (String, Nullable)
     */
    public static final String LANGUAGE = "language";

    /**
     * Preview url. (String, Nullable)
     */
    public static final String PREVIEWLINK = "previewLink";

    /**
     * Google Books info page. (String, Nullable)
     */
    public static final String INFOLINK = "infoLink";

    /**
     * Canonical volume link. (String, Nullable)
     */
    public static final String CANONICALVOLUMELINK = "canonicalVolumeLink";

    /**
     * Shortened description of volume. (String, Nullable)
     */
    public static final String DESCRIPTIONSNIPPET = "descriptionSnippet";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            BOOKID,
            TITLE,
            SUBTITLE,
            AUTHORS,
            PUBLISHER,
            PUBLISHEDDATE,
            DESCRIPTION,
            ISBN_10,
            ISBN_13,
            PAGECOUNT,
            PRINTTYPE,
            CATEGORIES,
            AVERAGERATING,
            RATINGSCOUNT,
            MATURITYRATING,
            SMALLTHUMBNAILURL,
            THUMBNAILURL,
            LANGUAGE,
            PREVIEWLINK,
            INFOLINK,
            CANONICALVOLUMELINK,
            DESCRIPTIONSNIPPET
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(BOOKID) || c.contains("." + BOOKID)) return true;
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(SUBTITLE) || c.contains("." + SUBTITLE)) return true;
            if (c.equals(AUTHORS) || c.contains("." + AUTHORS)) return true;
            if (c.equals(PUBLISHER) || c.contains("." + PUBLISHER)) return true;
            if (c.equals(PUBLISHEDDATE) || c.contains("." + PUBLISHEDDATE)) return true;
            if (c.equals(DESCRIPTION) || c.contains("." + DESCRIPTION)) return true;
            if (c.equals(ISBN_10) || c.contains("." + ISBN_10)) return true;
            if (c.equals(ISBN_13) || c.contains("." + ISBN_13)) return true;
            if (c.equals(PAGECOUNT) || c.contains("." + PAGECOUNT)) return true;
            if (c.equals(PRINTTYPE) || c.contains("." + PRINTTYPE)) return true;
            if (c.equals(CATEGORIES) || c.contains("." + CATEGORIES)) return true;
            if (c.equals(AVERAGERATING) || c.contains("." + AVERAGERATING)) return true;
            if (c.equals(RATINGSCOUNT) || c.contains("." + RATINGSCOUNT)) return true;
            if (c.equals(MATURITYRATING) || c.contains("." + MATURITYRATING)) return true;
            if (c.equals(SMALLTHUMBNAILURL) || c.contains("." + SMALLTHUMBNAILURL)) return true;
            if (c.equals(THUMBNAILURL) || c.contains("." + THUMBNAILURL)) return true;
            if (c.equals(LANGUAGE) || c.contains("." + LANGUAGE)) return true;
            if (c.equals(PREVIEWLINK) || c.contains("." + PREVIEWLINK)) return true;
            if (c.equals(INFOLINK) || c.contains("." + INFOLINK)) return true;
            if (c.equals(CANONICALVOLUMELINK) || c.contains("." + CANONICALVOLUMELINK)) return true;
            if (c.equals(DESCRIPTIONSNIPPET) || c.contains("." + DESCRIPTIONSNIPPET)) return true;
        }
        return false;
    }

}
