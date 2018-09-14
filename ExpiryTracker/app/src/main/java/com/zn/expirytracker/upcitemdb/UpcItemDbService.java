package com.zn.expirytracker.upcitemdb;

import android.content.Context;
import android.support.annotation.Nullable;

import com.zn.expirytracker.upcitemdb.model.UpcItem;
import com.zn.expirytracker.utils.Toolbox;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

/**
 * Uses {@link Retrofit} to interface with the UpcItemDb API
 */
public final class UpcItemDbService {
    private static final String API_URL = "https://api.upcitemdb.com";
    private static final String RESPONSE_OK = "OK";
    private UpcItemDbApiEndpointInterface mDbApiService;
    private Context mContext;

    public interface UpcItemDbApiEndpointInterface {
        // https://api.upcitemdb.com/prod/trial/lookup?upc=
        @GET("prod/trial/lookup")
        Call<UpcItem> getBarcodeData(@Query("upc") String barcode);
    }

    // TODO: Handle no internet cases

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
     * Fetches information about the {@code barcode} from upcitemdb.com. Can only be run on a
     * worker thread
     * <p>
     * If returned response is invalid, returns {@code null}
     *
     * @param barcode
     * @return
     */
    public UpcItem fetchUpcItemInfo(String barcode) throws IOException {
        Call<UpcItem> call = mDbApiService.getBarcodeData(barcode);
        UpcItem item = call.execute().body();
        return checkResponseOK(item) ? item : null;
    }

    /**
     * Fetches information about the {@code barcode} from upcitemdb.com, asynchronously
     *
     * @param barcode
     * @return
     */
    public void fetchUpcItemInfo(String barcode, Callback<UpcItem> callback) {
        Call<UpcItem> call = mDbApiService.getBarcodeData(barcode);
        call.enqueue(callback);
    }

    /**
     * Checks the response if it's valid. Logs and informs user if there is an error
     * <p>
     * TODO: Export strings into resources
     *
     * @param item
     * @return
     */
    private boolean checkResponseOK(@Nullable UpcItem item) {
        if (item == null) {
            Timber.e("UpcItemDb response was null");
            Toolbox.showToast(mContext, "There was no response.");
            return false;
        }
        String code = item.getCode();
        String message = item.getMessage();

        if (code.equals(RESPONSE_OK)) {
            Timber.d("UpcItemDb response was a success!");
            return true;
        } else {
            Timber.e("Error with UpcItemDb response: %s, %s", code, message);
            Toolbox.showToast(mContext, message);
            return false;
        }
    }
}
