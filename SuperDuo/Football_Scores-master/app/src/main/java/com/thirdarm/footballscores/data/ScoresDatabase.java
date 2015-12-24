package com.thirdarm.footballscores.data;

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
 * A helper class for creating and managing the local scores database
 */
@Database(
        version = ScoresDatabase.VERSION,
        packageName = "com.thirdarm.footballscores"
)
public final class ScoresDatabase {

    private ScoresDatabase() {
    }

    public static final int VERSION = 1;

    // Define name of tables
    public static class Tables {
        @Table(ScoresColumns.class) public static final String SCORES = "scores";
        @Table(TeamsColumns.class) public static final String TEAMS = "teams";
    }

    @OnCreate public static void onCreate(Context c, SQLiteDatabase db) {
    }

    @OnUpgrade public static void onUpgrade(Context c, SQLiteDatabase db,
                                            int oldVersion, int newVersion) {
    }

    @OnConfigure public static void onConfigure(SQLiteDatabase db) {
    }

    @ExecOnCreate public static final String EXEC_ON_CREATE = "SELECT * FROM " + Tables.SCORES;
}
