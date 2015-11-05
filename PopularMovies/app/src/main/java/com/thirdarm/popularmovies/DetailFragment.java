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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.thirdarm.popularmovies.model.Backdrop;
import com.thirdarm.popularmovies.model.Cast;
import com.thirdarm.popularmovies.model.Genre;
import com.thirdarm.popularmovies.model.Credits;
import com.thirdarm.popularmovies.model.Crew;
import com.thirdarm.popularmovies.model.Images;
import com.thirdarm.popularmovies.model.Reviews;
import com.thirdarm.popularmovies.model.Trailers;
import com.thirdarm.popularmovies.model.Youtube;
import com.thirdarm.popularmovies.sync.MoviesSyncAdapter;
import com.thirdarm.popularmovies.utilities.NonScrollListView;
import com.thirdarm.popularmovies.utilities.ReleaseDates;

import org.lucasr.twowayview.TwoWayView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment consisting of specific movie details
 *
 * TODO: Change all getColor(int) methods to getColor(int, Theme) upon API 23 release
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "Movies/Detail";
    public static final String MOVIE_URI = "movieUri";

    private Context mContext;

    // ui components
    private View mRootView;
    private BackdropsAdapter mBackdropsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private Trailers mTrailers;
    private List<Reviews> mReviews;

    // data components
    private static final int DETAILS_LOADER_ID = 0;
    private static final int ACTORS_MAX = 5;
    private Uri mUri;
    private Cursor mData;
    private boolean mLoaded;


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

    /*
     * For communicating with the SyncAdapter whether movie data has finished loading into the
     *  local database.
     */

    @Override public void onResume() {
        super.onResume();
        mContext.registerReceiver(syncFinishedReceiver, new IntentFilter(MoviesSyncAdapter.MOVIE_DETAILS_ADDED));
    }

    @Override public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(syncFinishedReceiver);
    }

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (mData != null &&
                    intent.getIntExtra(MoviesSyncAdapter.INTENT_EXTRA_MOVIE_ID, -1) == mData.getInt(Details.COL_MOVIE_TMDB_ID) &&
                    !mData.isNull(Details.COL_MOVIE_CREDITS)) {
                setDetailedFields();
            } else if (mData == null) {
                Log.d(LOG_TAG, "There was a problem with receiving the broadcasted intent");
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get uri from bundle
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.MOVIE_URI);
        }

        return mRootView;
    }

