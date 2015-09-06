package com.thirdarm.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Fragment consisting of a grid of movie posters
 */
public class MoviePostersFragment extends Fragment {

    public final String LOG_TAG = "MY MOVIES APP";

    // display constants
    public final int POSTER_WIDTH = 185;
    public final int POSTER_HEIGHT = 277;
    public final int GRID_PADDING = 0;
    public final int NUM_POSTERS = 10;

    // views
    GridView mPostersGrid;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // gets a reference to the root view
        View rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        // gets a reference to the GridView
        mPostersGrid = (GridView) rootView.findViewById(R.id.posters_grid);

        // sets the adapter for the GridView
        mPostersGrid.setAdapter(new PostersAdapter(getActivity()));

        // launches a "more details" screen for the selected movie
        mPostersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // ArrayAdapter for holding the movie posters. Custom adapter will be the source for all items
    //  to be displayed in the grid.
    // Closely follows BaseAdapter template as outlined in the DAC GridView tutorial
    //  Link here: http://developer.android.com/guide/topics/ui/layout/gridview.html
    public class PostersAdapter extends BaseAdapter {
        private Context mContext;

        public PostersAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
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
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(POSTER_WIDTH, POSTER_HEIGHT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // reference to images
        private Integer[] mThumbIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };
    }
}
