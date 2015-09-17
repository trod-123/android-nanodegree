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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.function.ReleaseDates;
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
    public LinearLayout mProgressContainer;
    public static TextView sProgressStatus; // allow other classes to modify the loading status

    // Playing with TMDB
    public TMDB mTmdb;
    public ArrayList<MovieDB> mMovies;
    public final String mPosterSize = IMAGE.SIZE.POSTER.w500;
    public String mCategory = PARAMS.CATEGORY.POPULAR;
    public String mSort = PARAMS.DISCOVER.SORT.POPULARITY_DESC;
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
                new FetchMovieResultsTask().execute(mCategory = PARAMS.CATEGORY.DISCOVER);
                return true;

            case R.id.action_get_playing:
                new FetchMovieResultsTask().execute(mCategory = PARAMS.CATEGORY.PLAYING);
                return true;

            case R.id.action_get_popular:
                new FetchMovieResultsTask().execute(mCategory = PARAMS.CATEGORY.POPULAR);
                return true;

            case R.id.action_get_rated:
                new FetchMovieResultsTask().execute(mCategory = PARAMS.CATEGORY.TOP);
                return true;

            case R.id.action_get_upcoming:
                new FetchMovieResultsTask().execute(mCategory = PARAMS.CATEGORY.UPCOMING);
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
        mProgressContainer = (LinearLayout) mRootView.findViewById(R.id.progress_container);
        sProgressStatus = (TextView) mRootView.findViewById(R.id.progress);
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
            new FetchMovieResultsTask().execute(mCategory);
        }

        return mRootView;
    }

    /**
     * Collects and parses JSON data from the TMDB servers via API calls and
     * fills the main UI with posters in a grid view.
     */
    public class FetchMovieResultsTask extends AsyncTask<String, Void, ArrayList<MovieDB>> {
        @Override protected void onPreExecute() {
            // Check for internet connection
            // TODO: Make internet connection checks persistent while grabbing data from server
            if (!isNetworkAvailable()) {
                sProgressStatus.setText(getString(R.string.status_no_internet));
                mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.GONE);
                cancel(true);
            }

            // Check if sort buttons have been clicked before results have been loaded
            if (!mLoadGuard) {
                Toast.makeText(mContext, getString(R.string.status_still_loading), Toast.LENGTH_SHORT).show();
                cancel(true);
            } else {
                // Disable reloading while grid is being populated
                mLoadGuard = false;
            }

            // Stop the AsyncTask if either condition above is met
            if (isCancelled()) {
                return;
            }

            // Otherwise, proceed with the AsyncTask
            showProgressBar();
            setTitle(mCategory);
        }

        @Override protected ArrayList<MovieDB> doInBackground(String... category) {
            if (category[0] == PARAMS.CATEGORY.DISCOVER) {
                return mTmdb.discover(mSort);
            } else {
                return mTmdb.getResults(category[0]);
            }
        }

        @Override protected void onPostExecute(ArrayList<MovieDB> result) {
            // Make sure result is not null
            if (result != null) {
                setGridView(mMovies = result);

                // Enable reloading and hide the progress spinner
                mLoadGuard = true;
                hideProgressBar();
            }
        }
    }

    /** Displays overlay containing loading icon and status and disables touch events */
     public void showProgressBar() {
        // TODO: Figure out how to fade the view in and out instead of having it just appear
        //  right away
        mProgressContainer.bringToFront();
        mProgressContainer.findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.VISIBLE);
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

    /** Checks network connection */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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
        mGridView.setAdapter(new PostersAdapter(mContext, movies));

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


    /**
     * ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
     *  to be displayed in the grid.
     * Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
     *  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
     */
    public class PostersAdapter extends BaseAdapter {
        private Context mContext;
        private List<MovieDB> movies;
        private LayoutInflater inflater;

        public PostersAdapter(Context c, List<MovieDB> movies) {
            mContext = c;
            this.movies = movies;
            inflater = LayoutInflater.from(c);
        }

        public int getCount() {
            return movies.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.poster, null);
            }

            AutoResizeTextView name = (AutoResizeTextView) convertView.findViewById(R.id.poster_name);
            name.setText(movies.get(position).getTitle());

            AutoResizeTextView date = (AutoResizeTextView) convertView.findViewById(R.id.poster_date);
            date.setText(ReleaseDates.convertDateFormat(movies.get(position).getReleaseDate()));

            AutoResizeTextView rating = (AutoResizeTextView) convertView.findViewById(R.id.poster_rating);
            rating.setText(
                    getString(R.string.detail_ratings)
                            + ": "
                            + new DecimalFormat("#.##").format(movies.get(position).getVoteAverage())
                            + " ("
                            + movies.get(position).getVoteCount()
                            + " "
                            + getString(R.string.detail_reviews).toLowerCase()
                            + ")");

            ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

            Picasso.with(mContext)
                    .load(URL.IMAGE_BASE + mPosterSize + movies.get(position).getPosterPath())
                    .error(R.drawable.piq_76054_400x400)
                    .into(imageView);

            return convertView;
        }
    }
}
