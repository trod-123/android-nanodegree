package it.jaschke.alexandria.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeResults;
import it.jaschke.alexandria.utilities.Network;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by TROD on 20160103.
 */
public class APIHelper {

    private final static String LOG_TAG = APIHelper.class.getSimpleName();

    private APIService api;
    private Context mContext;
    private String API_KEY;

    // For limiting null loops
    private static final int RECURSIVE_MAX = 10;
    private int mRecursiveCount = 0;

    public APIHelper(Context c) {
        mContext = c;
        API_KEY = c.getString(R.string.API_KEY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(APIService.class);
    }

    public List<Volume> getSearchResults(String searchQuery, int startIndex, String sort) {
        // Only loop a certain number of times
        if (mRecursiveCount == RECURSIVE_MAX) {
            mRecursiveCount = 0;
            Network.setSyncStatus(mContext, Network.SYNC_STATUS_SERVER_DOWN);
            return null;
        }
        // Ignore empty search queries
        if (searchQuery.length() == 0) {
            Network.setSyncStatus(mContext, Network.SYNC_STATUS_OK);
            return null;
        }
        Call<VolumeResults> response = api.getSearchResults(API_KEY, searchQuery, startIndex, sort);
        try {
            Response<VolumeResults> test = response.execute();
            VolumeResults volumes = test.body();
            if (volumes != null) {
                mRecursiveCount = 0;
                Network.setSyncStatus(mContext, Network.SYNC_STATUS_OK);
                return volumes.getVolumes();
            } else {
                mRecursiveCount++;
                return getSearchResults(searchQuery, startIndex, sort);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was an error fetching the search results", e);
            Network.setSyncStatus(mContext, Network.SYNC_STATUS_SERVER_INVALID);
            return null;
        }
    }

    public Volume getIndividualVolume(int id) {
        // Only loop a certain number of times
        if (mRecursiveCount == RECURSIVE_MAX) {
            mRecursiveCount = 0;
            Network.setSyncStatus(mContext, Network.SYNC_STATUS_SERVER_DOWN);
            return null;
        }
        Call<Volume> response = api.getIndividualVolume(id, API_KEY);
        try {
            Volume volume = response.execute().body();
            if (volume != null) {
                mRecursiveCount = 0;
                Network.setSyncStatus(mContext, Network.SYNC_STATUS_OK);
                return volume;
            } else {
                mRecursiveCount++;
                return getIndividualVolume(id);
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "There was an error fetching individual volume info", e);
            Network.setSyncStatus(mContext, Network.SYNC_STATUS_SERVER_INVALID);
            return null;
        }
    }
}
