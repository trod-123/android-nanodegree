/*
 *  Copyright (C) 2015 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P1-2: Popular Movies
 *
 *  Currently for educational purposes only.
 */

package com.thirdarm.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by TROD on 20151005.
 *
 * A helper class for creating and managing the local movie database
 */
@Database(
        version = MovieDatabase.VERSION,
        packageName = "com.thirdarm.popularmovies"
)
public final class MovieDatabase {

    private MovieDatabase() {
    }

    public static final int VERSION = 1;

    // Define name of table
    @Table(MovieColumns.class) public static final String MOVIES = "movies";

    @OnCreate public static void onCreate(Context c, SQLiteDatabase db) {
    }

    @OnUpgrade public static void onUpgrade(Context c, SQLiteDatabase db,
                                            int oldVersion, int newVersion) {
    }

    @OnConfigure public static void onConfigure(SQLiteDatabase db) {
    }

    @ExecOnCreate public static final String EXEC_ON_CREATE = "SELECT * FROM " + MOVIES;
}
