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

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import it.jaschke.alexandria.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code books} table.
 */
public class BooksCursor extends AbstractCursor implements BooksModel {
    public BooksCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(BooksColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The id of the volume. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    public String getBookid() {
        String res = getStringOrNull(BooksColumns.BOOKID);
        if (res == null)
            throw new NullPointerException("The value of 'bookid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The title of the volume. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getTitle() {
        String res = getStringOrNull(BooksColumns.TITLE);
        return res;
    }

    /**
     * The subtitle of the volume. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getSubtitle() {
        String res = getStringOrNull(BooksColumns.SUBTITLE);
        return res;
    }

    /**
     * The authors. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getAuthors() {
        String res = getStringOrNull(BooksColumns.AUTHORS);
        return res;
    }

    /**
     * Publishers. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getPublisher() {
        String res = getStringOrNull(BooksColumns.PUBLISHER);
        return res;
    }

    /**
     * The date of publishing. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getPublisheddate() {
        String res = getStringOrNull(BooksColumns.PUBLISHEDDATE);
        return res;
    }

    /**
     * The description. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getDescription() {
        String res = getStringOrNull(BooksColumns.DESCRIPTION);
        return res;
    }

    /**
     * ISBN 10. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getIsbn10() {
        String res = getStringOrNull(BooksColumns.ISBN_10);
        return res;
    }

    /**
     * ISBN 13. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getIsbn13() {
        String res = getStringOrNull(BooksColumns.ISBN_13);
        return res;
    }

    /**
     * Number of pages. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Integer getPagecount() {
        Integer res = getIntegerOrNull(BooksColumns.PAGECOUNT);
        return res;
    }

    /**
     * The volume's categories. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getCategories() {
        String res = getStringOrNull(BooksColumns.CATEGORIES);
        return res;
    }

    /**
     * Average rating. (Double, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Double getAveragerating() {
        Double res = getDoubleOrNull(BooksColumns.AVERAGERATING);
        return res;
    }

    /**
     * Number of ratings. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public Integer getRatingscount() {
        Integer res = getIntegerOrNull(BooksColumns.RATINGSCOUNT);
        return res;
    }

    /**
     * Small thumbnail url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getSmallthumbnailurl() {
        String res = getStringOrNull(BooksColumns.SMALLTHUMBNAILURL);
        return res;
    }

    /**
     * Large thumbnail url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getThumbnailurl() {
        String res = getStringOrNull(BooksColumns.THUMBNAILURL);
        return res;
    }

    /**
     * Language. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getLanguage() {
        String res = getStringOrNull(BooksColumns.LANGUAGE);
        return res;
    }

    /**
     * Google Books info page. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getInfolink() {
        String res = getStringOrNull(BooksColumns.INFOLINK);
        return res;
    }

    /**
     * Shortened description of volume. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getDescriptionsnippet() {
        String res = getStringOrNull(BooksColumns.DESCRIPTIONSNIPPET);
        return res;
    }
}
