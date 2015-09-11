package com.thirdarm.popularmovies.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Handles /discovery results JSON data
 *
 * POJO created using jsonschema2pojo (http://www.jsonschema2pojo.org/). May not work for all
 *  JSON data
 *
 * How it works:
 *  @Expose sets the value associated with the key, specified by the name of the variable
 *  @SerializedName sets the name of the key for which @Expose will pair the corresponding value
 *   (used for variable names that are different from the key that appears in JSON -
 *    the serialized name is the name of the key as it appears in JSON)
 */
public class MovieDBResults {

    @Expose
    private Integer page;

    @SerializedName("results")
    @Expose
    private List<MovieDBResult> movieDBResults = new ArrayList<>();

    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    /**
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return The movieDBResults
     */
    public List<MovieDBResult> getMovieDBResults() {
        return movieDBResults;
    }

    /**
     * @param movieDBResults The movieDBResults
     */
    public void setMovieDBResults(List<MovieDBResult> movieDBResults) {
        this.movieDBResults = movieDBResults;
    }

    /**
     * @return The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     * @param totalResults The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public class MovieDBResult {

        @Expose
        private Boolean adult;

        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;

        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = new ArrayList<>();

        @Expose
        private Integer id;

        @SerializedName("original_language")
        @Expose
        private String originalLanguage;

        @SerializedName("original_title")
        @Expose
        private String originalTitle;

        @Expose
        private String overview;

        @SerializedName("release_date")
        @Expose
        private String releaseDate;

        @SerializedName("poster_path")
        @Expose
        private String posterPath;

        @Expose
        private Double popularity;

        @Expose
        private String title;

        @Expose
        private Boolean video;

        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;

        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;

        /**
         * @return The adult
         */
        public Boolean getAdult() {
            return adult;
        }

        /**
         * @param adult The adult
         */
        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        /**
         * @return The backdropPath
         */
        public String getBackdropPath() {
            return backdropPath;
        }

        /**
         * @param backdropPath The backdrop_path
         */
        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        /**
         * @return The genreIds
         */
        public List<Integer> getGenreIds() {
            return genreIds;
        }

        /**
         * @param genreIds The genre_ids
         */
        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        /**
         * @return The id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * @return The originalLanguage
         */
        public String getOriginalLanguage() {
            return originalLanguage;
        }

        /**
         * @param originalLanguage The original_language
         */
        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        /**
         * @return The originalTitle
         */
        public String getOriginalTitle() {
            return originalTitle;
        }

        /**
         * @param originalTitle The original_title
         */
        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        /**
         * @return The overview
         */
        public String getOverview() {
            return overview;
        }

        /**
         * @param overview The overview
         */
        public void setOverview(String overview) {
            this.overview = overview;
        }

        /**
         * @return The releaseDate
         */
        public String getReleaseDate() {
            return releaseDate;
        }

        /**
         * @param releaseDate The release_date
         */
        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        /**
         * @return The posterPath
         */
        public String getPosterPath() {
            return posterPath;
        }

        /**
         * @param posterPath The poster_path
         */
        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        /**
         * @return The popularity
         */
        public Double getPopularity() {
            return popularity;
        }

        /**
         * @param popularity The popularity
         */
        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }

        /**
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return The video
         */
        public Boolean getVideo() {
            return video;
        }

        /**
         * @param video The video
         */
        public void setVideo(Boolean video) {
            this.video = video;
        }

        /**
         * @return The voteAverage
         */
        public Double getVoteAverage() {
            return voteAverage;
        }

        /**
         * @param voteAverage The vote_average
         */
        public void setVoteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
        }

        /**
         * @return The voteCount
         */
        public Integer getVoteCount() {
            return voteCount;
        }

        /**
         * @param voteCount The vote_count
         */
        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }
    }
}