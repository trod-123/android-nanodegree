package com.thirdarm.popularmovies.API;

import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.MovieDBResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TROD on 20150910.
 *
 * Service used to collect and parse JSON data from a server
 * Used for both Asynchronous and Synchronous calls (for Retrofit 2.0)
 */
public interface APIService {

    // Discovery information
    // https://api.themoviedb.org/3/discover/movie?api_key=###&sort_by=popularity.desc
    @GET("discover/movie")
    Call<MovieDBResults> discover(@Query("api_key") String key,
                                  @Query("sort_by") String sort);

    // Individual movie information
    // https://api.themoviedb.org/3/movie/550?api_key=###&append_to_response=images,releases,trailers
    @GET("movie/{movie_ID}")
    Call<MovieDB> getMovieDetails(@Path("movie_ID") int id,
                                  @Query("api_key") String key,
                                  @Query("append_to_response") String appends);
}
