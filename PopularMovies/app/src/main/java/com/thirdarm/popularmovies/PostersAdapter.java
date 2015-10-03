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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.utilities.AutoResizeImageView;
import com.thirdarm.popularmovies.utilities.AutoResizeTextView;
import com.thirdarm.popularmovies.utilities.ReleaseDates;

import java.text.DecimalFormat;
import java.util.List;

/**
 * ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
 *  to be displayed in the grid.
 * Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
 *  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
 *
 *  TODO: Turn this into a CursorAdapter so that it will fill the grid based on cursors
 */
public class PostersAdapter extends BaseAdapter {
    private Context mContext;
    private List<MovieDB> mMovies;
    private LayoutInflater inflater;
    private String mPosterSize;

    public PostersAdapter(Context c, List<MovieDB> movies, String posterSize) {
        mContext = c;
        mMovies = movies;
        mPosterSize = posterSize;
        inflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return mMovies.size();
    }

    // returns the actual object at specified position
    public Object getItem(int position) {
        return null;
    }

    // returns the row id of the object at specified position
    public long getItemId(int position) {
        return 0;
    }

    // Creates a new view (in this case, ImageView) for each item referenced by the Adapter
    // How it works:
    //  - a view is passed in, which is normally a recycled object
    //  - checks to see if that view is null
    //     - if view is null, a view is initialized and configured with desired properties
    //     - if view is not null, that view is then returned
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poster, null);
        }

        // set movie title
        ((AutoResizeTextView) convertView.findViewById(R.id.poster_name))
                .setText(mMovies.get(position).getTitle());

        // set release date
        ((AutoResizeTextView) convertView.findViewById(R.id.poster_date))
                .setText(ReleaseDates.convertDateFormat(mMovies.get(position).getReleaseDate()));

        // set ratings
        String votesTense = mContext.getResources().getString(R.string.detail_votes);
        if (mMovies.get(position).getVoteCount() != 1) {
            votesTense += "s";
        }
        ((AutoResizeTextView) convertView.findViewById(R.id.poster_rating)).setText(
                mContext.getResources().getString(R.string.detail_ratings)
                        + new DecimalFormat("#.##").format(mMovies.get(position).getVoteAverage())
                        + " ("
                        + mMovies.get(position).getVoteCount()
                        + " "
                        + votesTense.toLowerCase()
                        + ")"
        );

        // set poster
        // TODO: Find an appropriate placeholder image for poster paths that are null
        AutoResizeImageView imageView =
                (AutoResizeImageView) convertView.findViewById(R.id.poster);
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + mPosterSize + mMovies.get(position).getPosterPath())
                .error(android.R.drawable.screen_background_light)
                .into(imageView);

        return convertView;
    }
}