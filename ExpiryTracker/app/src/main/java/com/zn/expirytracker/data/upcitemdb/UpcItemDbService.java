package com.zn.expirytracker.data.upcitemdb;

import android.content.Context;
import android.util.Pair;

import com.zn.expirytracker.data.firebase.UserMetrics;
import com.zn.expirytracker.data.upcitemdb.model.UpcItem;
import com.zn.expirytracker.utils.Toolbox;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

/**
 * Uses {@link Retrofit} to interface with the UpcItemDb API
 * <p>
 * https://devs.upcitemdb.com/
 */
public final class UpcItemDbService {
    private static final String API_URL = "https://api.upcitemdb.com";
    private UpcItemDbApiEndpointInterface mDbApiService;
    private Context mContext;

    public interface UpcItemDbApiEndpointInterface {
        // https://api.upcitemdb.com/prod/trial/lookup?upc=
        @GET("prod/trial/lookup")
        Call<UpcItem> getBarcodeData(@Query("upc") String barcode);
    }

    /**
     * Creates a new REST adapter which points to the upcitemdb api
     *
     * @param context Required for showing user error messages
     */
    public UpcItemDbService(Context context) {
        mContext = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        Timber.tag(UpcItemDbService.class.getSimpleName());

        mDbApiService = retrofit.create(UpcItemDbApiEndpointInterface.class);
    }

    /**
     * Fetches information about the {@code barcode} from upcitemdb.com, as well as the
     * {@link ResponseCode} from the request. Can only be run on a worker thread
     * <p>
     * If returned response is invalid, returns {@code null}
     *
     * @param barcode
     * @return
     */
    public Pair<UpcItem, ResponseCode> fetchUpcItemInfo(String barcode) throws IOException {
        if (Toolbox.isNetworkAvailable(mContext)) {
            Call<UpcItem> call = mDbApiService.getBarcodeData(barcode);
            Response<UpcItem> response = call.execute();
            Timber.d("UpcItemDb: barcode=%s\ncode=%s\nmessage=%s\nheaders=%s", barcode,
                    response.code(), response.message(), response.headers().toString());
            UpcItem item = response.body();
            ResponseCode code = ResponseCode.fromInt(response.code());
            UserMetrics.incrementApiCallCount();
            return new Pair<>(item, code);
        } else {
            return new Pair<>(null, ResponseCode.NO_INTERNET);
        }
    }

    /**
     * Response messages that can be returned as a result of querying UpcItemDb
     */
    public enum ResponseCode {
        /**
         * Code: 200
         * <p>
         * May not always be the best case. Check {@link UpcItem} if the Items list is empty
         */
        OK(200),
        /**
         * Code: 400
         */
        INVALID_QUERY(400),
        /**
         * Code: 404
         */
        NOT_FOUND(404),
        /**
         * Code: 429
         * <p>
         * Either the daily limit has been reached, or there are too many quick requests
         */
        EXCEED_LIMIT(429),
        /**
         * Code: 500
         */
        SERVER_ERR(500),
        /**
         * Code: default
         */
        OTHER(999),
        /**
         * No code
         */
        NO_INTERNET(503);

        private int code;

        ResponseCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static ResponseCode fromInt(int code) {
            switch (code) {
                case 200:
                    return OK;
                case 400:
                    return INVALID_QUERY;
                case 404:
                    return NOT_FOUND;
                case 429:
                    return EXCEED_LIMIT;
                case 500:
                    return SERVER_ERR;
                case 503:
                    return NO_INTERNET;
                default:
                    return OTHER;
            }
        }
    }
}
