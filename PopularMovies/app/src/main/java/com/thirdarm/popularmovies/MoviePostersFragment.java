package com.thirdarm.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.thirdarm.popularmovies.API.TMDB;
import com.thirdarm.popularmovies.constant.DISCOVER;
import com.thirdarm.popularmovies.constant.IMAGE;
import com.thirdarm.popularmovies.constant.URL;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResults;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment consisting of a grid of movie posters
 */
public class MoviePostersFragment extends Fragment {

    public final String LOG_TAG = "MoviePostersFragment";

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
    public final String poster_size = IMAGE.SIZE.POSTER.w500;
    public String sort_by = DISCOVER.SORT.POPULARITY_DESC;
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
                populateMovies("discover", TMDB, sort_by);
                return true;

            case R.id.action_get_playing:
                populateMovies("getNowPlaying", TMDB, null);
                return true;

            case R.id.action_get_popular:
                populateMovies("getPopular", TMDB, null);
                return true;

            case R.id.action_get_rated:
                populateMovies("getTopRated", TMDB, null);
                return true;

            case R.id.action_get_upcoming:
                populateMovies("getUpcoming", TMDB, null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // gets a reference to the root view
        rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        // gets a reference to the GridView
        mPostersGrid = (GridView) rootView.findViewById(R.id.posters_grid);

        // Create TMDB API
        TMDB = new TMDB(getString(R.string.movie_api_key), "en");

        populateMovies("getPopular", TMDB, null);

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


    /*
    The following methods deal with collecting and parsing JSON data from the TMDB servers via
     API calls, and filling the main UI with posters.
     */

    public void showProgressBar() {
        LinearLayout pc = (LinearLayout) rootView.findViewById(R.id.progress_container);
        pc.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        LinearLayout pc = (LinearLayout) rootView.findViewById(R.id.progress_container);
        pc.setVisibility(View.GONE);
    }

    public void populateMovies(String category, TMDB api, String sort) {
        if (!load_guard) {
            Log.d(LOG_TAG, "STILL LOADING PLEASE WAIT");
            return;
        } else {
            load_guard = false;
        }

        switch (category) {
            case "discover": {
                try {
                    api.discover(sort);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Log.d(LOG_TAG, "Discover clicked");
                break;
            }
            case "getNowPlaying": {
                api.getNowPlaying();
                Log.d(LOG_TAG, "Now playing clicked");
                break;
            }
            case "getPopular": {
                api.getPopular();
                Log.d(LOG_TAG, "Popular clicked");
                break;
            }
            case "getTopRated": {
                api.getTopRated();
                Log.d(LOG_TAG, "Top rated clicked");
                break;
            }
            case "getUpcoming": {
                api.getUpcoming();
                Log.d(LOG_TAG, "Upcoming clicked");
                break;
            }
        }
        resetPostersGridView();
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
                setPostersGridView(); // uses post() to fill the grid
            }
        }).start();
    }

    public List<MovieDBResults.MovieDBResult> getResults(TMDB api) {
        while (api.getResults() == null) {
            try {
                Thread.sleep(mDelay);
                //Log.d(LOG_TAG, "A getResults() call");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       return api.getResults();
    }

    public ArrayList<MovieDB> getMovies(TMDB api) {
        for (int i : api.getMovieIDs()) {
            api.getMovieDetails(i);
            //Log.d(LOG_TAG, "I think Movie " + i + " has been added");

            // make sure that the movies are added in the right order by waiting until the
            //  current movie has been added to the movies list before adding the next
            int current = api.getMovies().size();
            while (current != api.getMovies().size() - 1) {
                try {
                    Thread.sleep(mDelay);
                    //Log.d(LOG_TAG, "A getMovies().size() call");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.d(LOG_TAG, "Length of MovieIDs: " + api.getMovieIDs().length);
        while (api.getMovies().size() != api.getMovieIDs().length) {
            try {
                Thread.sleep(mDelay);
                //Log.d(LOG_TAG, "A getMovies() and getMovieIDs() call");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Log.d(LOG_TAG, "Size of getMovies(): " + api.getMovies().size());
        return api.getMovies();
    }

    public ArrayList<String> getPosterUrls(ArrayList<MovieDB> movies) {

//        // for debugging. save for future errors
//        Log.d(LOG_TAG, "Posters movie size: " + movies.size());
//        for (MovieDB movie : movies) {
//            if (movie == null) {
//                Log.d(LOG_TAG, "MOVIE IS NULL");
//            } else {
//                Log.d(LOG_TAG, "Movie is not null");
//            }
//        }

        ArrayList<String> urls = new ArrayList<>();
        for (MovieDB movie : movies) {
            urls.add(movie.getPosterPath());
            //Log.d(LOG_TAG, movie.getTitle() + " poster added");
            Log.d(LOG_TAG, movie.getTitle() + ": poster link " + movie.getPosterPath());
        }
        //Log.d(LOG_TAG, "Size of poster_urls: " + urls.size());
        return urls;
    }

    public void setPostersGridView() {
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
                //Log.d(LOG_TAG, "Posters loaded");
                load_guard = true;
                hideProgressBar();
            }
        });
    }

    // Resets the GridView for reloading new posters
    public void resetPostersGridView() {
        showProgressBar();
        mPostersGrid.post(new Runnable() {
            @Override public void run() {
                mPostersGrid.setAdapter(null);
            }
        });
    }


    /*
    ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
     to be displayed in the grid.
    Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
     Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
     */
    public class PostersAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> image_urls;
        private LayoutInflater inflater;

        public PostersAdapter(Context c, ArrayList<String> images) {
            mContext = c;
            image_urls = images;
            inflater = LayoutInflater.from(c);
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

            //Log.d(LOG_TAG, "About to run picasso...");
            //Log.d(LOG_TAG, "Image url link: " + URL.BASE_IMAGE_URL + poster_size + poster_urls.get(position));

//          //Do not include here as it consumes too much memory and results in crash
//            Picasso.Builder builder = new Picasso.Builder(mContext);
//            builder.listener(new Picasso.Listener() {
//                @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
//                    Log.e(LOG_TAG, "ERROR PICASSO DID NOT LOAD IMAGE");
//                    e.printStackTrace();
//                }
//            });

            final int p = position;
            Picasso.with(mContext)
                    .load(URL.BASE_IMAGE_URL + poster_size + poster_urls.get(position))
                    .placeholder(R.drawable.sample_0)
                    .error(R.drawable.piq_76054_400x400)
                    .into(imageView, new Callback() {

                        // Callback used to notify whether Picasso successfully loaded the image
                        @Override public void onSuccess() {
                            Log.d(LOG_TAG, movies.get(p).getTitle() + ": loaded successfully");
                        }

                        @Override public void onError() {
                            Log.e(LOG_TAG, movies.get(p).getTitle() + ": ERROR PICASSO DID NOT LOAD IMAGE");
                        }
                    });

            return convertView;
        }
    }
}
