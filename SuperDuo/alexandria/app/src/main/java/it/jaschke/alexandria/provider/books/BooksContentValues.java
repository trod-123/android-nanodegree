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

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import it.jaschke.alexandria.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code books} table.
 */
public class BooksContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return BooksColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable BooksSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable BooksSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The id of the volume. (String, Not nullable)
     */
    public BooksContentValues putBookid(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("bookid must not be null");
        mContentValues.put(BooksColumns.BOOKID, value);
        return this;
    }


    /**
     * The title of the volume. (String, Not nullable)
     */
    public BooksContentValues putTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("title must not be null");
        mContentValues.put(BooksColumns.TITLE, value);
        return this;
    }


    /**
     * The subtitle of the volume. (String, Nullable)
     */
    public BooksContentValues putSubtitle(@Nullable String value) {
        mContentValues.put(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksContentValues putSubtitleNull() {
        mContentValues.putNull(BooksColumns.SUBTITLE);
        return this;
    }

    /**
     * The authors. (String, Nullable)
     */
    public BooksContentValues putAuthors(@Nullable String value) {
        mContentValues.put(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksContentValues putAuthorsNull() {
        mContentValues.putNull(BooksColumns.AUTHORS);
        return this;
    }

    /**
     * Publishers. (String, Nullable)
     */
    public BooksContentValues putPublisher(@Nullable String value) {
        mContentValues.put(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksContentValues putPublisherNull() {
        mContentValues.putNull(BooksColumns.PUBLISHER);
        return this;
    }

    /**
     * The date of publishing. (String, Nullable)
     */
    public BooksContentValues putPublisheddate(@Nullable String value) {
        mContentValues.put(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksContentValues putPublisheddateNull() {
        mContentValues.putNull(BooksColumns.PUBLISHEDDATE);
        return this;
    }

    /**
     * The description. (String, Nullable)
     */
    public BooksContentValues putDescription(@Nullable String value) {
        mContentValues.put(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksContentValues putDescriptionNull() {
        mContentValues.putNull(BooksColumns.DESCRIPTION);
        return this;
    }

    /**
     * ISBN 10. (String, Nullable)
     */
    public BooksContentValues putIsbn10(@Nullable String value) {
        mContentValues.put(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksContentValues putIsbn10Null() {
        mContentValues.putNull(BooksColumns.ISBN_10);
        return this;
    }

    /**
     * ISBN 13. (String, Nullable)
     */
    public BooksContentValues putIsbn13(@Nullable String value) {
        mContentValues.put(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksContentValues putIsbn13Null() {
        mContentValues.putNull(BooksColumns.ISBN_13);
        return this;
    }

    /**
     * Number of pages. (Integer, Nullable)
     */
    public BooksContentValues putPagecount(@Nullable Integer value) {
        mContentValues.put(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksContentValues putPagecountNull() {
        mContentValues.putNull(BooksColumns.PAGECOUNT);
        return this;
    }

    /**
     * The volume's print type. (String, Nullable)
     */
    public BooksContentValues putPrinttype(@Nullable String value) {
        mContentValues.put(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksContentValues putPrinttypeNull() {
        mContentValues.putNull(BooksColumns.PRINTTYPE);
        return this;
    }

    /**
     * The volume's categories. (String, Nullable)
     */
    public BooksContentValues putCategories(@Nullable String value) {
        mContentValues.put(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksContentValues putCategoriesNull() {
        mContentValues.putNull(BooksColumns.CATEGORIES);
        return this;
    }

    /**
     * Average rating. (Double, Nullable)
     */
    public BooksContentValues putAveragerating(@Nullable Double value) {
        mContentValues.put(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksContentValues putAverageratingNull() {
        mContentValues.putNull(BooksColumns.AVERAGERATING);
        return this;
    }

    /**
     * Number of ratings. (Integer, Nullable)
     */
    public BooksContentValues putRatingscount(@Nullable Integer value) {
        mContentValues.put(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksContentValues putRatingscountNull() {
        mContentValues.putNull(BooksColumns.RATINGSCOUNT);
        return this;
    }

    /**
     * Maturity rating. (String, Nullable)
     */
    public BooksContentValues putMaturityrating(@Nullable String value) {
        mContentValues.put(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksContentValues putMaturityratingNull() {
        mContentValues.putNull(BooksColumns.MATURITYRATING);
        return this;
    }

    /**
     * Small thumbnail url. (String, Nullable)
     */
    public BooksContentValues putSmallthumbnailurl(@Nullable String value) {
        mContentValues.put(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksContentValues putSmallthumbnailurlNull() {
        mContentValues.putNull(BooksColumns.SMALLTHUMBNAILURL);
        return this;
    }

    /**
     * Large thumbnail url. (String, Nullable)
     */
    public BooksContentValues putThumbnailurl(@Nullable String value) {
        mContentValues.put(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksContentValues putThumbnailurlNull() {
        mContentValues.putNull(BooksColumns.THUMBNAILURL);
        return this;
    }

    /**
     * Language. (String, Nullable)
     */
    public BooksContentValues putLanguage(@Nullable String value) {
        mContentValues.put(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksContentValues putLanguageNull() {
        mContentValues.putNull(BooksColumns.LANGUAGE);
        return this;
    }

    /**
     * Preview url. (String, Nullable)
     */
    public BooksContentValues putPreviewlink(@Nullable String value) {
        mContentValues.put(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksContentValues putPreviewlinkNull() {
        mContentValues.putNull(BooksColumns.PREVIEWLINK);
        return this;
    }

    /**
     * Google Books info page. (String, Nullable)
     */
    public BooksContentValues putInfolink(@Nullable String value) {
        mContentValues.put(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksContentValues putInfolinkNull() {
        mContentValues.putNull(BooksColumns.INFOLINK);
        return this;
    }

    /**
     * Canonical volume link. (String, Nullable)
     */
    public BooksContentValues putCanonicalvolumelink(@Nullable String value) {
        mContentValues.put(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksContentValues putCanonicalvolumelinkNull() {
        mContentValues.putNull(BooksColumns.CANONICALVOLUMELINK);
        return this;
    }

    /**
     * Shortened description of volume. (String, Nullable)
     */
    public BooksContentValues putDescriptionsnippet(@Nullable String value) {
        mContentValues.put(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksContentValues putDescriptionsnippetNull() {
        mContentValues.putNull(BooksColumns.DESCRIPTIONSNIPPET);
        return this;
    }
}
