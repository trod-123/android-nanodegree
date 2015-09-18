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
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.function.ReleaseDates;
import com.thirdarm.popularmovies.model.Genre;
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

            // set poster image
            Picasso.with(mContext)
                    .load(URL.IMAGE_BASE + IMAGE.SIZE.POSTER.w342 + movie.getPosterPath())
                    .error(R.drawable.sample_0)
                    .into((ImageView) rootView.findViewById(R.id.poster));

            // set movie tagline if there is one
            if (movie.getTagline().length() != 0) {
                ((TextView) rootView.findViewById(R.id.banner_title))
                        .setText("\"" + movie.getTagline() + "\"");
            }

            // set rating
            ((TextView) rootView.findViewById(R.id.rating))
                    .setText(
                            getString(R.string.detail_ratings)
                                    +": "
                                    + new DecimalFormat("#.##").format(movie.getVoteAverage())
                                    + " ("
                                    + movie.getVoteCount()
                                    + " "
                                    + getString(R.string.detail_reviews).toLowerCase()
                                    + ")");

            // set release
            ((TextView) rootView.findViewById(R.id.release))
                    .setText(ReleaseDates.setReleaseDate(mContext, movie));

            // set genres
            String genres = "Genres:\n";
            for (int i = 0; i < movie.getGenres().size(); i++) {
                Genre genre = movie.getGenres().get(i);
                if (i != movie.getGenres().size() - 1) {
                    genres += genre.getName() + ", ";
                } else {
                    genres += genre.getName();
                }
            }
            ((TextView) rootView.findViewById(R.id.genres))
                    .setText(genres);

            // set overview
            if (movie.getOverview().length() != 0) {
                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(movie.getOverview());
            } else {
                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(getString(R.string.error_overview));
            }

            // set copyright footer
            TextView url_text = (TextView) rootView.findViewById(R.id.tmdb_link);
            url_text.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    String url = URL.PUBLIC_BASE + URL.MOVIE + movie.getId();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }
}
