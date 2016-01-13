package com.thirdarm.footballscores;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureCursor;
import com.thirdarm.footballscores.provider.fixture.FixtureSelection;
import com.thirdarm.footballscores.sync.ScoresSyncAdapter;
import com.thirdarm.footballscores.utilities.Network;
import com.thirdarm.footballscores.widget.ScoresWidgetProvider;

/**
 * Fragment that displays the list of scores in a recycler view layout
 */
public class ScoresFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
                   SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = ScoresFragment.class.getSimpleName();
    private int FRAGMENT_INDEX = 0;

    private ScoresAdapter mScoresAdapter;
    private static final int SCORES_LOADER = 0;

    // For the savedInstanceState
    private static final String KEY_FRAGMENT_DATE = "fragment_date_key";
    private static final String KEY_FRAGMENT_INDEX = "fragment_index_key";

    private RecyclerView mRecyclerView;

    private String[] fragmentdate = new String[1];

    private SwipeRefreshLayout mRefreshLayout;

    public ScoresFragment() {
    }

    public void setFragmentDate(String date) {
        fragmentdate[0] = date;
    }

    public void setFragmentIndex(int index) {
        FRAGMENT_INDEX = index;
    }

    /*
        When implementing SharedPreferences.OnSharedPreferenceChangeListener, it is necessary to
         register the listeners in onResume() and unregister them in onPause(), or else
         onSharedPreferenceChanged() will not be called, even though the SharedPreferences has
         been changed.
     */

    // This calls the SyncAdapter to fetch scores from the online db.
    // Called when user swipes downward to refresh (onRefresh()) or when user clicks action_refresh
    private void update_scores() {
        mRefreshLayout.setRefreshing(true);
        ScoresSyncAdapter.syncImmediately(getActivity().getApplicationContext());
        getLoaderManager().restartLoader(SCORES_LOADER, null, this);
    }

    @Override
    public void onRefresh() {
        update_scores();
    }

    public void onRefreshComplete() {
        mRefreshLayout.setRefreshing(false);
    }

    // onRefreshComplete() is called when the SyncAdapter forces a "change" in the sync status
    //  stored in SharedPreferences
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sp_sync_status_key))) {
            @Network.SyncStatus int status = Network.getSyncStatus(getActivity());
            if (status != Network.SYNC_STATUS_SYNCING) {
                onRefreshComplete();
            } else {
                mRefreshLayout.setRefreshing(true);
                updateEmptyView();
            }
        }
    }

    // Register the listeners here
    @Override public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    // Unregister the listeners here
    @Override public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
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

        // Preparing the recycler view layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View emptyView = rootView.findViewById(R.id.fragment_main_recyclerview_scores_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_recyclerview_scores);
        mScoresAdapter = new ScoresAdapter(getActivity(), emptyView);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(getResources().getInteger(R.integer.num_columns),
                        StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.setAdapter(mScoresAdapter);

        // Set the SwipeRefreshLayout
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_main_recyclerview_swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_FRAGMENT_DATE)) {
                setFragmentDate(savedInstanceState.getString(KEY_FRAGMENT_DATE));
            }
        }
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

    // Store the current position and the fragment date
    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FRAGMENT_DATE, fragmentdate[0]);
        outState.putInt(KEY_FRAGMENT_INDEX, FRAGMENT_INDEX);

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // The error "java.lang.IllegalArgumentException: column 'x' does not exist" just means
        //  x is not part of projection.
        String[] projection = {
                FixtureColumns._ID, FixtureColumns.DATE, FixtureColumns.TIME,
                FixtureColumns.STATUS, FixtureColumns.TEAMA_ID, FixtureColumns.TEAMB_ID,
                FixtureColumns.LEAGUEID, FixtureColumns.HOMEGOALS, FixtureColumns.AWAYGOALS,
                FixtureColumns.MATCHID, FixtureColumns.MATCHDAY,
                AteamColumns.NAME, AteamColumns.SHORTNAME, AteamColumns.CODE, AteamColumns.CRESTURL,
                BteamColumns.NAME, BteamColumns.SHORTNAME, BteamColumns.CODE, BteamColumns.CRESTURL
        };

        return new CursorLoader(getActivity(),
                (new FixtureSelection()).uri(),
                projection,
                FixtureColumns.DATE + " == ? ",
                fragmentdate,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        updateEmptyView();
        mScoresAdapter.swapCursor(new FixtureCursor(cursor));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }

    /**
     * Updates the empty view to display information pertaining to the status of the sync adapter
     */
    private void updateEmptyView() {
        if (mScoresAdapter.getItemCount() == 0) {
            TextView emptyView = (TextView) getView().findViewById(R.id.fragment_main_recyclerview_scores_empty);
            if (emptyView != null) {
                int message = R.string.empty_scores_list;
                @Network.SyncStatus int status = Network.getSyncStatus(getActivity());
                switch (status) {
                    case Network.SYNC_STATUS_SYNCING :
                        message = R.string.empty_scores_list_syncing;
                        break;
                    case Network.SYNC_STATUS_SERVER_DOWN :
                        message = R.string.empty_scores_list_server_down;
                        break;
                    case Network.SYNC_STATUS_SERVER_INVALID :
                        message = R.string.empty_scores_list_server_error;
                        break;
                    default:
                        if (!Network.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_scores_list_no_network;
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(R.string.status_no_internet), Toast.LENGTH_SHORT).show();
                            onRefreshComplete();
                        }
                }
                emptyView.setText(message);
                mScoresAdapter.notifyDataSetChanged();

                // TODO: Provide a refresh button to the widget. This does not work.
                Intent intent = new Intent(getActivity(), ScoresWidgetProvider.class);
                intent.setAction(ScoresWidgetProvider.ACTION_UPDATE_EMPTY_TEXT);
                intent.putExtra(ScoresWidgetProvider.ACTION_UPDATE_EMPTY_TEXT, message);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.e(LOG_TAG, "There was an error handling the pending intent.", e);
                }
            }
        }
    }
}
