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
import android.database.Cursor;
import android.net.Uri;

import it.jaschke.alexandria.provider.base.AbstractSelection;

/**
 * Selection for the {@code books} table.
 */
public class BooksSelection extends AbstractSelection<BooksSelection> {
    @Override
    protected Uri baseUri() {
        return BooksColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BooksCursor} object, which is positioned before the first entry, or null.
     */
    public BooksCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BooksCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public BooksCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BooksCursor} object, which is positioned before the first entry, or null.
     */
    public BooksCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BooksCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public BooksCursor query(Context context) {
        return query(context, null);
    }


    public BooksSelection id(long... value) {
        addEquals("books." + BooksColumns._ID, toObjectArray(value));
        return this;
    }

    public BooksSelection idNot(long... value) {
        addNotEquals("books." + BooksColumns._ID, toObjectArray(value));
        return this;
    }

    public BooksSelection orderById(boolean desc) {
        orderBy("books." + BooksColumns._ID, desc);
        return this;
    }

    public BooksSelection orderById() {
        return orderById(false);
    }

    public BooksSelection bookid(String... value) {
        addEquals(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection bookidNot(String... value) {
        addNotEquals(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection bookidLike(String... value) {
        addLike(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection bookidContains(String... value) {
        addContains(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection bookidStartsWith(String... value) {
        addStartsWith(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection bookidEndsWith(String... value) {
        addEndsWith(BooksColumns.BOOKID, value);
        return this;
    }

    public BooksSelection orderByBookid(boolean desc) {
        orderBy(BooksColumns.BOOKID, desc);
        return this;
    }

    public BooksSelection orderByBookid() {
        orderBy(BooksColumns.BOOKID, false);
        return this;
    }

    public BooksSelection title(String... value) {
        addEquals(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection titleNot(String... value) {
        addNotEquals(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection titleLike(String... value) {
        addLike(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection titleContains(String... value) {
        addContains(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection titleStartsWith(String... value) {
        addStartsWith(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection titleEndsWith(String... value) {
        addEndsWith(BooksColumns.TITLE, value);
        return this;
    }

    public BooksSelection orderByTitle(boolean desc) {
        orderBy(BooksColumns.TITLE, desc);
        return this;
    }

    public BooksSelection orderByTitle() {
        orderBy(BooksColumns.TITLE, false);
        return this;
    }

    public BooksSelection subtitle(String... value) {
        addEquals(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection subtitleNot(String... value) {
        addNotEquals(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection subtitleLike(String... value) {
        addLike(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection subtitleContains(String... value) {
        addContains(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection subtitleStartsWith(String... value) {
        addStartsWith(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection subtitleEndsWith(String... value) {
        addEndsWith(BooksColumns.SUBTITLE, value);
        return this;
    }

    public BooksSelection orderBySubtitle(boolean desc) {
        orderBy(BooksColumns.SUBTITLE, desc);
        return this;
    }

    public BooksSelection orderBySubtitle() {
        orderBy(BooksColumns.SUBTITLE, false);
        return this;
    }

    public BooksSelection authors(String... value) {
        addEquals(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection authorsNot(String... value) {
        addNotEquals(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection authorsLike(String... value) {
        addLike(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection authorsContains(String... value) {
        addContains(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection authorsStartsWith(String... value) {
        addStartsWith(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection authorsEndsWith(String... value) {
        addEndsWith(BooksColumns.AUTHORS, value);
        return this;
    }

    public BooksSelection orderByAuthors(boolean desc) {
        orderBy(BooksColumns.AUTHORS, desc);
        return this;
    }

    public BooksSelection orderByAuthors() {
        orderBy(BooksColumns.AUTHORS, false);
        return this;
    }

    public BooksSelection publisher(String... value) {
        addEquals(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection publisherNot(String... value) {
        addNotEquals(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection publisherLike(String... value) {
        addLike(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection publisherContains(String... value) {
        addContains(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection publisherStartsWith(String... value) {
        addStartsWith(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection publisherEndsWith(String... value) {
        addEndsWith(BooksColumns.PUBLISHER, value);
        return this;
    }

    public BooksSelection orderByPublisher(boolean desc) {
        orderBy(BooksColumns.PUBLISHER, desc);
        return this;
    }

    public BooksSelection orderByPublisher() {
        orderBy(BooksColumns.PUBLISHER, false);
        return this;
    }

    public BooksSelection publisheddate(String... value) {
        addEquals(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection publisheddateNot(String... value) {
        addNotEquals(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection publisheddateLike(String... value) {
        addLike(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection publisheddateContains(String... value) {
        addContains(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection publisheddateStartsWith(String... value) {
        addStartsWith(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection publisheddateEndsWith(String... value) {
        addEndsWith(BooksColumns.PUBLISHEDDATE, value);
        return this;
    }

    public BooksSelection orderByPublisheddate(boolean desc) {
        orderBy(BooksColumns.PUBLISHEDDATE, desc);
        return this;
    }

    public BooksSelection orderByPublisheddate() {
        orderBy(BooksColumns.PUBLISHEDDATE, false);
        return this;
    }

    public BooksSelection description(String... value) {
        addEquals(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection descriptionNot(String... value) {
        addNotEquals(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection descriptionLike(String... value) {
        addLike(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection descriptionContains(String... value) {
        addContains(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection descriptionStartsWith(String... value) {
        addStartsWith(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection descriptionEndsWith(String... value) {
        addEndsWith(BooksColumns.DESCRIPTION, value);
        return this;
    }

    public BooksSelection orderByDescription(boolean desc) {
        orderBy(BooksColumns.DESCRIPTION, desc);
        return this;
    }

    public BooksSelection orderByDescription() {
        orderBy(BooksColumns.DESCRIPTION, false);
        return this;
    }

    public BooksSelection isbn10(String... value) {
        addEquals(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection isbn10Not(String... value) {
        addNotEquals(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection isbn10Like(String... value) {
        addLike(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection isbn10Contains(String... value) {
        addContains(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection isbn10StartsWith(String... value) {
        addStartsWith(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection isbn10EndsWith(String... value) {
        addEndsWith(BooksColumns.ISBN_10, value);
        return this;
    }

    public BooksSelection orderByIsbn10(boolean desc) {
        orderBy(BooksColumns.ISBN_10, desc);
        return this;
    }

    public BooksSelection orderByIsbn10() {
        orderBy(BooksColumns.ISBN_10, false);
        return this;
    }

    public BooksSelection isbn13(String... value) {
        addEquals(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection isbn13Not(String... value) {
        addNotEquals(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection isbn13Like(String... value) {
        addLike(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection isbn13Contains(String... value) {
        addContains(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection isbn13StartsWith(String... value) {
        addStartsWith(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection isbn13EndsWith(String... value) {
        addEndsWith(BooksColumns.ISBN_13, value);
        return this;
    }

    public BooksSelection orderByIsbn13(boolean desc) {
        orderBy(BooksColumns.ISBN_13, desc);
        return this;
    }

    public BooksSelection orderByIsbn13() {
        orderBy(BooksColumns.ISBN_13, false);
        return this;
    }

    public BooksSelection pagecount(Integer... value) {
        addEquals(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection pagecountNot(Integer... value) {
        addNotEquals(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection pagecountGt(int value) {
        addGreaterThan(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection pagecountGtEq(int value) {
        addGreaterThanOrEquals(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection pagecountLt(int value) {
        addLessThan(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection pagecountLtEq(int value) {
        addLessThanOrEquals(BooksColumns.PAGECOUNT, value);
        return this;
    }

    public BooksSelection orderByPagecount(boolean desc) {
        orderBy(BooksColumns.PAGECOUNT, desc);
        return this;
    }

    public BooksSelection orderByPagecount() {
        orderBy(BooksColumns.PAGECOUNT, false);
        return this;
    }

    public BooksSelection printtype(String... value) {
        addEquals(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection printtypeNot(String... value) {
        addNotEquals(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection printtypeLike(String... value) {
        addLike(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection printtypeContains(String... value) {
        addContains(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection printtypeStartsWith(String... value) {
        addStartsWith(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection printtypeEndsWith(String... value) {
        addEndsWith(BooksColumns.PRINTTYPE, value);
        return this;
    }

    public BooksSelection orderByPrinttype(boolean desc) {
        orderBy(BooksColumns.PRINTTYPE, desc);
        return this;
    }

    public BooksSelection orderByPrinttype() {
        orderBy(BooksColumns.PRINTTYPE, false);
        return this;
    }

    public BooksSelection categories(String... value) {
        addEquals(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection categoriesNot(String... value) {
        addNotEquals(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection categoriesLike(String... value) {
        addLike(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection categoriesContains(String... value) {
        addContains(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection categoriesStartsWith(String... value) {
        addStartsWith(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection categoriesEndsWith(String... value) {
        addEndsWith(BooksColumns.CATEGORIES, value);
        return this;
    }

    public BooksSelection orderByCategories(boolean desc) {
        orderBy(BooksColumns.CATEGORIES, desc);
        return this;
    }

    public BooksSelection orderByCategories() {
        orderBy(BooksColumns.CATEGORIES, false);
        return this;
    }

    public BooksSelection averagerating(Double... value) {
        addEquals(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection averageratingNot(Double... value) {
        addNotEquals(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection averageratingGt(double value) {
        addGreaterThan(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection averageratingGtEq(double value) {
        addGreaterThanOrEquals(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection averageratingLt(double value) {
        addLessThan(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection averageratingLtEq(double value) {
        addLessThanOrEquals(BooksColumns.AVERAGERATING, value);
        return this;
    }

    public BooksSelection orderByAveragerating(boolean desc) {
        orderBy(BooksColumns.AVERAGERATING, desc);
        return this;
    }

    public BooksSelection orderByAveragerating() {
        orderBy(BooksColumns.AVERAGERATING, false);
        return this;
    }

    public BooksSelection ratingscount(Integer... value) {
        addEquals(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection ratingscountNot(Integer... value) {
        addNotEquals(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection ratingscountGt(int value) {
        addGreaterThan(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection ratingscountGtEq(int value) {
        addGreaterThanOrEquals(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection ratingscountLt(int value) {
        addLessThan(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection ratingscountLtEq(int value) {
        addLessThanOrEquals(BooksColumns.RATINGSCOUNT, value);
        return this;
    }

    public BooksSelection orderByRatingscount(boolean desc) {
        orderBy(BooksColumns.RATINGSCOUNT, desc);
        return this;
    }

    public BooksSelection orderByRatingscount() {
        orderBy(BooksColumns.RATINGSCOUNT, false);
        return this;
    }

    public BooksSelection maturityrating(String... value) {
        addEquals(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection maturityratingNot(String... value) {
        addNotEquals(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection maturityratingLike(String... value) {
        addLike(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection maturityratingContains(String... value) {
        addContains(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection maturityratingStartsWith(String... value) {
        addStartsWith(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection maturityratingEndsWith(String... value) {
        addEndsWith(BooksColumns.MATURITYRATING, value);
        return this;
    }

    public BooksSelection orderByMaturityrating(boolean desc) {
        orderBy(BooksColumns.MATURITYRATING, desc);
        return this;
    }

    public BooksSelection orderByMaturityrating() {
        orderBy(BooksColumns.MATURITYRATING, false);
        return this;
    }

    public BooksSelection smallthumbnailurl(String... value) {
        addEquals(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection smallthumbnailurlNot(String... value) {
        addNotEquals(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection smallthumbnailurlLike(String... value) {
        addLike(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection smallthumbnailurlContains(String... value) {
        addContains(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection smallthumbnailurlStartsWith(String... value) {
        addStartsWith(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection smallthumbnailurlEndsWith(String... value) {
        addEndsWith(BooksColumns.SMALLTHUMBNAILURL, value);
        return this;
    }

    public BooksSelection orderBySmallthumbnailurl(boolean desc) {
        orderBy(BooksColumns.SMALLTHUMBNAILURL, desc);
        return this;
    }

    public BooksSelection orderBySmallthumbnailurl() {
        orderBy(BooksColumns.SMALLTHUMBNAILURL, false);
        return this;
    }

    public BooksSelection thumbnailurl(String... value) {
        addEquals(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection thumbnailurlNot(String... value) {
        addNotEquals(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection thumbnailurlLike(String... value) {
        addLike(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection thumbnailurlContains(String... value) {
        addContains(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection thumbnailurlStartsWith(String... value) {
        addStartsWith(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection thumbnailurlEndsWith(String... value) {
        addEndsWith(BooksColumns.THUMBNAILURL, value);
        return this;
    }

    public BooksSelection orderByThumbnailurl(boolean desc) {
        orderBy(BooksColumns.THUMBNAILURL, desc);
        return this;
    }

    public BooksSelection orderByThumbnailurl() {
        orderBy(BooksColumns.THUMBNAILURL, false);
        return this;
    }

    public BooksSelection language(String... value) {
        addEquals(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection languageNot(String... value) {
        addNotEquals(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection languageLike(String... value) {
        addLike(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection languageContains(String... value) {
        addContains(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection languageStartsWith(String... value) {
        addStartsWith(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection languageEndsWith(String... value) {
        addEndsWith(BooksColumns.LANGUAGE, value);
        return this;
    }

    public BooksSelection orderByLanguage(boolean desc) {
        orderBy(BooksColumns.LANGUAGE, desc);
        return this;
    }

    public BooksSelection orderByLanguage() {
        orderBy(BooksColumns.LANGUAGE, false);
        return this;
    }

    public BooksSelection previewlink(String... value) {
        addEquals(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection previewlinkNot(String... value) {
        addNotEquals(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection previewlinkLike(String... value) {
        addLike(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection previewlinkContains(String... value) {
        addContains(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection previewlinkStartsWith(String... value) {
        addStartsWith(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection previewlinkEndsWith(String... value) {
        addEndsWith(BooksColumns.PREVIEWLINK, value);
        return this;
    }

    public BooksSelection orderByPreviewlink(boolean desc) {
        orderBy(BooksColumns.PREVIEWLINK, desc);
        return this;
    }

    public BooksSelection orderByPreviewlink() {
        orderBy(BooksColumns.PREVIEWLINK, false);
        return this;
    }

    public BooksSelection infolink(String... value) {
        addEquals(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection infolinkNot(String... value) {
        addNotEquals(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection infolinkLike(String... value) {
        addLike(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection infolinkContains(String... value) {
        addContains(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection infolinkStartsWith(String... value) {
        addStartsWith(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection infolinkEndsWith(String... value) {
        addEndsWith(BooksColumns.INFOLINK, value);
        return this;
    }

    public BooksSelection orderByInfolink(boolean desc) {
        orderBy(BooksColumns.INFOLINK, desc);
        return this;
    }

    public BooksSelection orderByInfolink() {
        orderBy(BooksColumns.INFOLINK, false);
        return this;
    }

    public BooksSelection canonicalvolumelink(String... value) {
        addEquals(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection canonicalvolumelinkNot(String... value) {
        addNotEquals(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection canonicalvolumelinkLike(String... value) {
        addLike(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection canonicalvolumelinkContains(String... value) {
        addContains(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection canonicalvolumelinkStartsWith(String... value) {
        addStartsWith(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection canonicalvolumelinkEndsWith(String... value) {
        addEndsWith(BooksColumns.CANONICALVOLUMELINK, value);
        return this;
    }

    public BooksSelection orderByCanonicalvolumelink(boolean desc) {
        orderBy(BooksColumns.CANONICALVOLUMELINK, desc);
        return this;
    }

    public BooksSelection orderByCanonicalvolumelink() {
        orderBy(BooksColumns.CANONICALVOLUMELINK, false);
        return this;
    }

    public BooksSelection descriptionsnippet(String... value) {
        addEquals(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection descriptionsnippetNot(String... value) {
        addNotEquals(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection descriptionsnippetLike(String... value) {
        addLike(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection descriptionsnippetContains(String... value) {
        addContains(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection descriptionsnippetStartsWith(String... value) {
        addStartsWith(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection descriptionsnippetEndsWith(String... value) {
        addEndsWith(BooksColumns.DESCRIPTIONSNIPPET, value);
        return this;
    }

    public BooksSelection orderByDescriptionsnippet(boolean desc) {
        orderBy(BooksColumns.DESCRIPTIONSNIPPET, desc);
        return this;
    }

    public BooksSelection orderByDescriptionsnippet() {
        orderBy(BooksColumns.DESCRIPTIONSNIPPET, false);
        return this;
    }
}
