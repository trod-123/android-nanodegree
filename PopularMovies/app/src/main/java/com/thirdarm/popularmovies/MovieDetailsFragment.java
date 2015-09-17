/*
 * Copyright (C) 2015 Teddy Rodriguez (TROD)
 *   email: cia.123trod@gmail.com
 *   github: TROD-123
 *
 * For Udacity's Android Developer Nanodegree
 * P1-2: Popular Movies
 *
 * Currently for educational purposes only.
 */

package com.thirdarm.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.MovieDB;

import java.text.DecimalFormat;

/**
 * Fragment consisting of specific movie details
 */
public class MovieDetailsFragment extends Fragment {

    public static final String LOG_TAG = "Movies/Detail";

    public Context mContext;
    public View rootView;
    public Intent intent;
    public MovieDB movie;


    public MovieDetailsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        // get the intent from the posters activity
        intent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Make sure to first check, before loading any intent extras or anything from intents,
        //  that the intent is not null and that the intent contains the key matching the
        //  string ID that was loaded in the intent sent by the previous activity
        if (intent != null && intent.hasExtra(MoviePostersFragment.INTENT_DATA)) {
            movie = intent.getParcelableExtra(MoviePostersFragment.INTENT_DATA);

            // set title of movie as title of activity
            getActivity().setTitle(movie.getTitle());

            // set banner image
            // TODO: What to do if the backdrop_path == null?
            Picasso.with(mContext)
                    .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 + movie.getBackdropPath())
                    .fit()
                    .error(R.drawable.piq_76054_400x400)
                    .into((ImageView) rootView.findViewById(R.id.banner));

            Picasso.with(mContext)
                    .load(URL.IMAGE_BASE + IMAGE.SIZE.POSTER.w342 + movie.getPosterPath())
                    .error(R.drawable.piq_76054_400x400)
                    .into((ImageView) rootView.findViewById(R.id.poster));

            // set movie tagline if there is one
            if (movie.getTagline().length() != 0) {
                ((TextView) rootView.findViewById(R.id.banner_title))
                        .setText("\"" + movie.getTagline() + "\"");
            }

            // set overview
            if (movie.getOverview().length() != 0) {
                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(movie.getOverview());
            } else {
                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(getString(R.string.error_overview));
            }

            // set rating
            ((TextView) rootView.findViewById(R.id.rating))
                    .setText(
                            "Rating: "
                                    + new DecimalFormat("#.##").format(movie.getVoteAverage())
                                    + " ("
                                    + movie.getVoteCount()
                                    + " "
                                    + getString(R.string.detail_reviews).toLowerCase()
                                    + ")");

            // set release
            ((TextView) rootView.findViewById(R.id.release))
                    .setText(
                            "Released "
                                    + movie.getReleaseDate());
        }

        TextView url_text = (TextView) rootView.findViewById(R.id.tmdb_link);
        url_text.setMovementMethod(LinkMovementMethod.getInstance());

        return rootView;
    }
}