//    // Deprecated, but stored for reference
//    private void fetchMovieDetails(int movieId) {
//        new FetchMovieDetailsTask(getActivity(), PostersFragment.mTmdb, movieId, mRootView);
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
        TextView taglineTextView = (TextView) mRootView.findViewById(R.id.textview_detail_tagline);
        if (tagline != null && tagline.length() != 0) {
            String taglineString = getString(R.string.format_detail_tagline, tagline);
            taglineTextView.setText(taglineString);
        } else {
            // if no tagline, hide textview placeholder
            taglineTextView.setHeight(0);
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
            tv.setTextColor(getResources().getColor(R.color.white));
        }
    }

    /** Sets the main actors, in order of prominence. If no actor is found, text color is grey */
    public void setActors() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_actors);
        String actors = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<Credits>() {}.getType();
        Credits credits = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_CREDITS), type);
        if (credits != null) {
            ArrayList<String> actorsList = new ArrayList<>();
            for (Cast cast : credits.getCast()) {
                if (actorsList.size() < ACTORS_MAX) {
                    actorsList.add(cast.getName());
                    if (multiple) {
                        actors += ", " + getString(R.string.format_detail_actors, cast.getName(), cast.getCharacter());
                    } else {
                        actors = getString(R.string.format_detail_actors, cast.getName(), cast.getCharacter());
                        tv.setTextColor(getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(actors);
    }

    /** Sets the directors. If no director is found, text color is grey */
    public void setDirectors() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_directors);
        String director = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<Credits>() {}.getType();
        Credits credits = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_CREDITS), type);
        if (credits != null) {
            for (Crew crew : credits.getCrew()) {
                if (crew.getJob().equals(JOBS.DIRECTING.DIRECTOR)) {
                    if (multiple) {
                        director += ", " + getString(R.string.format_detail_directors, crew.getName(), crew.getJob());
                    } else {
                        director = getString(R.string.format_detail_directors, crew.getName(), crew.getJob());
                        tv.setTextColor(getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(director);
    }

    /** Sets the writers. If no writer is found, text color is grey */
    public void setWriters() {
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
                        writer += ", " + getString(R.string.format_detail_writers, crew.getName(), crew.getJob());
                    } else {
                        writer = getString(R.string.format_detail_writers, crew.getName(), crew.getJob());
                        tv.setTextColor(getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(writer);
    }

    /** Sets the rating */
    public void setRating() {
        TextView ratingTextView = (TextView) mRootView.findViewById(R.id.textview_detail_rating);
        ratingTextView.setText(getString(R.string.format_detail_rating, mData.getInt(Details.COL_MOVIE_VOTE_COUNT)));
        RatingBar bar = (RatingBar) mRootView.findViewById(R.id.ratingbar_detail);
        bar.setRating((float) mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE) / 2);
    }

    /** Sets the release info and runtime */
    public void setReleaseInfoRuntime() {
        int runtime = mData.getInt(Details.COL_MOVIE_RUNTIME);
        String release_date = ReleaseDates.convertDateFormat(mData.getString(Details.COL_MOVIE_RELEASE_DATE));
        String release_duration;
        if (runtime == 0) {
            // if unknown runtime, just include release date
            release_duration = release_date;
        } else {
            release_duration = getString(R.string.format_detail_release_runtime, release_date, runtime);
        }
        TextView releaseInfoRuntimeTextView = (TextView) mRootView.findViewById(R.id.textview_detail_release_duration_mpaaRating);
        releaseInfoRuntimeTextView.setText(release_duration);
        releaseInfoRuntimeTextView.setTextColor(getResources().getColor(R.color.white));
    }

    /** Sets the genres. If no genre is found, text color is grey */
    public void setGenres() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_genres);
        String genres = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<ArrayList<Genre>>() {}.getType();
        ArrayList<Genre> movieGenres = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_GENRES), type);
        if (movieGenres != null) {
            for (Genre genre : movieGenres) {
                if (multiple) {
                    genres += ", " + getString(R.string.format_detail_genres, genre.getName());
                } else {
                    genres = getString(R.string.format_detail_genres, genre.getName());
                    tv.setTextColor(getResources().getColor(R.color.white));
                    multiple = true;
                }
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
        String html = getString(R.string.format_detail_footer, url);
        url_text.setText(Html.fromHtml(html));
    }

    /** Sets the backdrops **/
    public void setBackdrops() {
        // Create a BackdropsAdapter if haven't already
        if (mBackdropsAdapter == null) {
            Log.d(LOG_TAG, "BACKDROPS ADAPTER IS NULL");
            Type type = new TypeToken<Images>() {}.getType();
            Images images = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_IMAGES), type);
            if (images != null) {
                List<Backdrop> backdrops = images.getBackdrops();
                mBackdropsAdapter = new BackdropsAdapter(mContext, backdrops);
                // Get rid of the null backdrops text
                TextView backdropsStatusTextView = (TextView) mRootView.findViewById(R.id.textview_detail_status_backdrops);
                if (backdrops.size() != 0) {
                    backdropsStatusTextView.setText(null);
                    backdropsStatusTextView.setHeight(0);
                }
            }
        } else {
            Log.d(LOG_TAG, "It is not null");
        }
        TwoWayView backdropView = (TwoWayView) mRootView.findViewById(R.id.listview_detail_backdrops);
        backdropView.setAdapter(mBackdropsAdapter);
    }

    /** Sets the trailers **/
    public void setTrailers() {
        // Create a TrailersAdapter if haven't already
        if (mTrailersAdapter == null) {
            Type type = new TypeToken<Trailers>() {}.getType();
            mTrailers = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_TRAILERS), type);
            if (mTrailers != null) {
                List<Youtube> videos = mTrailers.getYoutube();
                mTrailersAdapter = new TrailersAdapter(mContext, videos);
                // Get rid of the null trailers text
                TextView trailersStatusTextView = (TextView) mRootView.findViewById(R.id.textview_detail_status_trailers);
                if (videos.size() != 0) {
                    trailersStatusTextView.setText(null);
                    trailersStatusTextView.setHeight(0);
                }
            }
        }
        TwoWayView trailerView = (TwoWayView) mRootView.findViewById(R.id.listview_detail_trailers);
        trailerView.setAdapter(mTrailersAdapter);

        // TODO: The buttons CAN be configured through the pertaining Custom Adapter class! WOO WOO WOO.
        // TODO: FIX TRAILERS THAT ARE NOT LOADING UP!!!! WHAT THE HELL.
    }

    /** Sets the ratings for reviews **/
    public void setDetailedRating() {
        String votesTense = getString(R.string.detail_votes_tense);
        if (mData.getInt(Details.COL_MOVIE_VOTE_COUNT) != 1) {
            votesTense += "s";
        }
        ((TextView) mRootView.findViewById(R.id.textview_detail_rating_large)).setText(
                new DecimalFormat("#0.0").format(mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE))
        );
        ((TextView) mRootView.findViewById(R.id.textview_detail_rating_count)).setText(
                getString(R.string.format_detail_reviews_rating,
                        mData.getInt(Details.COL_MOVIE_VOTE_COUNT),
                        votesTense.toLowerCase()
                )
        );
        RatingBar bar = (RatingBar) mRootView.findViewById(R.id.ratingbar_detail_large);
        bar.setRating((float) mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE) / 2);
    }

    /** Sets the reviews **/
    public void setReviews() {
        // Create a ReviewsAdapter if haven't already
        if (mReviewsAdapter == null) {
            Type type = new TypeToken<List<Reviews>>() {}.getType();
            mReviews = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_REVIEWS), type);
            if (mReviews != null) {
                mReviewsAdapter = new ReviewsAdapter(mContext, mReviews);
                // Get rid of the null trailers text
                TextView reviewsStatusTextView = (TextView) mRootView.findViewById(R.id.textview_detail_status_reviews);
                if (mReviews.size() != 0) {
                    String reviewsTense = getString(R.string.detail_reviews_tense);
                    if (mReviews.size() != 1) {
                        reviewsTense += "s";
                    }
                    reviewsStatusTextView.setText(
                            getString(R.string.format_detail_reviews, mReviews.size(), reviewsTense)
                    );
                    reviewsStatusTextView.setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
        NonScrollListView reviewsView = (NonScrollListView) mRootView.findViewById(R.id.listview_detail_reviews);
        reviewsView.setAdapter(mReviewsAdapter);
    }

    /** Helper method for setting all basic ui fields **/
    public void setInitialFields() {
        if (!mLoaded) {
            setBanner();
            setPoster();
            setTitle();
            setOverview();
            setRating();
            setDetailedRating();
        }
    }

    /** Helpe rmethod for setting all detailed ui fields **/
    public void setDetailedFields() {
        if (!mLoaded) {
            setTagline();
            setReleaseInfoRuntime();
            setGenres();
            setFooter();
            setActors();
            setDirectors();
            setWriters();
            setBackdrops();
            setTrailers();
            setReviews();
            mLoaded = true;
            Log.d(LOG_TAG, "setDetailedFields()");
        }
    }

    /*
        Loader methods
     */

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            mLoaded = false;
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

        // set title of movie as title of activity
        getActivity().setTitle(mData.getString(Details.COL_MOVIE_TITLE));

        // prepare the UI
        setInitialFields();
        // if the data is available, prepare the detailed elements
        if (mData.getString(Details.COL_MOVIE_TAGLINE) != null) {
            setDetailedFields();
        }
    }

    @Override public void onLoaderReset(Loader loader) {
    }
}
