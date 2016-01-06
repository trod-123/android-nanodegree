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

import it.jaschke.alexandria.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Table containing authors. Each entry is an author, book pairing.
 */
public interface AuthorsModel extends BaseModel {

    /**
     * Author's name. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getName();

    /**
     * The volume corresponding to the author. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getAuthorvolumeid();
}
