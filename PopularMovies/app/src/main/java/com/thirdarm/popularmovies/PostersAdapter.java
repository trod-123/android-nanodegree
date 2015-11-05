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

package com.thirdarm.popularmovies;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.MovieProjections.Results;
import com.thirdarm.popularmovies.utilities.AutoResizeImageView;
import com.thirdarm.popularmovies.utilities.AutoResizeTextView;
import com.thirdarm.popularmovies.utilities.ReleaseDates;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * CursorAdapter that fills the grid based on cursors
 */
public class PostersAdapter extends CursorAdapter {

    public static final String LOG_TAG = "PostersAdapter";

    private Context mContext;
    private String mPosterSize;


    public PostersAdapter(Context context, String posterSize, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
        mPosterSize = posterSize;
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.poster, parent, false);
        // This is for the bindView() method below, so that it can refer to each of these views and
        //  its place in the Adapter
        v.setTag(new ViewHolder(v));
        return v;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        // Gets the tag of the view as done in newView() above
        ViewHolder vh = (ViewHolder) view.getTag();
        // set movie title
        vh.poster_name.setText(cursor.getString(Results.COL_MOVIE_TITLE));
        // set release date
        vh.poster_date.setText(mContext.getString(
                        R.string.format_detail_release,
                        ReleaseDates.convertDateFormat(
                                cursor.getString(Results.COL_MOVIE_RELEASE_DATE)
                        )
                )
        );
        // set ratings
        vh.poster_rating.setText(new DecimalFormat("#0.0").format(
                cursor.getDouble(Results.COL_MOVIE_VOTE_AVERAGE))
        );
        vh.poster_votes.setText(mContext.getString(
                R.string.format_detail_rating, cursor.getInt(Results.COL_MOVIE_VOTE_COUNT))
        );
        // set poster
        // TODO: Find an appropriate placeholder image for poster paths that are null
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + mPosterSize +
                        cursor.getString(Results.COL_MOVIE_POSTER_PATH))
                .error(android.R.drawable.screen_background_light)
                .into(vh.poster);
    }


    // For butterknife to bind the resource views into a view holder which would be used in
    //  referencing and setting the fields for each view inflated from the poster layout
    static class ViewHolder {
        @Bind(R.id.container_detail_poster) AutoResizeImageView poster;
        @Bind(R.id.poster_name) AutoResizeTextView poster_name;
        @Bind(R.id.poster_date) AutoResizeTextView poster_date;
        @Bind(R.id.poster_rating) TextView poster_rating;
        @Bind(R.id.poster_votes) TextView poster_votes;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}