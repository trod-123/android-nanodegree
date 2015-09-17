package com.thirdarm.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import com.thirdarm.popularmovies.constant.DISCOVER;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.MovieDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment consisting of a grid of movie posters
 */
public class MoviePostersFragment extends Fragment {

    public final String LOG_TAG = "MoviePostersFragment";

    public Context mContext;

    // views
    public View rootView;
    public GridView mPostersGrid;
    public LinearLayout progress_container;
    public static TextView progress_status; // allow other classes to modify the loading status

    // Playing with TMDB
    public TMDB TMDB;
    public ArrayList<MovieDB> movies;
    public final String poster_size = IMAGE.SIZE.POSTER.w500;
    public String category = PARAMS.CATEGORY.POPULAR;
    public String sort_by = DISCOVER.SORT.POPULARITY_DESC;
    public String language = "en";
    public boolean load_guard = true;

    public MoviePostersFragment() {
        // TODO: Figure out whether it would be preferred to allow activities or fragments to handle menu events
        // This line allows fragment to handle menu events
        // Note this can also be called in the onCreate() method instead
        setHasOptionsMenu(true);
    }

    // A fragment's onCreate() method is called before its onCreateView() method
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
                new FetchMovieResultsTask().execute(category = PARAMS.CATEGORY.DISCOVER);
                return true;

            case R.id.action_get_playing:
                new FetchMovieResultsTask().execute(category = PARAMS.CATEGORY.PLAYING);
                return true;

            case R.id.action_get_popular:
                new FetchMovieResultsTask().execute(category = PARAMS.CATEGORY.POPULAR);
                return true;

            case R.id.action_get_rated:
                new FetchMovieResultsTask().execute(category = PARAMS.CATEGORY.TOP);
                return true;

            case R.id.action_get_upcoming:
                new FetchMovieResultsTask().execute(category = PARAMS.CATEGORY.UPCOMING);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // gets a reference to the root view
        rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);
        progress_container = (LinearLayout) rootView.findViewById(R.id.progress_container);
        progress_status = (TextView) rootView.findViewById(R.id.progress);

        // gets a reference to the GridView
        mPostersGrid = (GridView) rootView.findViewById(R.id.posters_grid);

        // Create TMDB API
        TMDB = new TMDB(getString(R.string.movie_api_key), language, 1);

        // Populate with popular movies by default
        new FetchMovieResultsTask().execute(category);

        return rootView;
    }


    /**
     * Methods below collect and parse JSON data from the TMDB servers via API calls and
     * fill the main UI with posters in a grid view.
     */

    // AsyncTask for fetching movie data
    public class FetchMovieResultsTask extends AsyncTask<String, Void, ArrayList<MovieDB>> {
        @Override protected void onPreExecute() {
            // Check for internet connection
            // TODO: Make internet connection checks persistent while grabbing data from server
            if (!isNetworkAvailable()) {
                progress_status.setText("There is no active internet connection.");
                progress_container.findViewById(R.id.progress_spinner).setVisibility(View.GONE);
                cancel(true);
            }

            // Check if sort buttons have been clicked before results have been loaded
            if (!load_guard) {
                Toast.makeText(mContext, "Still loading results. Please wait.", Toast.LENGTH_SHORT).show();
                cancel(true);
            } else {
                // Disable reloading while grid is being populated
                load_guard = false;
            }

            // Stop the AsyncTask if either condition above is met
            if (isCancelled()) {
                return;
            }

            // Otherwise, proceed with the AsyncTask
            showProgressBar();
            setTitle(category);
        }

        @Override protected ArrayList<MovieDB> doInBackground(String... category) {
            if (category[0] == PARAMS.CATEGORY.DISCOVER) {
                return TMDB.discover(sort_by);
            } else {
                return TMDB.getResults(category[0]);
            }
        }

        @Override protected void onPostExecute(ArrayList<MovieDB> result) {
            // Make sure result is not null
            if (result != null) {
                movies = result;
                mPostersGrid.setAdapter(new PostersAdapter(mContext, movies));

                // Launch a "more details" screen for the selected movie
                mPostersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        MovieDB dataToSend = movies.get(position);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("myData", dataToSend);
                        startActivity(intent);
                    }
                });

                // Enable reloading and hide the progress spinner
                load_guard = true;
                hideProgressBar();
            }
        }
    }

    public void showProgressBar() {
        // TODO: Figure out how to fade the view in and out instead of having it just appear
        //  right away
        progress_container.bringToFront();
        progress_container.findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        progress_container.setVisibility(View.VISIBLE);
        enableDisableViewGroup((ViewGroup) rootView, false);
    }

    public void hideProgressBar() {
        progress_container.setVisibility(View.GONE);
        rootView.setClickable(true);
        enableDisableViewGroup((ViewGroup) rootView, true);
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

    // Check network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Set the title of the activity based on sort category
    public void setTitle(String category) {
        switch (category) {
            case PARAMS.CATEGORY.DISCOVER: {
                getActivity().setTitle("Discover");
                break;
            }
            case PARAMS.CATEGORY.PLAYING: {
                getActivity().setTitle("Now playing");
                break;
            }
            case PARAMS.CATEGORY.POPULAR: {
                getActivity().setTitle("Popular movies");
                break;
            }
            case PARAMS.CATEGORY.TOP: {
                getActivity().setTitle("Top rated");
                break;
            }
            case PARAMS.CATEGORY.UPCOMING: {
                getActivity().setTitle("Upcoming movies");
                break;
            }
        }
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

            TextView name = (TextView) convertView.findViewById(R.id.poster_name);
            name.setText(movies.get(position).getTitle());

            TextView date = (TextView) convertView.findViewById(R.id.poster_date);
            date.setText(movies.get(position).getReleaseDate());

            TextView rating = (TextView) convertView.findViewById(R.id.poster_rating);
            rating.setText("Rating: " +
                    new DecimalFormat("#.##").format(movies.get(position).getVoteAverage()) +
                    " (" + movies.get(position).getVoteCount() + " reviews)");

            ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

            Picasso.with(mContext)
                    .load(URL.BASE_IMAGE_URL + poster_size + movies.get(position).getPosterPath())
                    .error(R.drawable.piq_76054_400x400)
                    .into(imageView);

            return convertView;
        }
    }
}
