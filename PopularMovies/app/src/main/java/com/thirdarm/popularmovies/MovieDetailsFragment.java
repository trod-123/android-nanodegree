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
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.JOBS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.function.Network;
import com.thirdarm.popularmovies.function.ReleaseDates;
import com.thirdarm.popularmovies.model.Credits;
import com.thirdarm.popularmovies.model.Crew;
import com.thirdarm.popularmovies.model.Genre;
import com.thirdarm.popularmovies.model.MovieDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment consisting of specific movie details
 */
public class MovieDetailsFragment extends Fragment {

    public static final String LOG_TAG = "Movies/Detail";

    public Context mContext;
    public View mRootView;
    public Intent mIntent;
    public MovieDB mMovie;

    // TODO: Get a working definition of "writer" for setWriters()
    public String[] WRITERS = {JOBS.WRITING.AUTHOR, JOBS.WRITING.COWRITER,
            JOBS.WRITING.SCREENPLAY, JOBS.WRITING.STORY, JOBS.WRITING.WRITER};

    public MovieDetailsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        // get the intent from the posters activity
        mIntent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Make sure to first check, before loading any intent extras or anything from intents,
        //  that the intent is not null and that the intent contains the key matching the
        //  string ID that was loaded in the intent sent by the previous activity
        if (mIntent != null && mIntent.hasExtra(MoviePostersFragment.INTENT_DATA)) {
            mMovie = mIntent.getParcelableExtra(MoviePostersFragment.INTENT_DATA);

            // set title of movie as title of activity
            getActivity().setTitle(mMovie.getTitle());

            // prepare the UI
            setBanner();
            setPoster();
            setTagline();
            setOverview();
            new FetchCreditsTask().execute();
            setRating();
            setReleaseInfo();
            setGenre();
            setFooter();
        }

        return mRootView;
    }

    /** Sets the banner with backdrop */
    public void setBanner() {
        // TODO: What to do if the backdrop_path == null?
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 + mMovie.getBackdropPath())
                .fit()
                .error(R.drawable.piq_76054_400x400)
                .into((ImageView) mRootView.findViewById(R.id.banner));
    }

    /** Sets the poster */
    public void setPoster() {
        // TODO: What to do if the poster_path == null?
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + IMAGE.SIZE.POSTER.w342 + mMovie.getPosterPath())
                .error(R.drawable.sample_0)
                .into((ImageView) mRootView.findViewById(R.id.poster));
    }

    /** Sets the movie tagline if there is one. Otherwise, leave blank */
    public void setTagline() {
        if (mMovie.getTagline().length() != 0) {
            ((TextView) mRootView.findViewById(R.id.banner_title))
                    .setText("\"" + mMovie.getTagline() + "\"");
        }
    }

    /** Sets the overview */
    public void setOverview() {
        if (mMovie.getOverview().length() != 0) {
            ((TextView) mRootView.findViewById(R.id.overview))
                    .setText(mMovie.getOverview());
        } else {
            ((TextView) mRootView.findViewById(R.id.overview))
                    .setText(getString(R.string.error_info_null));
        }
    }

    /** Sets the directors */
    public void setDirector(Credits credits) {
        String director = getString(R.string.error_info_null);
        boolean multiple = false;
        for (Crew crew : credits.getCrew()) {
            if (crew.getJob().equals(JOBS.DIRECTING.DIRECTOR)) {
                if (multiple) {
                    director += ", " + crew.getName();
                } else {
                    director = crew.getName();
                    multiple = true;
                }
            }
        }
        ((TextView) mRootView.findViewById(R.id.director))
                .setText(
                        getString(R.string.detail_directors)
                                + ": "
                                + director
                );
    }

    /** Sets the writers */
    public void setWriter(Credits credits) {
        String writer = getString(R.string.error_info_null);
        boolean multiple = false;
        ArrayList<String> writers = new ArrayList<>();
        for (Crew crew : credits.getCrew()) {
            if (Arrays.asList(WRITERS).contains(crew.getJob())
                    && !writers.contains(crew.getName())) {
                writers.add(crew.getName());
                if (multiple) {
                    writer += ", " + crew.getName();
                } else {
                    writer = crew.getName();
                    multiple = true;
                }
            }
        }
        ((TextView) mRootView.findViewById(R.id.writer))
                .setText(
                        getString(R.string.detail_writers)
                                + ": "
                                + writer
                );
    }

    /** Sets the rating */
    public void setRating() {
        String votesTense = getString(R.string.detail_votes);
        if (mMovie.getVoteCount() != 1) {
            votesTense += "s";
        }
        ((TextView) mRootView.findViewById(R.id.rating))
                .setText(
                        getString(R.string.detail_ratings)
                                + ": "
                                + new DecimalFormat("#.##").format(mMovie.getVoteAverage())
                                + " ("
                                + mMovie.getVoteCount()
                                + " "
                                + votesTense.toLowerCase()
                                + ")"
                );
    }

    /** Sets the release info */
    public void setReleaseInfo() {
        ((TextView) mRootView.findViewById(R.id.release))
                .setText(ReleaseDates.setReleaseDate(mContext, mMovie));
    }

    /** Sets the genre */
    public void setGenre() {
        String genres = getString(R.string.detail_genres) + ":\n";
        for (int i = 0; i < mMovie.getGenres().size(); i++) {
            Genre genre = mMovie.getGenres().get(i);
            if (i != mMovie.getGenres().size() - 1) {
                genres += genre.getName() + ", ";
            } else {
                genres += genre.getName();
            }
        }
        ((TextView) mRootView.findViewById(R.id.genres))
                .setText(genres);
    }

    /** Sets the TMDB footer */
    public void setFooter() {
        TextView url_text = (TextView) mRootView.findViewById(R.id.tmdb_link);

        // use LinkMovementMethod to create hyperlink redirecting to TMDB movie page
        url_text.setClickable(true);
        url_text.setMovementMethod(LinkMovementMethod.getInstance());
        String url = URL.PUBLIC_BASE + URL.MOVIE + mMovie.getId();
        String html = "<a href='" + url + "'>" + getString(R.string.footer_tmdb) + "</a>";
        url_text.setText(Html.fromHtml(html));
    }

    /**
     * Collects and parses JSON data from the TMDB servers via API calls and
     * fills the main UI with posters in a grid view.
     */
    public class FetchCreditsTask extends AsyncTask<Void, Void, Credits> {
        @Override protected void onPreExecute() {
            // Check for internet connection
            if (!Network.isNetworkAvailable(mContext)) {
                // TODO: Implement a cleaner method, without code repeats
                ((TextView) mRootView.findViewById(R.id.director))
                        .setText(
                                getString(R.string.detail_directors)
                                        + ": "
                                        + getString(R.string.error_no_internet)
                        );
                ((TextView) mRootView.findViewById(R.id.writer))
                        .setText(
                                getString(R.string.detail_writers)
                                        + ": "
                                        + getString(R.string.error_no_internet)
                        );
                cancel(true);
            }

            // Stop the AsyncTask if either condition above is met
            if (isCancelled()) {
                return;
            }
        }

        @Override protected Credits doInBackground(Void... params) {
            return MoviePostersFragment.mTmdb.getMovieCredits(mMovie.getId());
        }

        @Override protected void onPostExecute(Credits result) {
            // Make sure result is not null
            if (result != null) {
                setDirector(result);
                setWriter(result);
            }
        }
    }
}
