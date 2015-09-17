package com.thirdarm.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public static final String LOG_TAG = "Movies/Detail";

    Context mContext;
    View rootView;
    Intent intent;
    MovieDB movie;

    public MovieDetailsFragment() {
    }

    // A fragment's onCreate() method is called before its onCreateView() method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        intent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // make sure to first check, before loading any intent extras or anything from intents,
        //  that the intent is not null and that intent has extra text matching the
        //  string ID that was loaded in the activity that sent the intent
        if (intent != null && intent.hasExtra("myData")) {
            movie = intent.getParcelableExtra("myData");

            // set title of movie as title of activity
            getActivity().setTitle(movie.getTitle());

            // set banner image
            // TODO: What to do if the backdrop_path == null?
            Picasso.with(mContext)
                    .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 + movie.getBackdropPath())
                    .fit()
                    .error(R.drawable.piq_76054_400x400)
                    .into((ImageView) rootView.findViewById(R.id.banner));

            // set movie tagline
            Log.d(LOG_TAG, "Tagline for movie " + movie.getId() + ": " + movie.getTagline());
            if (movie.getTagline().length() != 0) {
                ((TextView)rootView.findViewById(R.id.banner_title)).setText("\"" + movie.getTagline() + "\"");
            }

            // set overview
            if (movie.getOverview().length() != 0) {
                ((TextView) rootView.findViewById(R.id.overview)).setText(movie.getOverview());
            } else {
                ((TextView) rootView.findViewById(R.id.overview)).setText("Overview not available");
            }

            // set rating
            ((TextView) rootView.findViewById(R.id.rating)).setText(new DecimalFormat("#.##").format(movie.getVoteAverage()) +
                    " (" + movie.getVoteCount() + " reviews)");

            // set release
            ((TextView) rootView.findViewById(R.id.release)).setText(movie.getReleaseDate());

        }

        return rootView;
    }
}
