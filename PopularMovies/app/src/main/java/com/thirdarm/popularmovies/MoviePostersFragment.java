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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.utilities.AutoResizeImageView;
import com.thirdarm.popularmovies.utilities.AutoResizeTextView;
import com.thirdarm.popularmovies.utilities.Network;
import com.thirdarm.popularmovies.utilities.ReleaseDates;
import com.thirdarm.popularmovies.model.MovieDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment consisting of a grid of movie posters
 */
public class MoviePostersFragment extends Fragment {

    public static final String LOG_TAG = "MoviePostersFragment";
    public static final String INTENT_DATA = "myData";
    public static final String DATA_MOVIES = "movies";
    public static final String DATA_TITLE = "title";
    public static final String DATA_POSITION = "position";

    public Context mContext;

    // views
    public View mRootView;
    public GridView mGridView;
    public RelativeLayout mProgressContainer;

    // allow TMDB to modify the loading status
    public static TextView sProgressStatus;
    public static ProgressBar sProgressBar;

    // Playing with TMDB
    public static TMDB mTmdb; // allow detail activity to access the TMDB
    public ArrayList<MovieDB> mMovies;
    public final String mPosterSize = IMAGE.SIZE.POSTER.w500;
    public String mCategory = PARAMS.CATEGORY.POPULAR;
    public String mSort = PARAMS.RESULTS.SORT.POPULARITY_DESC;
    public String mLanguage = "en";
    public int mPage = 1;
    public boolean mLoadGuard = true;



    public MoviePostersFragment() {
        // TODO: Figure out whether it would be preferred to allow activities or fragments to handle menu events
        // This line allows fragment to handle menu events
        // Note this can also be called in the onCreate() method instead
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar
        inflater.inflate(R.menu.movie_posters_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_discover:
                fetchMovies(mCategory = PARAMS.CATEGORY.DISCOVER);
                return true;

            case R.id.action_get_playing:
                fetchMovies(mCategory = PARAMS.CATEGORY.PLAYING);
                return true;

            case R.id.action_get_popular:
                fetchMovies(mCategory = PARAMS.CATEGORY.POPULAR);
                return true;

            case R.id.action_get_rated:
                fetchMovies(mCategory = PARAMS.CATEGORY.TOP);
                return true;

            case R.id.action_get_upcoming:
                fetchMovies(mCategory = PARAMS.CATEGORY.UPCOMING);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        // Save and reuse information upon activity destroy
        if (mLoadGuard && mMovies != null) {
            outState.putParcelableArrayList(DATA_MOVIES, mMovies);
            outState.putString(DATA_TITLE, mCategory);
            outState.putInt(DATA_POSITION, mGridView.getFirstVisiblePosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get a reference to UI elements
        mRootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);
        mProgressContainer = (RelativeLayout) mRootView.findViewById(R.id.progress_container);
        sProgressStatus = (TextView) mRootView.findViewById(R.id.progress);
        sProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        mGridView = (GridView) mRootView.findViewById(R.id.posters_grid);

        // Create TMDB API
        mTmdb = new TMDB(getString(R.string.movie_api_key), mLanguage, mPage);

        // Recycle information from last destroy, if applicable
        if(savedInstanceState != null &&
                savedInstanceState.containsKey(DATA_MOVIES) &&
                savedInstanceState.containsKey(DATA_TITLE) &&
                savedInstanceState.containsKey(DATA_POSITION)) {
            setGridView(mMovies = savedInstanceState.getParcelableArrayList(DATA_MOVIES));
            setTitle(mCategory = savedInstanceState.getString(DATA_TITLE));
            mGridView.smoothScrollToPosition(savedInstanceState.getInt(DATA_POSITION));
            hideProgressBar();
        } else {
            // Otherwise, populate with popular movies by default
            fetchMovies(mCategory);
        }

        // TODO: Create a preferences activity and load movies according to preferences. Include:
        //  -size of posters: list of dimensions (focus on widths)
        //  -info overlay: true or false
        //  -language
        //  -number of results per page (and functionality in UI to view other pages too)

        return mRootView;
    }


    public void fetchMovies(String category) {
        // Check for internet connection
        // TODO: Make internet connection checks persistent while grabbing data from server
        if (!Network.isNetworkAvailable(mContext)) {
            sProgressStatus.setText(getString(R.string.status_no_internet));
            showProgressBar();
            mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            return;
        }

        // Check if sort buttons have been clicked before results have been loaded
        if (!mLoadGuard) {
            Toast.makeText(mContext, getString(R.string.status_still_loading), Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Disable reloading while grid is being populated
            mLoadGuard = false;
        }

        // Otherwise, proceed with the AsyncTask
        showProgressBar();
        sProgressStatus.setText(getString(R.string.status_loading));
        setTitle(mCategory);

        new FetchMovieResultsTask(mTmdb, mSort).execute(category);
    }

    /**
     * Displays overlay containing loading icon and status, resets horizontal progress bar, and
     *  disables touch events.
     */
    public void showProgressBar() {
        // TODO: Figure out how to fade the view in and out instead of having it just appear
        //  right away
        mProgressContainer.bringToFront();
        mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.VISIBLE);
        sProgressBar.setProgress(0);
        enableDisableViewGroup((ViewGroup) mRootView, false);
    }

    /** Hides overlay and enables touch events */
    public void hideProgressBar() {
        mProgressContainer.setVisibility(View.GONE);
        mRootView.setClickable(true);
        enableDisableViewGroup((ViewGroup) mRootView, true);
    }

    /**
     * Enables/Disables all child views in a view group.
     * From http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
     *
     * @param viewGroup the view group
     * @param enabled <code>true</code> to enable, <code>false</code> to disable
     * the views.
     */
    public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    /**
     * Sets the title of the activity based on sort category
     *
     * @param category the category used to sort movies
     */
    public void setTitle(String category) {
        switch (category) {
            case PARAMS.CATEGORY.DISCOVER:
                getActivity().setTitle(getString(R.string.title_discover));
                break;

            case PARAMS.CATEGORY.PLAYING:
                getActivity().setTitle(getString(R.string.title_playing));
                break;

            case PARAMS.CATEGORY.POPULAR:
                getActivity().setTitle(getString(R.string.title_popular));
                break;

            case PARAMS.CATEGORY.TOP:
                getActivity().setTitle(getString(R.string.title_top_rated));
                break;

            case PARAMS.CATEGORY.UPCOMING:
                getActivity().setTitle(getString(R.string.title_upcoming));
                break;
        }
    }

    /**
     * Populates the grid view with posters
     *
     * @param movies the list of movies
     */
    public void setGridView(final ArrayList<MovieDB> movies) {
        mGridView.setAdapter(new PostersAdapter(mContext, movies, mPosterSize));

        // Launch a "more details" screen for the selected movie when poster is clicked
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieDB dataToSend = movies.get(position);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(INTENT_DATA, dataToSend);
                startActivity(intent);
            }
        });
    }


    // TODO: Uncomment this once LoadManager has been implemented. This will re-enable loading
    //  and hide the progress bar overlay. The movies will have been loaded into the grid view
    //  after the null cursor has been swapped with the loaded cursor.
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//
//            // This is going to be used in a custom adapter class
//            // e.g. new ForecastAdapter(getActivity(), Cursor, flags)
//            if (result != null) {
//                setGridView(mMovies = result);
//
//                // Enable reloading and hide the progress spinner
//                mLoadGuard = true;
//                hideProgressBar();
//            }
//    }

}
