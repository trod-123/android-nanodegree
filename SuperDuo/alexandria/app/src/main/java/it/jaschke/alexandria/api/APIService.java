package it.jaschke.alexandria.api;

import java.util.ArrayList;

import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeResults;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TROD on 20160103.
 *
 * Service used to collect and parse JSON data from a server.
 * Used for both Asynchronous and Synchronous calls.
 *  (uses Retrofit 2.0, by Jake Wharton [Square, Inc., 2015]: http://square.github.io/retrofit/)
 */
public interface APIService {

    /*
     * Base url: "https://www.googleapis.com/books/v1
     * Format: https://www.googleapis.com/books/v1/{collectionName}/resourceId?parameters
     */

    String URL_BASE = "https://www.googleapis.com/books/v1/";

    class PARAMS {
        public static final String API_KEY = "key";
        public static final String SEARCH = "q";
        public static final String ID = "id";

        // This can provide a dynamic function. As user scrolls through, more and more results are
        //  fetched. Just like facebook posts, e-mails, twitter posts, etc. The simulation of an
        //  "endless" list.
        public static final String START_INDEX = "startIndex"; // first result is 0
        public static final String MAX_RESULTS = "maxResults"; // default is 10/page

        // User defined settings (to be saved in a sharedPreferences file)
        public class SORT {
            public static final String BASE = "orderBy";
            public static final String RELEVANCE = "relevance";
            public static final String NEWEST = "newest";
        }
    }


    /*
     *  API methods
     */

    /**
     * Gets volume search results
     *   e.g. https://www.googleapis.com/books/v1/volumes?q=flowers&key=yourAPIKey
     *   e.g. https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keyes
     *
     * @param key Api key
     * @param search Search field terms
     * @return
     */
    @GET("volumes")
    Call<VolumeResults> getSearchResults(@Query(PARAMS.API_KEY) String key,
                                         @Query(PARAMS.SEARCH) String search,
                                         @Query(PARAMS.START_INDEX) int id,
                                         @Query(PARAMS.SORT.BASE) String sort);

    /**
     * Gets individual volume information (most likely not needed)
     *   e.g. https://www.googleapis.com/books/v1/volumes/zyTCAlFPjgYC?key=yourAPIKey
     *
     * @param id Volume id
     * @param key Api key
     * @return
     */
    @GET("volumes/{id}")
    Call<Volume> getIndividualVolume(@Path(PARAMS.ID) int id,
                                     @Query(PARAMS.API_KEY) String key);
}
