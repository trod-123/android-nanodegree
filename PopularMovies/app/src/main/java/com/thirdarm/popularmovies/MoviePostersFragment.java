package com.thirdarm.popularmovies;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResults;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment consisting of a grid of movie posters
 */
public class MoviePostersFragment extends Fragment {

    public final String LOG_TAG = "MoviePostersFragment";

    // display constants
    public final int POSTER_WIDTH = 185;
    public final int POSTER_HEIGHT = 277;
    public final int GRID_PADDING = 0;
    public final int NUM_POSTERS = 10;

    public Context mContext;
    public final int mDelay = 100;

    // views
    View rootView;
    GridView mPostersGrid;

    // Playing with TMDB
    public TMDB TMDB;
    public List<MovieDBResults.MovieDBResult> results;
    public ArrayList<MovieDB> movies;
    public ArrayList<String> poster_urls;
    public final String poster_size = "w500";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // gets a reference to the root view
        rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        // Create TMDB API
        TMDB = new TMDB(mContext, getString(R.string.movie_api_key));

        populateMovies(TMDB, "vote_average.desc");

        // Can't do this. Result ends up being null even though it was called through onResponse().
        //  Apparently, even though result was modified in onResponse(), it returns to being null
        //  when onResponse() has been completed.
        // results = TMDB.getResults().get(0).getTitle();

        // The reason why it is null when the above line is called is because the app is still
        //  getting data from the server, and therefore it needs to wait for until the
        //  data has been collected before it is no longer null.

        // TODO_DONE: Need to figure out how to make the result change persistent
        // Bingo. Run a new thread that continuously checks to see if result has been loaded with
        //  the List<MovieDBResult> data, and once it has been loaded, access the data. While
        //  the thread is continuously checking, it waits for some mDelay to reduce the number
        //  of getResults() calls to TMDB.
        // TODO: Now, is this efficient?

        return rootView;
    }

    public void populateMovies(TMDB api, String sort) {
        api.discover(sort);
        populateMovieDBInfo();
    }

    public void populateMovieDBInfo() {
        new Thread(new Runnable() {
            public void run() {
                // first check if the results are ready
                results = getResults(TMDB); // issue. it waits for it to return before proceeding
                //  to next method below

                // now get the individual movies and populate the movies list
                movies = getMovies(TMDB);

                // now get the poster URLs for each of the movies
                poster_urls = getPosterUrls(movies);

                // finally, get and fill the grid with posters
                setPostersGridView(rootView); // uses post() to fill the grid
            }
        }).start();
    }

    public List<MovieDBResults.MovieDBResult> getResults(TMDB api) {
        while (api.getResults() == null) {
            try {
                Thread.sleep(mDelay);
                Log.d(LOG_TAG, "A getResults() call");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       return api.getResults();
    }

    public ArrayList<MovieDB> getMovies(TMDB api) {
        for (int i : api.getMovieIDs()) {
            api.getMovieDetails(i);
        }
        while (api.getMovies().size() != api.getMovieIDs().length) {
            try {
                Thread.sleep(mDelay);
                Log.d(LOG_TAG, "A getMovies() and getMovieIDs() call");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(LOG_TAG, "Size of getMovies(): " + api.getMovies().size());
        return api.getMovies();
    }

    public ArrayList<String> getPosterUrls(ArrayList<MovieDB> movies) {
        ArrayList<String> urls = new ArrayList<>();
        for (MovieDB movie : movies) {
            urls.add(movie.getPosterPath());
        }
        Log.d(LOG_TAG, "Size of poster_urls: " + urls.size());
        return urls;
    }

    public void setPostersGridView(View rootView) {
        // gets a reference to the GridView
        mPostersGrid = (GridView) rootView.findViewById(R.id.posters_grid);
        Log.d(LOG_TAG, "Got mPostersGrid reference");

        // Sets the adapter for the GridView. Needs to call post() because this method is called
        //  from another thread
        mPostersGrid.post(new Runnable() {
            @Override
            public void run() {
                mPostersGrid.setAdapter(new PostersAdapter(mContext, poster_urls));

                // launches a "more details" screen for the selected movie
                mPostersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Toast.makeText(getActivity(), movies.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
    //  to be displayed in the grid.
    // Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
    //  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
    public class PostersAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> image_urls;

        public PostersAdapter(Context c, ArrayList<String> images) {
            mContext = c;
            image_urls = images;
        }

        public int getCount() {
            return image_urls.size();
        }

        // returns the actual object at specified position
        public Object getItem(int position) {
            return null;
        }

        // returns the row id of the object at specified position
        public long getItemId(int position) {
            return 0;
        }

        // creates a new view (in this case, ImageView) for each item referenced by the Adapter
        // How it works:
        //  - a view is passed in, which is normally a recycled object
        //  - checks to see if that view is null
        //     - if view is null, a view is initialized and configured with desired properties
        //     - if view is not null, that view is then returned
        public View getView(int position, View convertView, ViewGroup parent) {
            final int p = position;
            final ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                //imageView.setLayoutParams(new GridView.LayoutParams(POSTER_WIDTH, POSTER_HEIGHT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING);
            } else {
                imageView = (ImageView) convertView;
            }

            Log.d(LOG_TAG, "About to run picasso");
            Log.d(LOG_TAG, "Image url link: " + getString(R.string.image_base_url) + poster_size + poster_urls.get(position));
            Picasso.with(mContext)
                    .load(getString(R.string.image_base_url) + poster_size + poster_urls.get(position))
                    .placeholder(R.drawable.piq_76054_400x400)
                    .error(R.drawable.piq_76054_400x400)
                    .into(imageView);

            return imageView;
        }
    }
}
