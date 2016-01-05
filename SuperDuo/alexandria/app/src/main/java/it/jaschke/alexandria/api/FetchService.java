package it.jaschke.alexandria.api;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Volume;


/**
 * Intent service that handles fetching search results.
 */
public class FetchService extends IntentService {

    private final String LOG_TAG = FetchService.class.getSimpleName();

    // Accessing intent data
    public static final String FETCH_RESULTS = "it.jaschke.alexandria.services.action.FETCH_RESULTS";
    public static final String QUERY = "it.jaschke.alexandria.services.extra.QUERY";
    public static final String START_INDEX = "it.jaschke.alexandria.services.extra.START_INDEX";
    public static final String SORT = "it.jaschke.alexandria.services.extra.SORT";

    public FetchService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Only handle the intent if it contains the appropriate action
        if (intent != null && FETCH_RESULTS.equals(intent.getAction())) {
            String query = intent.getStringExtra(QUERY);
            int startIndex = intent.getIntExtra(START_INDEX, 0);
            String sort = intent.getStringExtra(SORT);
            fetchResults(query, startIndex, sort);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchResults(String query, int startIndex, String sort) {

        // TODO: Need to first check whether the given string is an ISBN or a search query
        // This query type is only going to be used in querying the databse if the book is already
        //  in it.
        // Search functions do not need to classify query type. ISBN searches can be performed
        //  and the books with those ISBNs will be returned in the results.

        // TODO: Add functionality or direction for users in making "special" search queries that,
        //  for example, narrows results based on author or title.

        // Fetch process
        // TODO: Do not make this a consuming process as it may be run each time the user modifies
        //  their search query. Modifying query per character really can make this expensive
        //  process.
        // TODO: ORRRRR just make it so that the user will have to click a button before the
        //  search process starts.

        Log.d(LOG_TAG, "Fetching book...");

        APIHelper apiHelper = new APIHelper(getApplicationContext());

        // Fetch search results
        List<Volume> volumes = apiHelper.getSearchResults(query, startIndex, sort);

    }
}