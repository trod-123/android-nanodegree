package com.zn.baking.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    private static final int CONNECT_TIMEOUT = 10; // seconds
    private static final int READ_TIMEOUT = 10; // seconds

    private static final OkHttpClient sClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build();

    /**
     * Downloads a file from requested url synchronously. Good if response body is smaller than 1 MiB
     * Source: https://github.com/square/okhttp/wiki/Recipes
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getStringResponseFromUrlSynchronously(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = sClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }

    /**
     * Downloads a file from requested url asynchronously. Good if response body is 1 MiB or larger
     * Source: https://github.com/square/okhttp/wiki/Recipes
     *
     * @param url
     * @param callback
     */
    public static void getStringResponseFromUrlAsynchronously(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        sClient.newCall(request).enqueue(callback);
    }
}
