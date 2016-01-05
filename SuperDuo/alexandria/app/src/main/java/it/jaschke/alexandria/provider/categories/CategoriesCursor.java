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

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import it.jaschke.alexandria.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code categories} table.
 */
public class CategoriesCursor extends AbstractCursor implements CategoriesModel {
    public CategoriesCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(CategoriesColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Category name. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    public String getName() {
        String res = getStringOrNull(CategoriesColumns.NAME);
        return res;
    }
}
