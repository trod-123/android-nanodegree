package com.thirdarm.popularmovies.API;

import com.thirdarm.popularmovies.constant.PARAMS;
import com.thirdarm.popularmovies.model.MovieDB;
import com.thirdarm.popularmovies.model.Results;

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
    // https://api.themoviedb.org/3/discover/movie?api_key=###&sort_by=##&language=###&page=###
    @GET("discover/movie")
    Call<Results> discover(@Query(PARAMS.GLOBAL.API_KEY) String key,
                           @Query(PARAMS.DISCOVER.SORT_BY) String sort,
                           @Query(PARAMS.DISCOVER.LANGUAGE) String code,
                           @Query(PARAMS.DISCOVER.PAGE) int page);

    // Latest movie (refreshes everyday)
    // https://api.themoviedb.org/3/movie/latest?api_key=###&language=###
    @GET("movie/latest")
    Call<MovieDB> getLatest(@Query(PARAMS.GLOBAL.API_KEY) String key,
                            @Query(PARAMS.DISCOVER.LANGUAGE) String code);

    // Movies released this week (refreshes everyday)
    // https://api.themoviedb.org/3/movie/{category}?api_key=###&language=###&page=###
    @GET("movie/{category}")
    Call<Results> getResults(@Path("category") String category,
                             @Query(PARAMS.GLOBAL.API_KEY) String key,
                             @Query(PARAMS.DISCOVER.LANGUAGE) String code,
                             @Query(PARAMS.DISCOVER.PAGE) int page);

    // Individual movie information
    // https://api.themoviedb.org/3/movie/550?api_key=###&append_to_response=images,releases,trailers
    @GET("movie/{id}")
    Call<MovieDB> getMovieDetails(@Path("id") int id,
                                  @Query(PARAMS.GLOBAL.API_KEY) String key,
                                  @Query(PARAMS.DISCOVER.LANGUAGE) String code,
                                  @Query(PARAMS.MOVIE.APPEND_TO_RESPONSE) String appends);
}
