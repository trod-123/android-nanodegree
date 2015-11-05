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
import com.thirdarm.popularmovies.model.Backdrop;
import com.thirdarm.popularmovies.model.Reviews;

import java.util.List;

/**
 * Created by TROD on 20151102.
 */
public class ReviewsAdapter extends BaseAdapter {

    private static final String LOG_TAG = "ReviewsAdapter";

    private Context mContext;
    private List<Reviews> mReviews;
    private LayoutInflater inflater;

    public ReviewsAdapter(Context c, List<Reviews> reviews) {
        mContext = c;
        mReviews = reviews;
        inflater = LayoutInflater.from(c);
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
        ImageButton reportButton = (ImageButton) convertView.findViewById(R.id.imagebutton_detail_reviews_report);
        ImageButton urlButton = (ImageButton) convertView.findViewById(R.id.imagebutton_detail_reviews_url);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Share intent here
                Toast.makeText(mContext, "Share button clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Report url intent here
                Toast.makeText(mContext, "Report button clicked.", Toast.LENGTH_SHORT).show();
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
