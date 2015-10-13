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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by TROD on 20151005.
 *
 * Content provider class for managing local movie data
 */
@ContentProvider(
        authority = MovieProvider.AUTHORITY,
        database = MovieDatabase.class,
        packageName = "com.thirdarm.popularmovies"
)
public class MovieProvider {

    public static final String AUTHORITY = "com.thirdarm.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // For setting the URI paths
    interface Path {
        String MOVIES = "movies";
    }

    // For building URIs for each of the URI paths in the interface above
    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.MOVIES) public static class Movies {

        // The base URI for the entire directory of movies. This will be the URI with which you
        //  will refer to to grab the cursor
        // content://com.thirdarm.popularmovies/movies [dir]
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movies"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        // The URI for a single movie item
        // content://com.thirdarm.popularmovies/movies/# [item]
        //
        // An InexactContentUri is used for all additional URIs built from the CONTENT_URI to
        //  access select data from the database
        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movies",
                whereColumn = MovieColumns.TMDB_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

        // For overriding the onInsert() method
        @NotifyInsert(paths = Path.MOVIES)
        public static Uri[] onInsert(ContentValues cv) {
            final long movieId = cv.getAsLong(MovieColumns.TMDB_ID);
            return new Uri[] {
                    Movies.withId(movieId),
            };
        }

        // For overriding the onBulkInsert() method
        @NotifyBulkInsert(paths = Path.MOVIES)
        public static Uri[] onBulkInsert(Context c, Uri uri, ContentValues[] cv, long[] ids) {
            return new Uri[] {
                    uri,
            };
        }

        // For overriding the onUpdate() method
        @NotifyUpdate(paths = Path.MOVIES + "/#")
        public static Uri[] onUpdate(Context c, Uri uri, String where, String[] whereArgs) {
            final long movieId = Long.valueOf(uri.getPathSegments().get(1));
            Cursor cursor = c.getContentResolver().query(uri, new String[] {
                MovieColumns.TMDB_ID,}, null, null, null);
            cursor.moveToFirst();
            cursor.close();

            return new Uri[] {
                    withId(movieId),
            };
        }

        // For overriding the onDelete() method
        @NotifyDelete(paths = Path.MOVIES + "/#")
        public static Uri[] onDelete(Context c, Uri uri) {
            final long movieId = Long.valueOf(uri.getPathSegments().get(1));
            Cursor cursor = c.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            cursor.close();

            return new Uri[] {
                    withId(movieId),
            };
        }

    }
}
