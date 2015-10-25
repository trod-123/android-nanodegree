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
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.JOBS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.MovieProjections.Details;
import com.thirdarm.popularmovies.model.Genre;
import com.thirdarm.popularmovies.model.Credits;
import com.thirdarm.popularmovies.model.Crew;
import com.thirdarm.popularmovies.utilities.ReleaseDates;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment consisting of specific movie details
 *
 * TODO: Change all getColor(int) methods to getColor(int, Theme) upon API 23 release
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "Movies/Detail";
    public static final String MOVIE_URI = "movieUri";

    private View mRootView;
    private Uri mUri;
    private Cursor mData;

    private Context mContext;
    private static final int DETAILS_LOADER_ID = 0;

    // TODO: Get a working definition of "writer" for setWriters()
    public String[] WRITERS = {JOBS.WRITING.AUTHOR, JOBS.WRITING.COWRITER,
            JOBS.WRITING.SCREENPLAY, JOBS.WRITING.STORY, JOBS.WRITING.WRITER};

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    // Prepare the loader
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get uri from bundle
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.MOVIE_URI);
        }

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return mRootView;
    }

//    public void fetchCredits() {
//        new FetchCreditsTask(getActivity(), mRootView, mData.getInt(Details.COL_MOVIE_TMDB_ID));
//    }

    /** Sets the banner with backdrop. White field if path is null */
    public void setBanner() {
        // TODO: Find an appropriate placeholder image for backdrop paths that are null
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 +
                        mData.getString(Details.COL_MOVIE_BACKDROP_PATH))
                .fit()
                .error(android.R.drawable.screen_background_light)
                .into((ImageView) mRootView.findViewById(R.id.banner));
    }

    /** Sets the poster. White field if path is null */
    public void setPoster() {
        // TODO: Find an appropriate placeholder image for poster paths that are null
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + IMAGE.SIZE.POSTER.w342 +
                        mData.getString(Details.COL_MOVIE_POSTER_PATH))
                .error(android.R.drawable.screen_background_light)
                .into((ImageView) mRootView.findViewById(R.id.container_detail_poster));
    }

    /** Sets the movie tagline if there is one. Otherwise, leave blank */
    public void setTagline() {
        String tagline = mData.getString(Details.COL_MOVIE_TAGLINE);
        if (tagline != null && tagline.length() != 0) {
            ((TextView) mRootView.findViewById(R.id.textview_detail_tagline))
                    .setText("\"" + tagline + "\"");
        }
    }

    /** Sets the title */
    public void setTitle() {
        String title = mData.getString(Details.COL_MOVIE_TITLE);
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_title);
        tv.setText(title);
    }

    /** Sets the overview. If no overview is found, text color is grey */
    public void setOverview() {
        String overview = mData.getString(Details.COL_MOVIE_OVERVIEW);
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_overview);
        if (overview != null && overview.length() != 0) {
            tv.setText(overview);
            tv.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    /** Sets the directors. If no director is found, text color is grey */
    public void setDirector() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_directors);
        String director = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<Credits>() {}.getType();
        Credits credits = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_CREDITS), type);
        if (credits != null) {
            for (Crew crew : credits.getCrew()) {
                if (crew.getJob().equals(JOBS.DIRECTING.DIRECTOR)) {
                    if (multiple) {
                        director += ", " + crew.getName();
                    } else {
                        director = crew.getName();
                        tv.setTextColor(mContext.getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(director);
    }

    /** Sets the writers. If no writer is found, text color is grey */
    public void setWriter() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_writers);
        String writer = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<Credits>() {}.getType();
        Credits credits = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_CREDITS), type);
        if (credits != null) {
            ArrayList<String> writers = new ArrayList<>();
            for (Crew crew : credits.getCrew()) {
                if (Arrays.asList(WRITERS).contains(crew.getJob())
                        && !writers.contains(crew.getName())) {
                    writers.add(crew.getName());
                    if (multiple) {
                        writer += ", " + crew.getName();
                    } else {
                        writer = crew.getName();
                        tv.setTextColor(mContext.getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(writer);
    }

    /** Sets the rating */
    public void setRating() {
        String votesTense = getString(R.string.detail_votes);
        if (mData.getInt(Details.COL_MOVIE_VOTE_COUNT) != 1) {
            votesTense += "s";
        }
        ((TextView) mRootView.findViewById(R.id.textview_detail_rating)).setText(
//                getString(R.string.detail_ratings)
//                        + new DecimalFormat("#.##").format(mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE))
                        "("
                        + mData.getInt(Details.COL_MOVIE_VOTE_COUNT)
//                        + " "
//                        + votesTense.toLowerCase()
                        + ")"
        );
        RatingBar bar = (RatingBar) mRootView.findViewById(R.id.ratingbar_detail);
        bar.setRating((float) mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE) / 2);
    }

    /** Sets the release info */
    public void setReleaseInfo() {
        Log.d(LOG_TAG, "" + mData.getInt(Details.COL_MOVIE_RUNTIME));
        String release_duration = ReleaseDates.convertDateFormat(mData.getString(Details.COL_MOVIE_RELEASE_DATE)) + " | " +
                mData.getInt(Details.COL_MOVIE_RUNTIME) + " min";
        ((TextView) mRootView.findViewById(R.id.textview_detail_release_duration_mpaaRating))
                .setText(release_duration);
    }

    /** Sets the genre. If no genre is found, text color is grey */
    public void setGenre() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_genres);
        String genres = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<ArrayList<Genre>>() {}.getType();
        ArrayList<Genre> movieGenres = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_GENRES), type);
        for (Genre genre : movieGenres) {
            if (multiple) {
                genres += ", " + genre.getName();
            } else {
                genres = genre.getName();
                tv.setTextColor(mContext.getResources().getColor(R.color.white));
                multiple = true;
            }
        }
       tv.setText(genres);
    }

    /** Sets the TMDB footer */
    public void setFooter() {
        TextView url_text = (TextView) mRootView.findViewById(R.id.tmdb_link);

        // use LinkMovementMethod to create hyperlink redirecting to TMDB movie page
        url_text.setClickable(true);
        url_text.setMovementMethod(LinkMovementMethod.getInstance());
        String url = URL.PUBLIC_BASE + URL.MOVIE + mData.getInt(Details.COL_MOVIE_TMDB_ID);
        String html = "<a href='" + url + "'>" + getString(R.string.footer_tmdb) + "</a>";
        url_text.setText(Html.fromHtml(html));
    }

    /*
        Loader methods
     */

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        // Make sure to first check, before loading any intent extras or anything from intents,
//        //  that the intent is not null
//        // UPDATE: Intent is no longer necessary since we have bounded the movie uri data to the
//        //  fragment upon its instantiation.
//        Intent intent = getActivity().getIntent();
//        if (intent == null) {
//            return null;
//        }

        if (mUri != null) {
            return new CursorLoader(mContext, mUri, Details.PROJECTION,
                    null, null, null);
        }
        return null;
    }


    // No cursor view is involved with this fragment, so just extract the data from the cursor
    //  and pair them with the appropriate text views
    @Override public void onLoadFinished(Loader loader, Cursor data) {
        // Check if cursor is empty
        if (!data.moveToFirst()) return;

        mData = data;
        //fetchCredits();

        // set title of movie as title of activity
        getActivity().setTitle(mData.getString(Details.COL_MOVIE_TITLE));

        // prepare the UI
        //new FetchCreditsTask().execute(); // because credits info was not sent through intent
        setBanner();
        setPoster();
        setTagline();
        setTitle();
        setOverview();
        setRating();
        setReleaseInfo();
        setGenre();
        setFooter();
        setDirector();
        setWriter();
    }

    @Override public void onLoaderReset(Loader loader) {
    }
}
