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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.Youtube;

import java.util.List;

/**
 * ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
 *  to be displayed in the grid.
 * Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
 *  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class TrailersAdapter extends BaseAdapter {

    private static final String LOG_TAG = "TrailersAdapter";

    private Context mContext;
    private String mTitle;
    private List<Youtube> mThumbnails;
    private LayoutInflater inflater;
    private String mThumbnailSize = "/0.jpg";

    public TrailersAdapter(Context c, String title, List<Youtube> videos) {
        mContext = c;
        mTitle = title;
        mThumbnails = videos;
        inflater = LayoutInflater.from(c);
    }

    /** Shares the review with friends */
    private void shareTrailer(int position) {
        String url = URL.YOUTUBE_BASE + mThumbnails.get(position).getSource();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mContext.getString(R.string.format_share_trailer, mTitle, url)
        );
        mContext.startActivity(shareIntent);
    }

    public int getCount() {
        return mThumbnails.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.trailer, null);
        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.container_detail_trailer);
        final LinearLayout buttonLayout = (LinearLayout) convertView.findViewById(R.id.container_detail_trailer_buttons);
        final ImageView playButtonView = (ImageView) convertView.findViewById(R.id.imageview_video_play_button);
        final ImageView shareButtonView = (ImageView) convertView.findViewById(R.id.imageview_video_share_button);

        // set backdrop
        // TODO: Find an appropriate placeholder image for backdrop paths that are null
        Picasso.with(mContext)
                .load(URL.YOUTUBE_THUMBNAIL_BASE +
                        mThumbnails.get(position).getSource() + mThumbnailSize)
                .error(android.R.drawable.screen_background_light)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);

                        // Set the dimensions of the play button overlay and bring it in front of
                        //  the thumbnail
                        playButtonView.setAdjustViewBounds(true);
                        playButtonView.setMaxHeight(bitmap.getHeight());
                        playButtonView.setMaxWidth(bitmap.getWidth() / 2);
                        LinearLayout.LayoutParams lpPb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lpPb.gravity = Gravity.CENTER;
                        playButtonView.setLayoutParams(lpPb);

                        shareButtonView.setAdjustViewBounds(true);
                        shareButtonView.setMaxHeight(bitmap.getHeight());
                        shareButtonView.setMaxWidth(bitmap.getWidth() / 2);
                        LinearLayout.LayoutParams lpSb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lpSb.gravity = Gravity.CENTER;
                        shareButtonView.setLayoutParams(lpSb);

                        FrameLayout.LayoutParams lpBl = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
                        lpBl.gravity = Gravity.CENTER_HORIZONTAL;
                        buttonLayout.setLayoutParams(lpBl);
                        buttonLayout.bringToFront();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        // Set button click listeners
        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mThumbnails.get(position).getSource();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.YOUTUBE_BASE + path));
                mContext.startActivity(intent);
            }
        });

        shareButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set share intent
                shareTrailer(position);
            }
        });

        return convertView;
    }
}