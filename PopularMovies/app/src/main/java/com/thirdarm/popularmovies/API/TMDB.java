package com.thirdarm.popularmovies.API;

import android.content.Context;
import android.util.Log;

import com.thirdarm.popularmovies.R;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResults;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by TROD on 20150910.
 *
 * Class used to call APIService with specified information
 */
public class TMDB {

    private final String LOG_TAG = "Movies/TMDB";
    private String API_KEY;
    private Retrofit retrofit;
    private APIService api;
    private List<MovieDBResults.MovieDBResult> results;
    private int[] movieIDs;
    private ArrayList<MovieDB> movies = new ArrayList<>();

    // constructor
    public TMDB(Context c, String key) {
        API_KEY = key;
        retrofit = new Retrofit.Builder()
                .baseUrl(c.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(APIService.class);
    }

    public APIService getAPI() {
        return api;
    }

    public String getKey() {
        return API_KEY;
    }

    public List<MovieDBResults.MovieDBResult> getResults() {
        return results;
    }

    public int[] getMovieIDs() {
        return movieIDs;
    }

    public ArrayList<MovieDB> getMovies() {
        return movies;
    }

    // generate MovieDBResults object
    public void discover(String sort) {
        Call<MovieDBResults> response = api.discover(API_KEY, sort);
        response.enqueue(new Callback<MovieDBResults>() {
            // Below methods occur on main thread
            @Override
            public void onResponse(Response<MovieDBResults> response) {

                // generate results
                results = response.body().getMovieDBResults();

                // generate movieIDs list
                movieIDs = new int[results.size()];
                for (int i = 0; i < movieIDs.length; i++) {
                    movieIDs[i] = results.get(i).getId();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("THE TMDB", "ON FAILURE");
            }
        });
    }

    // generate MovieDB object
    public void getMovieDetails(int id) {
        Call<MovieDB> response = api.getMovieDetails(id, API_KEY, "images,releases,trailers");
        response.enqueue(new Callback<MovieDB>() {
            // Below methods occur on main thread
            @Override public void onResponse(Response<MovieDB> response) {
                movies.add(response.body());
                Log.d(LOG_TAG, "Movie added to list");
            }

            @Override public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "getMovieDetails() failure");
            }
        });
    }

    //    public class LoadResults implements Runnable {
//
//        @Override
//        public void run() {
//            while (results == null) {
//                try {
//                    Thread.sleep(mDelay);
//                    Log.d(LOG_TAG, "A call");
//                } catch (InterruptedException e) {
//
//                }
//            }
//        }
//
//        public List<MovieDBResults.MovieDBResult> getResults() {
//            return results;
//        }
//    }
//
//    public List<MovieDBResults.MovieDBResult> getResults() {
//        LoadResults lr = new LoadResults();
//        new Thread(lr).start();
//    }
//
//    public class LoadResults extends AsyncTask<Void, Void, List<MovieDBResults.MovieDBResult>> {
//
//        @Override protected List<MovieDBResults.MovieDBResult> doInBackground(Void... params) {
//            while (results == null) {
//                try {
//                    Thread.sleep(mDelay);
//                    Log.d(LOG_TAG, "A call");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return results;
//        }
//
//        @Override protected void onPostExecute(List<MovieDBResults.MovieDBResult> result) {
//
//        }
//    }
}