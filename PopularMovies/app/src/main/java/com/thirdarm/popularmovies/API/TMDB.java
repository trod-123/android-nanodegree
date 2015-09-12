package com.thirdarm.popularmovies.API;

import android.content.Context;
import android.util.Log;

import com.thirdarm.popularmovies.R;
import com.thirdarm.popularmovies.constant.URL;
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

    // Constants for interface method calls
    public final String[] methods = {
            "discover",
            "getNowPlaying",
            "getPopular",
            "getTopRated",
            "getUpcoming"
    };

    public static final String DISCOVER = "discover";
    public static final String GETLATEST = "getLatest";
    public static final String GETNOWPLAYING = "getNowPlaying";
    public static final String GETPOPULAR = "getPopular";
    public static final String GETTOPRATED = "getTopRated";
    public static final String GETUPCOMING = "getUpcoming";

    private final String LOG_TAG = "Movies/TMDB";

    private String API_KEY;
    private String LANGUAGE;
    private APIService api;

    private List<MovieDBResults.MovieDBResult> results;
    private int[] movieIDs;
    private ArrayList<MovieDB> movies = new ArrayList<>();

    // constructor
    public TMDB(String key, String language_code) {
        API_KEY = key;
        LANGUAGE = language_code;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(APIService.class);
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

    // General method for getting MovieDBResults (called by APIService methods below)
    public void getResults(Call<MovieDBResults> response) {
        clear();
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

    // Generate MovieDBResults for /discover
    public void discover(String sort) {
        getResults(api.discover(API_KEY, sort));
    }

    // Generate MovieDBResults for now playing movies
    public void getNowPlaying() {
        getResults(api.getNowPlaying(API_KEY, 1, LANGUAGE));
    }

    // Generate MovieDBResults for popular movies
    public void getPopular() {
        getResults(api.getPopular(API_KEY, 1, LANGUAGE));
    }

    // Generate MovieDBResults for top rated movies
    public void getTopRated() {
        getResults(api.getTopRated(API_KEY, 1, LANGUAGE));
    }

    // Generate MovieDBResults for upcoming movies
    public void getUpcoming() {
        getResults(api.getUpcoming(API_KEY, 1, LANGUAGE));
    }

    // Generate MovieDB object
    public void getMovieDetails(int id) {
        // allow id to be referenced in onResponse()
        final int i = id;

        Call<MovieDB> response = api.getMovieDetails(id, API_KEY, "images,releases,trailers");
        response.enqueue(new Callback<MovieDB>() {
            // Below methods occur on main thread
            @Override public void onResponse(Response<MovieDB> response) {
                // prevent null responses from being added to movies list by calling
                //  getMovieDetails(id) recursively until response is no longer null
                if (response.body() == null) {
                    //Log.d(LOG_TAG, "Movie " + i + " is null!!!!!!!!!!!");
                    getMovieDetails(i);
                } else {
                    movies.add(response.body());
                    //Log.d(LOG_TAG, "Movie " + i + " has really been added");
                }
            }

            @Override public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "getMovieDetails() failure");
            }
        });
    }

    // Clear MovieDBResults and movies lists for new search queries
    public void clear() {
        if (results != null && movies != null && movieIDs != null){
            results = null;
            movies = new ArrayList<>();
            movieIDs = null;
            Log.d(LOG_TAG, "Cleared MovieDB data objects");
        }
    }
}