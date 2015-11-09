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
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.MovieProjections;
import com.thirdarm.popularmovies.model.Backdrop;
import com.thirdarm.popularmovies.model.Reviews;

import java.util.List;

/**
 * Adapter for holding reviews
 */
public class ReviewsAdapter extends BaseAdapter {

    private static final String LOG_TAG = "ReviewsAdapter";

    private Context mContext;
    private String mTitle;
    private List<Reviews> mReviews;
    private LayoutInflater inflater;

    public ReviewsAdapter(Context c, String title, List<Reviews> reviews) {
        mContext = c;
        mTitle = title;
        mReviews = reviews;
        inflater = LayoutInflater.from(c);
    }

    /** Shares the review with friends */
    private void shareReview(int position) {
        String url = mReviews.get(position).getUrl();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mContext.getString(R.string.format_share_review, mTitle,
                        mReviews.get(position).getAuthor(), url)
        );
        mContext.startActivity(shareIntent);
    }

    public int getCount() {
        return mReviews.size();
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
            convertView = inflater.inflate(R.layout.review, null);
        }

        TextView authorTextView = (TextView) convertView.findViewById(R.id.textview_detail_review_author);
        TextView contentTextView = (TextView) convertView.findViewById(R.id.textview_detail_review_content);

        // set review
        authorTextView.setText(mReviews.get(position).getAuthor());
        contentTextView.setText(mReviews.get(position).getContent());

        // Handle button click events
        ImageButton shareButton = (ImageButton) convertView.findViewById(R.id.imagebutton_detail_reviews_share);
        ImageButton urlButton = (ImageButton) convertView.findViewById(R.id.imagebutton_detail_reviews_url);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReview(position);
            }
        });

        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Review url intent here
                    String path = mReviews.get(position).getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                    mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
