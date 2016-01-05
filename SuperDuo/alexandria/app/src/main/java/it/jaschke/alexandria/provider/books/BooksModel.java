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

import it.jaschke.alexandria.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Table containing volumes. Each entry in table is complete information about a single volume (book).
 */
public interface BooksModel extends BaseModel {

    /**
     * The id of the volume. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getBookid();

    /**
     * The title of the volume. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * The subtitle of the volume. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getSubtitle();

    /**
     * The authors. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getAuthors();

    /**
     * Publishers. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getPublisher();

    /**
     * The date of publishing. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getPublisheddate();

    /**
     * The description. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getDescription();

    /**
     * ISBN 10. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getIsbn10();

    /**
     * ISBN 13. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getIsbn13();

    /**
     * Number of pages. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Integer getPagecount();

    /**
     * The volume's print type. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getPrinttype();

    /**
     * The volume's categories. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getCategories();

    /**
     * Average rating. (Double, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Double getAveragerating();

    /**
     * Number of ratings. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Integer getRatingscount();

    /**
     * Maturity rating. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getMaturityrating();

    /**
     * Small thumbnail url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getSmallthumbnailurl();

    /**
     * Large thumbnail url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getThumbnailurl();

    /**
     * Language. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getLanguage();

    /**
     * Preview url. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getPreviewlink();

    /**
     * Google Books info page. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getInfolink();

    /**
     * Canonical volume link. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getCanonicalvolumelink();

    /**
     * Shortened description of volume. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getDescriptionsnippet();
}
