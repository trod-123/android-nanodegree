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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.MovieProjections.Results;
import com.thirdarm.popularmovies.utilities.AutoResizeImageView;
import com.thirdarm.popularmovies.utilities.AutoResizeTextView;
import com.thirdarm.popularmovies.utilities.ReleaseDates;
import com.thirdarm.popularmovies.utilities.SwapViewContainers;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * CursorAdapter that fills the grid based on cursors
 */
public class PostersAdapter extends CursorAdapter {

    public static final String LOG_TAG = "PostersAdapter";

    private Context mContext;
    private String mPosterSize;
    private View mRootView;


    public PostersAdapter(Context context, String posterSize, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
        mPosterSize = posterSize;
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.poster, parent, false);
        // This is for the bindView() method below, so that it can refer to each of these views and
        //  its place in the Adapter
        mRootView.setTag(new ViewHolder(mRootView));
        return mRootView;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        // Gets the tag of the view as done in newView() above
        final ViewHolder vh = (ViewHolder) view.getTag();
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
        vh.poster_rating.setText(context.getString(R.string.format_poster_rating,
                        new DecimalFormat("#0.0").format(cursor.getDouble(Results.COL_MOVIE_VOTE_AVERAGE)),
                        cursor.getInt(Results.COL_MOVIE_VOTE_COUNT)
                )
        );

        // TODO: Fix up poster placeholder image so that it appears properly and not all zoomed in
//        Target bitmapTarget = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                SwapViewContainers.showViewContainer(vh.poster, mRootView);
//                //SwapViewContainers.hideViewContainer(vh.empty_poster, mRootView);
//                vh.poster.setImageBitmap(bitmap);
//                Log.d(LOG_TAG, "ON BITMAP LOADED");
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//                //SwapViewContainers.showViewContainer(vh.empty_poster, mRootView);
//                SwapViewContainers.hideViewContainer(vh.poster, mRootView);
//                //vh.empty_poster.setImageDrawable(errorDrawable);
//                Log.d(LOG_TAG, "ON BITMAP FAILED");
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };

        // set poster
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + mPosterSize +
                        cursor.getString(Results.COL_MOVIE_POSTER_PATH))
                .error(R.drawable.ic_wallpaper_black_48dp)
                .into(vh.poster);
    }

    // For butterknife to bind the resource views into a view holder which would be used in
    //  referencing and setting the fields for each view inflated from the poster layout
    static class ViewHolder {
        @BindView(R.id.imageview_detail_poster) AutoResizeImageView poster;
        //@Bind(R.id.imageview_detail_poster_empty) ImageView empty_poster;
        @BindView(R.id.poster_name) AutoResizeTextView poster_name;
        @BindView(R.id.poster_date) AutoResizeTextView poster_date;
        @BindView(R.id.poster_rating) TextView poster_rating;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}