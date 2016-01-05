package it.jaschke.alexandria.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeResults;
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
        // Ignore empty search queries
        if (searchQuery.length() == 0) {
            return null;
        }
        Call<VolumeResults> response = api.getSearchResults(API_KEY, searchQuery, startIndex, sort);
        try {
            Response<VolumeResults> test = response.execute();
            Log.d(LOG_TAG, "In getSearchResults, the response code is: " + test.message());
            VolumeResults volumes = test.body();
            if (volumes != null) {
                Log.d(LOG_TAG, "The volumes is not null! Returned.");
                return volumes.getVolumes();
            } else {
                Log.d(LOG_TAG, "The volumes is null! Looping...");
                return getSearchResults(searchQuery, startIndex, sort);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was an error fetching the search results", e);
            return null;
        }
    }

    public Volume getIndividualVolume(int id) {
        Call<Volume> response = api.getIndividualVolume(id, API_KEY);
        try {
            Volume volume = response.execute().body();
            if (volume != null) {
                return volume;
            } else {
                return getIndividualVolume(id);
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "There was an error fetching individual volume info", e);
            return null;
        }
    }
}
