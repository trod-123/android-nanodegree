package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import barqsoft.footballscores.data.*;
import barqsoft.footballscores.data.ScoresProvider;
import barqsoft.footballscores.service.myFetchService;

/**
 * Fragment that displays the list of scores in a recycler view layout
 */
public class ScoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ScoresFragment.class.getSimpleName();
    private ScoresAdapter mScoresAdapter;
    private static final int SCORES_LOADER = 0;

    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;

    public ScoresFragment() {
    }

    // This calls myFetchService to fetch scores from the online db.
    // Called during onCreateView()
    // TODO: Call this when necessary only.
    private void update_scores() {
        Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(String date) {
        Log.d(LOG_TAG, "Fragment date set to: " + date);
        fragmentdate[0] = date;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Preparing the list view layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View emptyView = rootView.findViewById(R.id.recyclerview_scores_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_scores);
        mScoresAdapter = new ScoresAdapter(getActivity(), new ScoresAdapter.ScoresAdapterOnClickHandler() {
            @Override public void onClick(double match_id, ScoresAdapter.ViewHolder vh) {
                mScoresAdapter.detail_match_id = match_id;
                MainActivity.selected_match_id = (int) mScoresAdapter.detail_match_id;
                mScoresAdapter.notifyDataSetChanged();
                mPosition = vh.getAdapterPosition();
            }
        }, emptyView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mScoresAdapter);

        mScoresAdapter.detail_match_id = MainActivity.selected_match_id;

//        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
//                mScoresAdapter.detail_match_id = selected.match_id;
//                MainActivity.selected_match_id = (int) selected.match_id;
//                mScoresAdapter.notifyDataSetChanged();
//            }
//        });


//        Toast.makeText(getActivity(), "The date is: " + fragmentdate[0], Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "The date is: " + fragmentdate[0]);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about:
                Intent start_about = new Intent(getActivity(), AboutActivity.class);
                startActivity(start_about);
                return true;
            case R.id.action_refresh:
                update_scores();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Load only the matches pertaining to the appropriate date
        Log.d(LOG_TAG, "onCreateLoader");
        return new CursorLoader(getActivity(),
                ScoresProvider.Scores.CONTENT_URI,
                ScoresProjections.COLUMNS,
                ScoresColumns.DATE + " == ? ",
                fragmentdate,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        int i = 0;
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast())
//        {
//            i++;
//            cursor.moveToNext();
//        }
        Log.d(LOG_TAG, "onLoadFinished");

        mScoresAdapter.swapCursor(cursor);
        mScoresAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mScoresAdapter.swapCursor(null);
    }


}
