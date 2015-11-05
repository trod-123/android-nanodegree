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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.Backdrop;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
 *  to be displayed in the grid.
 * Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
 *  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class BackdropsAdapter extends BaseAdapter {

    private static final String LOG_TAG = "BackdropsAdapter";

    private Context mContext;
    private List<Backdrop> mBackdrops;
    private LayoutInflater inflater;
    private String mBackdropSize = IMAGE.SIZE.BACKDROP.w300;

    public BackdropsAdapter(Context c, List<Backdrop> backdrops) {
        mContext = c;
        mBackdrops = backdrops;
        inflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return mBackdrops.size();
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
            convertView = inflater.inflate(R.layout.backdrop, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.container_detail_backdrop);
        Log.d(LOG_TAG, "Backdrop path: " + mBackdrops.get(position).getFilePath());

        // set backdrop
        // TODO: Find an appropriate placeholder image for backdrop paths that are null
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + mBackdropSize +
                        mBackdrops.get(position).getFilePath())
                .error(android.R.drawable.screen_background_light)
                .into(imageView);

        return convertView;
    }
}