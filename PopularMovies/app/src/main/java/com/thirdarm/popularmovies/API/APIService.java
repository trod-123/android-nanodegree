package com.thirdarm.popularmovies.API;

import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResults;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TROD on 20150910.
 *
 * Service used to collect and parse JSON data from a server
 * Used for both Asynchronous and Synchronous calls (using Retrofit 2.0)
 */
public interface APIService {

    // Discovery information
    // https://api.themoviedb.org/3/discover/movie?api_key=###&sort_by=popularity.desc
    @GET("discover/movie")
    Call<MovieDBResults> discover(@Query(PARAMS.GLOBAL.API_KEY) String key,
                                  @Query(PARAMS.DISCOVER.SORT_BY) String sort);

    // Latest movie (refreshes everyday)
    // https://api.themoviedb.org/3/movie/latest?api_key=###
    @GET("movie/latest")
    Call<MovieDB> getLatest(@Query(PARAMS.GLOBAL.API_KEY) String key);

    // Movies released this week (refreshes everyday)
    // https://api.themoviedb.org/3/movie/now_playing?api_key=###
    @GET("movie/now_playing")
    Call<MovieDBResults> getNowPlaying(@Query(PARAMS.GLOBAL.API_KEY) String key,
                                       @Query(PARAMS.DISCOVER.PAGE) int page,
                                       @Query(PARAMS.DISCOVER.LANGUAGE) String code);

    // Most popular movies (refreshes everyday)
    // https://api.themoviedb.org/3/movie/popular?api_key=###
    @GET("movie/popular")
    Call<MovieDBResults> getPopular(@Query(PARAMS.GLOBAL.API_KEY) String key,
                                    @Query(PARAMS.DISCOVER.PAGE) int page,
                                    @Query(PARAMS.DISCOVER.LANGUAGE) String code);

    // Top rated movies (refreshes everyday)
    // https://api.themoviedb.org/3/movie/top_rated?api_key=###
    @GET("movie/top_rated")
    Call<MovieDBResults> getTopRated(@Query(PARAMS.GLOBAL.API_KEY) String key,
                                     @Query(PARAMS.DISCOVER.PAGE) int page,
                                     @Query(PARAMS.DISCOVER.LANGUAGE) String code);

    // Upcoming movies (refreshes everyday)
    // https://api.themoviedb.org/3/movie/upcoming?api_key=###
    @GET("movie/upcoming")
    Call<MovieDBResults> getUpcoming(@Query(PARAMS.GLOBAL.API_KEY) String key,
                                     @Query(PARAMS.DISCOVER.PAGE) int page,
                                     @Query(PARAMS.DISCOVER.LANGUAGE) String code);


    // Individual movie information
    // https://api.themoviedb.org/3/movie/550?api_key=###&append_to_response=images,releases,trailers
    @GET("movie/{id}")
    Call<MovieDB> getMovieDetails(@Path("id") int id,
                                  @Query(PARAMS.GLOBAL.API_KEY) String key,
                                  @Query(PARAMS.MOVIE.APPEND_TO_RESPONSE) String appends);
}
