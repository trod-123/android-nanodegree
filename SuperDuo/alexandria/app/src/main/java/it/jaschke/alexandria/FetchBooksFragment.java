package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.jaschke.alexandria.api.APIHelper;
import it.jaschke.alexandria.api.APIService;
import it.jaschke.alexandria.model.Volume;

/**
 * Created by TROD on 20160104.
 */
public class FetchBooksFragment extends Fragment {

    private static final String LOG_TAG = FetchBooksFragment.class.getSimpleName();

    private EditText mSearchField;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private FetchAdapter mFetchAdapter;
    private List<Volume> mVolumeList;

    // For the saveInstanceState
    private static final String SIS_QUERY = "query";

    private static final int RC_BARCODE_CAPTURE = 1001;

    public FetchBooksFragment(){
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

        // Initialize recycler view
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fetch_books_recyclerview);
        mFetchAdapter = new FetchAdapter(getContext(), new FetchAdapter.FetchAdapterOnClickHandler() {
            @Override
            public void onClick(int position, FetchAdapter.ViewHolder holder) {
                Log.d(LOG_TAG, "View " + position + " has been clicked!!!");
            }
        }, emptyView);
        mFetchAdapter.swapList(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFetchAdapter);

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
                        new FetchResultsTask(query, 0, APIService.PARAMS.SORT.RELEVANCE).execute();
                    }
                }, TIMER_DURATION);
            }
        });

        // Launch the camera
        mRootView.findViewById(R.id.fetch_books_button_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
//                    statusMessage.setText(R.string.barcode_success);
//                    barcodeValue.setText(barcode.displayValue);
                    ((EditText) mRootView.findViewById(R.id.fetch_books_search_field)).setText(barcode.displayValue);
                    Log.d(LOG_TAG, "Barcode read: " + barcode.displayValue);
                } else {
//                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(LOG_TAG, "No barcode captured, intent data is null");
                }
            } else {
//                statusMessage.setText(String.format(getString(R.string.barcode_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    private class FetchResultsTask extends AsyncTask<Void, Void, List<Volume>> {
        String mQuery;
        int mStartIndex;
        String mSort;

        public FetchResultsTask(String query, int startIndex, String sort) {
            mQuery = query;
            mStartIndex = startIndex;
            mSort = sort;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Volume> doInBackground(Void... params) {
            Log.d(LOG_TAG, "Fetching book...");

            APIHelper apiHelper = new APIHelper(getContext());

            // Fetch search results
            List<Volume> volumes = apiHelper.getSearchResults(mQuery, mStartIndex, mSort);
            return volumes;
        }

        @Override
        protected void onPostExecute(List<Volume> volumes) {
            Log.d(LOG_TAG, "Volumes list swapped to something that's filled");
            mFetchAdapter.swapList(volumes);
            // This is necessary to refresh the recycler view
            mFetchAdapter.notifyDataSetChanged();
        }
    }
}
