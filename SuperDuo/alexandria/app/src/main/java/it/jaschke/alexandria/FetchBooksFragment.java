package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.jaschke.alexandria.api.APIHelper;
import it.jaschke.alexandria.api.APIService;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.utilities.Network;

/**
 * Created by TROD on 20160104.
 */
public class FetchBooksFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = FetchBooksFragment.class.getSimpleName();

    private EditText mSearchField;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private FetchAdapter mFetchAdapter;
    private ProgressBar mLoadingSpinner;
    private FloatingActionButton mScannerButton;
    private List<Volume> mVolumeList;

    // For the saveInstanceState
    private static final String SIS_QUERY = "query";

    private static final int RC_BARCODE_CAPTURE = 1001;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface ResultSelectionCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onResultItemSelected(Volume volume, boolean update, FetchAdapter.ViewHolder vh);
    }

    public FetchBooksFragment(){
    }

    public static FetchBooksFragment newInstance() {
        return new FetchBooksFragment();
    }


    /*
        When implementing SharedPreferences.OnSharedPreferenceChangeListener, it is necessary to
         register the listeners in onResume() and unregister them in onPause(), or else
         onSharedPreferenceChanged() will not be called, even though the SharedPreferences has
         been changed.
     */

    // Register the listeners here
    @Override public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_fetch);
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    // Unregister the listeners here
    @Override public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // onRefreshComplete() is called when the SyncAdapter forces a "change" in the sync status
    //  stored in SharedPreferences
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sp_sync_status_key))) {
                updateEmptyView();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSearchField != null) {
            outState.putString(SIS_QUERY, mSearchField.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fetch_books, container, false);
        View emptyView = mRootView.findViewById(R.id.fetch_books_empty);

        mSearchField = (EditText) mRootView.findViewById(R.id.fetch_books_search_field);
        mLoadingSpinner = (ProgressBar) mRootView.findViewById(R.id.fetch_books_progress_spinner);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fetch_books_recyclerview);
        mFetchAdapter = new FetchAdapter(getContext(), new FetchAdapter.FetchAdapterOnClickHandler() {
            // (3) This is the third click method that is called when user presses on a view.
            //      This calls the last method, which is the one hosted in the housing activity.
            @Override
            public void onClick(Volume volume, boolean update, FetchAdapter.ViewHolder holder) {
                ((ResultSelectionCallback) getActivity())
                        .onResultItemSelected(volume, update, holder);
            }
        }, emptyView);
        mFetchAdapter.swapList(null);
        if (((ViewGroup.MarginLayoutParams) mSearchField.getLayoutParams()).leftMargin > 0) {
            // Number of columns determined by sw and orientation (see res/values/integers)
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    getResources().getInteger(R.integer.num_card_columns),
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }        mRecyclerView.setAdapter(mFetchAdapter);

        // This is performed every time the text field value is changed
        // Use of timer to delay changed text event attributed to Marcus Pohls of futurestud.io
        //  (url: https://futurestud.io/blog/android-how-to-delay-changedtextevent-on-androids-edittext)
        mSearchField.addTextChangedListener(new TextWatcher() {
            private Timer timer;
            private static final int TIMER_DURATION = 600;

            // Called before text in EditText changes
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            // Called while text in EditText changes
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // User is currently typing: reset already started timer (if existing)
                if (timer != null) {
                    timer.cancel();
                }
            }

            // Called after text in EditText changes
            @Override
            public void afterTextChanged(final Editable s) {
                // User typed: start the timer. This allows the action below to be executed only
                //  within the temporal bounds set by the timer. The timer runs until the specified
                //  duration time (in ms). If the user continues to type, the timer is reset back
                //  to 0 and starts again.
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override public void run() {
                        // This is where actions are done
                        String query = s.toString();
                        new FetchResultsTask(getContext(), query, 0, APIService.PARAMS.SORT.RELEVANCE).execute();
                    }
                }, TIMER_DURATION);
            }
        });

        // Launch the camera
        mScannerButton = (FloatingActionButton) mRootView.findViewById(R.id.fetch_books_button_scan);
        mScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScannerButton.hide();
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        // Save the value currently in the search field for when the activity recreates itself
        if (savedInstanceState != null) {
            mSearchField.setText(savedInstanceState.getString(SIS_QUERY));
            mSearchField.setHint("");
        }

        return mRootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mScannerButton.show();
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    ((EditText) mRootView.findViewById(R.id.fetch_books_search_field)).setText(barcode.displayValue);
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class FetchResultsTask extends AsyncTask<Void, Void, List<Volume>> {
        Context mContext;
        String mQuery;
        int mStartIndex;
        String mSort;

        public FetchResultsTask(Context context, String query, int startIndex, String sort) {
            mContext = context;
            mQuery = query;
            mStartIndex = startIndex;
            mSort = sort;
        }

        @Override
        protected void onPreExecute() {
            mLoadingSpinner.post(new Runnable() {
                @Override
                public void run() {
                    mLoadingSpinner.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        protected List<Volume> doInBackground(Void... params) {
            APIHelper apiHelper = new APIHelper(mContext);

            // Fetch search results
            return apiHelper.getSearchResults(mQuery, mStartIndex, mSort);
        }

        @Override
        protected void onPostExecute(List<Volume> volumes) {
            mFetchAdapter.swapList(volumes);
            if (volumes == null || volumes.size() != 0) {
                // Update the empty view according to the last sync status recorded while getting
                //  search results in the APIHelper
                updateEmptyView();
            } else {
                // If there are no results, set the status and update the empty view to reflect that
                Network.setSyncStatus(getContext(), Network.SYNC_STATUS_NO_RESULTS);
            }
            // This is necessary to refresh the recycler view
            mFetchAdapter.notifyDataSetChanged();
            mLoadingSpinner.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the empty view to display information pertaining to the status of the search
     */
    private void updateEmptyView() {
        if (mFetchAdapter.getItemCount() == 0) {
            TextView emptyView = (TextView) mRootView.findViewById(R.id.fetch_books_empty);
            if (emptyView != null) {
                int message = R.string.empty_fetch_books_null_query;
                @Network.SyncStatus int status = Network.getSyncStatus(getActivity());
                switch (status) {
                    case Network.SYNC_STATUS_NO_RESULTS :
                        message = R.string.empty_fetch_books_volume_empty;
                        break;
                    case Network.SYNC_STATUS_SERVER_DOWN :
                        message = R.string.empty_fetch_books_server_down;
                        break;
                    case Network.SYNC_STATUS_SERVER_INVALID :
                        message = R.string.empty_fetch_books_server_invalid;
                        break;
                    default:
                        if (!Network.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_fetch_books_no_network;
                        }
                }
                emptyView.setText(message);
                mFetchAdapter.notifyDataSetChanged();
            }
        }
    }
}
