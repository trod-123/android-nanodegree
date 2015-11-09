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
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.JOBS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.data.*;
import com.thirdarm.popularmovies.data.MovieProjections.Details;
import com.thirdarm.popularmovies.data.MovieProvider;
import com.thirdarm.popularmovies.model.Backdrop;
import com.thirdarm.popularmovies.model.Cast;
import com.thirdarm.popularmovies.model.Country;
import com.thirdarm.popularmovies.model.Genre;
import com.thirdarm.popularmovies.model.Credits;
import com.thirdarm.popularmovies.model.Crew;
import com.thirdarm.popularmovies.model.Images;
import com.thirdarm.popularmovies.model.Releases;
import com.thirdarm.popularmovies.model.Reviews;
import com.thirdarm.popularmovies.model.Trailers;
import com.thirdarm.popularmovies.model.Youtube;
import com.thirdarm.popularmovies.sync.MoviesSyncAdapter;
import com.thirdarm.popularmovies.utilities.NonScrollListView;
import com.thirdarm.popularmovies.utilities.ReleaseDates;
import com.thirdarm.popularmovies.utilities.SwapViewContainers;

import org.lucasr.twowayview.TwoWayView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    private ShareActionProvider mShareActionProvider;
    private Menu mOptionsMenu;

    // ui components
    private View mRootView;
    private RelativeLayout mProgressContainer;
    private BackdropsAdapter mBackdropsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private Trailers mTrailers;
    private List<Reviews> mReviews;
    private String mPosterSize;
    private int mFavorited;

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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // make sure this is called first
        inflater.inflate(R.menu.menu_fragment_details, menu);

        // Inflate the menu; this adds items to the action bar
        mOptionsMenu = menu;

        // Retrieve the share menu item
        final MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        // Use new thread to loop until the provider has been set
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mShareActionProvider == null) {
                    try {
                        mShareActionProvider =
                                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_favorite:
                favorite();
                return true;

            case R.id.action_share:
                return true;

            case R.id.action_launch_tmdb_page:
                String url = URL.PUBLIC_BASE + URL.MOVIE + mData.getInt(Details.COL_MOVIE_TMDB_ID);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(url));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mProgressContainer = (RelativeLayout) mRootView.findViewById(R.id.container_detail_progress);
        swapViewContainers();

        mPosterSize = getString(R.string.poster_width);

        // Get uri from bundle
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.MOVIE_URI);
        }
        return mRootView;
    }


    /*
        App bar action methods
     */

    /**
     * Loads the favorite icon
     *
     * @param swap True if user affected favorites field.
     *             For dealing with non-instantaneous updating of db when swapping favorites values
     */
    private void loadFavorite(boolean swap) {
        // preliminary check if movie is favorited
        int swapTruth = swap ? 0 : 1;
        boolean inFavorites = mData.getInt(Details.COL_MOVIE_FAVORITE) == swapTruth;
        // load proper icon
        final int iconRes;
        if (inFavorites) {
            iconRes = R.drawable.ic_favorite_white_24dp;
        } else {
            iconRes = R.drawable.ic_favorite_border_white_24dp;
        }
        // wait until onCreateOptionsMenu() has been called and mOptionMenu initialized or else app
        //  will crash upon loading detail fragment
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mOptionsMenu == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        mOptionsMenu.findItem(R.id.action_favorite).setIcon(iconRes);
                    }
                });
            }
        }).start();
    }

    /**
     * Favorites the movie if unfavorited, vice versa
     *
     * @return the long value of the row where the movie was inserted
     */
    private long favorite() {
        int movieId = mData.getInt(Details.COL_MOVIE_TMDB_ID);
        // preliminary check if movie is favorited
        boolean inFavorites = mData.getInt(Details.COL_MOVIE_FAVORITE) == 1;
        int swapFavorites = inFavorites ? 0 : 1;

        // prepare content resolver
        ContentResolver cr = mContext.getContentResolver();
        long locationId = -1;

        Cursor cursor = cr.query(
                com.thirdarm.popularmovies.data.MovieProvider.Movies.CONTENT_URI,
                new String[]{MovieColumns.TMDB_ID},
                MovieColumns.TMDB_ID + " = ? ",
                new String[]{Integer.toString(movieId)},
                null
        );

        // store favorite value in db
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieColumns.FAVORITE, swapFavorites);

        if (cursor.moveToFirst()) {
            // Update movie information if it already exists in local db. Otherwise, do nothing.
            //  The movie should already be in the db
            locationId = cr.update(MovieProvider.Movies.CONTENT_URI,
                    movieValues,
                    MovieColumns.TMDB_ID + " = ? ",
                    new String[]{Integer.toString(movieId)}
            );
            if (swapFavorites == 1) {
                Toast.makeText(mContext, getString(R.string.format_modify_favorites,
                        "Added", mData.getString(Details.COL_MOVIE_TITLE), "to"),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, getString(R.string.format_modify_favorites,
                        "Removed", mData.getString(Details.COL_MOVIE_TITLE), "from"),
                        Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();

        // swap app bar icon
        loadFavorite(true);

        return locationId;
    }

    /**
     * Shares the movie with friends. If there is a trailer, share first trailer link. If no
     *  trailer, share TMDB link.
     */
    private Intent shareMovie() {
        String path = "";
        Type type = new TypeToken<Trailers>() {}.getType();
        Trailers trailers = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_TRAILERS), type);
        if (trailers != null) {
            List<Youtube> videos = trailers.getYoutube();
            if (videos.size() != 0) {
                path = videos.get(0).getSource();
            }
        }
        String url;
        String shareMessage;
        if (path == "") {
            // If there are no trailers, refer to the tmdb homepage of the movie
            url = URL.PUBLIC_BASE + URL.MOVIE + mData.getInt(Details.COL_MOVIE_TMDB_ID);
            shareMessage = getString(R.string.format_share_movie_no_trailer,
                    mData.getString(Details.COL_MOVIE_TITLE),
                    url,
                    mData.getString(Details.COL_MOVIE_HOMEPAGE)
            );
        } else {
            // If a trailer exists, refer to the trailer
            url = URL.YOUTUBE_BASE + path;
            shareMessage = getString(R.string.format_share_movie_with_trailer,
                    mData.getString(Details.COL_MOVIE_TITLE),
                    url
            );
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        return shareIntent;
    }

    /*
        UI setter methods
     */

    /** Sets the banner with backdrop. White field if path is null */
    private void setBanner() {
        // TODO: Find an appropriate placeholder image for backdrop paths that are null
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + IMAGE.SIZE.BACKDROP.w1280 +
                        mData.getString(Details.COL_MOVIE_BACKDROP_PATH))
                .fit()
                .error(R.drawable.ic_wallpaper_black_48dp)
                .into((ImageView) mRootView.findViewById(R.id.imageview_banner));
    }

    /** Sets the poster. White field if path is null */
    private void setPoster() {
        // TODO: Find an appropriate placeholder image for poster paths that are null
        Log.d(LOG_TAG, "Poster size: " + mPosterSize);
        Picasso.with(mContext)
                .load(URL.IMAGE_BASE + mPosterSize +
                        mData.getString(Details.COL_MOVIE_POSTER_PATH))
                .error(R.drawable.ic_wallpaper_black_48dp)
                .into((ImageView) mRootView.findViewById(R.id.imageview_detail_poster));
    }

    /** Sets the movie tagline if there is one. Otherwise, leave blank */
    private void setTagline() {
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
    private void setTitle() {
        String title = mData.getString(Details.COL_MOVIE_TITLE);
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_title);
        tv.setText(title);
    }

    /** Sets the overview. If no overview is found, text color is grey */
    private void setOverview() {
        String overview = mData.getString(Details.COL_MOVIE_OVERVIEW);
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_overview);
        if (overview != null && overview.length() != 0) {
            tv.setText(overview);
            tv.setTextColor(getResources().getColor(R.color.white));
        }
    }

    /** Sets the main actors, in order of prominence. If no actor is found, text color is grey */
    private void setActors() {
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
                    String actor;
                    if (cast.getCharacter() != "") {
                        actor = getString(R.string.format_detail_actors, cast.getName(), cast.getCharacter());
                    } else {
                        actor = cast.getName();
                    }
                    if (multiple) {
                        actors += ", " + actor;
                    } else {
                        actors = actor;
                        tv.setTextColor(getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(actors);
    }

    /** Sets the directors. If no director is found, text color is grey */
    private void setDirectors() {
        TextView tv = (TextView) mRootView.findViewById(R.id.textview_detail_directors);
        String directors = getString(R.string.error_info_null);
        boolean multiple = false;
        Type type = new TypeToken<Credits>() {}.getType();
        Credits credits = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_CREDITS), type);
        if (credits != null) {
            for (Crew crew : credits.getCrew()) {
                if (crew.getJob().equals(JOBS.DIRECTING.DIRECTOR)) {
                    if (multiple) {
                        directors += ", " + getString(R.string.format_detail_directors, crew.getName(), crew.getJob());
                    } else {
                        directors = getString(R.string.format_detail_directors, crew.getName(), crew.getJob());
                        tv.setTextColor(getResources().getColor(R.color.white));
                        multiple = true;
                    }
                }
            }
        }
        tv.setText(directors);
    }

    /** Sets the writers. If no writer is found, text color is grey */
    private void setWriters() {
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
    private void setRating() {
        TextView ratingTextView = (TextView) mRootView.findViewById(R.id.textview_detail_rating);
        ratingTextView.setText(getString(R.string.format_detail_rating, mData.getInt(Details.COL_MOVIE_VOTE_COUNT)));
        RatingBar bar = (RatingBar) mRootView.findViewById(R.id.ratingbar_detail);
        bar.setRating((float) mData.getDouble(Details.COL_MOVIE_VOTE_AVERAGE) / 2);
    }

    /**
     * Sets the release info, runtime, and mpaa rating pertaining to the user's locale
     *  (empty if not found)
     */
    private void setReleaseInfoRuntimeMpaa() {
        String release_date = ReleaseDates.convertDateFormat(mData.getString(Details.COL_MOVIE_RELEASE_DATE));
        int runtime = mData.getInt(Details.COL_MOVIE_RUNTIME);
        Type type = new TypeToken<Releases>() {}.getType();
        Releases releases = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_RELEASES), type);
        String mpaa_rating = "";
        if (releases != null) {
            for (Country country : releases.getCountries()) {
                if (Locale.getDefault().getCountry().contains(country.getIso31661())) {
                    mpaa_rating = country.getCertification();
                }
            }
        }
        String release_duration_mpaa;
        if (runtime == 0 && mpaa_rating == "") {
            // if unknown runtime, just include release date
            release_duration_mpaa = release_date;
        } else if (runtime != 0 && mpaa_rating == "") {
            // if unknown mpaa rating but known runtime, just include release date and runtime
            release_duration_mpaa = getString(R.string.format_detail_release_runtime, release_date, runtime);
        } else if (runtime == 0 && mpaa_rating != "") {
            // if unknown runtime but known mpaa rating, just include release date and mpaa rating
            release_duration_mpaa = getString(R.string.format_detail_release_mpaa, release_date, mpaa_rating);
        } else {
            // if all information is available, include them all!
            release_duration_mpaa = getString(R.string.format_detail_release_runtime_mpaa, release_date, runtime, mpaa_rating);
        }
        TextView releaseInfoRuntimeTextView = (TextView) mRootView.findViewById(R.id.textview_detail_release_duration_mpaaRating);
        releaseInfoRuntimeTextView.setText(release_duration_mpaa);
        releaseInfoRuntimeTextView.setTextColor(getResources().getColor(R.color.white));
    }

    /** Sets the genres. If no genre is found, text color is grey */
    private void setGenres() {
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
    private void setFooter() {
        TextView url_text = (TextView) mRootView.findViewById(R.id.tmdb_link);

        // use LinkMovementMethod to create hyperlink redirecting to TMDB movie page
        url_text.setClickable(true);
        url_text.setMovementMethod(LinkMovementMethod.getInstance());
        String url = URL.PUBLIC_BASE + URL.MOVIE + mData.getInt(Details.COL_MOVIE_TMDB_ID);
        String html = getString(R.string.format_detail_footer, url);
        url_text.setText(Html.fromHtml(html));
    }


    /** Sets the backdrops **/
    private void setBackdrops() {
        // Create a BackdropsAdapter if haven't already
        if (mBackdropsAdapter == null) {
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
        }
        TwoWayView backdropView = (TwoWayView) mRootView.findViewById(R.id.listview_detail_backdrops);
        backdropView.setAdapter(mBackdropsAdapter);
    }

    /**
     * Sets the trailers
     *
     * Note: OnClickListeners for buttons can be configured through the Adapter, which is pretty cool.
     */
    private void setTrailers() {
        // Create a TrailersAdapter if haven't already
        if (mTrailersAdapter == null) {
            Type type = new TypeToken<Trailers>() {}.getType();
            mTrailers = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_TRAILERS), type);
            if (mTrailers != null) {
                List<Youtube> videos = mTrailers.getYoutube();
                mTrailersAdapter = new TrailersAdapter(mContext, mData.getString(Details.COL_MOVIE_TITLE), videos);
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
    }

    /** Sets the ratings for reviews **/
    private void setDetailedRating() {
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
    private void setReviews() {
        // Create a ReviewsAdapter if haven't already
        if (mReviewsAdapter == null) {
            Type type = new TypeToken<List<Reviews>>() {}.getType();
            mReviews = (new Gson()).fromJson(mData.getString(Details.COL_MOVIE_REVIEWS), type);
            if (mReviews != null) {
                mReviewsAdapter = new ReviewsAdapter(mContext, mData.getString(Details.COL_MOVIE_TITLE), mReviews);
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

    /** Helper method for setting app bar buttons and all basic ui fields **/
    private void setInitialFields() {
        if (!mLoaded) {
            // app bar buttons
            loadFavorite(false);

            // ui fields
            setBanner();
            setPoster();
            setTitle();
            setOverview();
            setRating();
            setDetailedRating();

            swapViewContainers();
        }
    }

    /** Helpe rmethod for setting all detailed ui fields **/
    private void setDetailedFields() {
        if (!mLoaded) {
            setTagline();
            setReleaseInfoRuntimeMpaa();
            setGenres();
            setFooter();
            setActors();
            setDirectors();
            setWriters();
            setBackdrops();
            setTrailers();
            setReviews();
            mLoaded = true;
        }
    }

    private void swapViewContainers() {
        if (mData == null) {
            SwapViewContainers.showViewContainer(mProgressContainer, mRootView);
        } else {
            SwapViewContainers.hideViewContainer(mProgressContainer, mRootView);
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

//        Toast.makeText(mContext,
//                "Movie in favorites? " + (mData.getInt(Details.COL_MOVIE_FAVORITE)),
//                Toast.LENGTH_SHORT).show();

        // Attach an intent to this ShareActionProvider.
        // Use new thread to ensure that the correct share intent is loaded onto the provider
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mShareActionProvider == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        mShareActionProvider.setShareIntent(shareMovie());
                    }
                });
            }
        }).start();

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
