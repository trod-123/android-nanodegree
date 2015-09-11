package com.thirdarm.popularmovies.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Handles /movie JSON data
 *
 * POJO created using jsonschema2pojo (http://www.jsonschema2pojo.org/). May not work for all
 *  JSON data
 */
public class MovieDB {

    @Expose
    private Boolean adult;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("belongs_to_collection")
    @Expose
    private Object belongsToCollection;

    @Expose
    private Integer budget;

    @SerializedName("genres")
    @Expose
    private List<MovieDBGenre> movieDBGenres = new ArrayList<>();

    @Expose
    private String homepage;

    @Expose
    private Integer id;

    @SerializedName("imdb_id")
    @Expose
    private String imdbId;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @Expose
    private String overview;

    @Expose
    private Double popularity;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("production_companies")
    @Expose
    private List<MovieDBProductionCompany> productionCompanies = new ArrayList<>();

    @SerializedName("production_countries")
    @Expose
    private List<MovieDBProductionCountry> productionCountries = new ArrayList<>();

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @Expose
    private Integer revenue;

    @Expose
    private Integer runtime;

    @SerializedName("spoken_languages")
    @Expose
    private List<MovieDBSpokenLanguage> movieDBSpokenLanguages = new ArrayList<>();

    @Expose
    private String status;

    @Expose
    private String tagline;

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

    @SerializedName("images")
    @Expose
    private MovieDBImages movieDBImages;

    @SerializedName("releases")
    @Expose
    private MovieDBReleases movieDBReleases;

    @SerializedName("trailers")
    @Expose
    private MovieDBTrailers movieDBTrailers;


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
     * @return The belongsToCollection
     */
    public Object getBelongsToCollection() {
        return belongsToCollection;
    }

    /**
     * @param belongsToCollection The belongs_to_collection
     */
    public void setBelongsToCollection(Object belongsToCollection) {
        this.belongsToCollection = belongsToCollection;
    }

    /**
     * @return The budget
     */
    public Integer getBudget() {
        return budget;
    }

    /**
     * @param budget The budget
     */
    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    /**
     * @return The movieDBGenres
     */
    public List<MovieDBGenre> getMovieDBGenres() {
        return movieDBGenres;
    }

    /**
     * @param movieDBGenres The movieDBGenres
     */
    public void setMovieDBGenres(List<MovieDBGenre> movieDBGenres) {
        this.movieDBGenres = movieDBGenres;
    }

    /**
     * @return The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * @param homepage The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
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
     * @return The imdbId
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     * @param imdbId The imdb_id
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
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
     * @return The productionCompanies
     */
    public List<MovieDBProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    /**
     * @param productionCompanies The production_companies
     */
    public void setProductionCompanies(List<MovieDBProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    /**
     * @return The productionCountries
     */
    public List<MovieDBProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    /**
     * @param productionCountries The production_countries
     */
    public void setProductionCountries(List<MovieDBProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
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
     * @return The revenue
     */
    public Integer getRevenue() {
        return revenue;
    }

    /**
     * @param revenue The revenue
     */
    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    /**
     * @return The runtime
     */
    public Integer getRuntime() {
        return runtime;
    }

    /**
     * @param runtime The runtime
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    /**
     * @return The movieDBSpokenLanguages
     */
    public List<MovieDBSpokenLanguage> getMovieDBSpokenLanguages() {
        return movieDBSpokenLanguages;
    }

    /**
     * @param movieDBSpokenLanguages The spoken_languages
     */
    public void setMovieDBSpokenLanguages(List<MovieDBSpokenLanguage> movieDBSpokenLanguages) {
        this.movieDBSpokenLanguages = movieDBSpokenLanguages;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The tagline
     */
    public String getTagline() {
        return tagline;
    }

    /**
     * @param tagline The tagline
     */
    public void setTagline(String tagline) {
        this.tagline = tagline;
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

    /**
     * @return The movieDBImages
     */
    public MovieDBImages getMovieDBImages() {
        return movieDBImages;
    }

    /**
     * @param movieDBImages The movieDBImages
     */
    public void setMovieDBImages(MovieDBImages movieDBImages) {
        this.movieDBImages = movieDBImages;
    }

    /**
     * @return The movieDBReleases
     */
    public MovieDBReleases getMovieDBReleases() {
        return movieDBReleases;
    }

    /**
     * @param movieDBReleases The movieDBReleases
     */
    public void setMovieDBReleases(MovieDBReleases movieDBReleases) {
        this.movieDBReleases = movieDBReleases;
    }

    /**
     * @return The movieDBTrailers
     */
    public MovieDBTrailers getMovieDBTrailers() {
        return movieDBTrailers;
    }

    /**
     * @param movieDBTrailers The movieDBTrailers
     */
    public void setMovieDBTrailers(MovieDBTrailers movieDBTrailers) {
        this.movieDBTrailers = movieDBTrailers;
    }

    public class MovieDBBackdrop {

        @SerializedName("aspect_ratio")
        @Expose
        private Double aspectRatio;

        @SerializedName("file_path")
        @Expose
        private String filePath;

        @Expose
        private Integer height;

        @SerializedName("iso_639_1")
        @Expose
        private String iso6391;

        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;

        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;

        @Expose
        private Integer width;


        /**
         * @return The aspectRatio
         */
        public Double getAspectRatio() {
            return aspectRatio;
        }

        /**
         * @param aspectRatio The aspect_ratio
         */
        public void setAspectRatio(Double aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        /**
         * @return The filePath
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * @param filePath The file_path
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

        /**
         * @return The iso6391
         */
        public String getIso6391() {
            return iso6391;
        }

        /**
         * @param iso6391 The iso_639_1
         */
        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
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

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }
    }

    public class MovieDBCountry {

        @Expose
        private String certification;

        @SerializedName("iso_3166_1")
        @Expose
        private String iso31661;

        @Expose
        private Boolean primary;

        @SerializedName("release_date")
        @Expose
        private String releaseDate;

        /**
         * @return The certification
         */
        public String getCertification() {
            return certification;
        }

        /**
         * @param certification The certification
         */
        public void setCertification(String certification) {
            this.certification = certification;
        }

        /**
         * @return The iso31661
         */
        public String getIso31661() {
            return iso31661;
        }

        /**
         * @param iso31661 The iso_3166_1
         */
        public void setIso31661(String iso31661) {
            this.iso31661 = iso31661;
        }

        /**
         * @return The primary
         */
        public Boolean getPrimary() {
            return primary;
        }

        /**
         * @param primary The primary
         */
        public void setPrimary(Boolean primary) {
            this.primary = primary;
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
    }

    public class MovieDBGenre {

        @Expose
        private Integer id;

        @Expose
        private String name;

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
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    public class MovieDBImages {

        @Expose
        private List<MovieDBBackdrop> movieDBBackdrops = new ArrayList<MovieDBBackdrop>();

        @Expose
        private List<MovieDBPoster> movieDBPosters = new ArrayList<MovieDBPoster>();

        /**
         * @return The movieDBBackdrops
         */
        public List<MovieDBBackdrop> getMovieDBBackdrops() {
            return movieDBBackdrops;
        }

        /**
         * @param movieDBBackdrops The movieDBBackdrops
         */
        public void setMovieDBBackdrops(List<MovieDBBackdrop> movieDBBackdrops) {
            this.movieDBBackdrops = movieDBBackdrops;
        }

        /**
         * @return The movieDBPosters
         */
        public List<MovieDBPoster> getMovieDBPosters() {
            return movieDBPosters;
        }

        /**
         * @param movieDBPosters The movieDBPosters
         */
        public void setMovieDBPosters(List<MovieDBPoster> movieDBPosters) {
            this.movieDBPosters = movieDBPosters;
        }
    }

    public class MovieDBPoster {

        @SerializedName("aspect_ratio")
        @Expose
        private Double aspectRatio;

        @SerializedName("file_path")
        @Expose
        private String filePath;

        @Expose
        private Integer height;

        @SerializedName("iso_639_1")
        @Expose
        private String iso6391;

        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;

        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;

        @Expose
        private Integer width;

        /**
         * @return The aspectRatio
         */
        public Double getAspectRatio() {
            return aspectRatio;
        }

        /**
         * @param aspectRatio The aspect_ratio
         */
        public void setAspectRatio(Double aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        /**
         * @return The filePath
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * @param filePath The file_path
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

        /**
         * @return The iso6391
         */
        public String getIso6391() {
            return iso6391;
        }

        /**
         * @param iso6391 The iso_639_1
         */
        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
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

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }
    }

    public class MovieDBProductionCompany {

        @Expose
        private String name;

        @Expose
        private Integer id;

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
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
    }

    public class MovieDBProductionCountry {

        @SerializedName("iso_3166_1")
        @Expose
        private String iso31661;

        @Expose
        private String name;

        /**
         * @return The iso31661
         */
        public String getIso31661() {
            return iso31661;
        }

        /**
         * @param iso31661 The iso_3166_1
         */
        public void setIso31661(String iso31661) {
            this.iso31661 = iso31661;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    public class MovieDBReleases {

        @Expose
        private List<MovieDBCountry> countries = new ArrayList<MovieDBCountry>();

        /**
         * @return The countries
         */
        public List<MovieDBCountry> getCountries() {
            return countries;
        }

        /**
         * @param countries The countries
         */
        public void setCountries(List<MovieDBCountry> countries) {
            this.countries = countries;
        }
    }

    public class MovieDBSpokenLanguage {

        @SerializedName("iso_639_1")
        @Expose
        private String iso6391;
        @Expose
        private String name;

        /**
         * @return The iso6391
         */
        public String getIso6391() {
            return iso6391;
        }

        /**
         * @param iso6391 The iso_639_1
         */
        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    public class MovieDBTrailers {

        @Expose
        private List<Object> quicktime = new ArrayList<Object>();

        @Expose
        private List<MovieDBYoutube> movieDBYoutube = new ArrayList<MovieDBYoutube>();

        /**
         * @return The quicktime
         */
        public List<Object> getQuicktime() {
            return quicktime;
        }

        /**
         * @param quicktime The quicktime
         */
        public void setQuicktime(List<Object> quicktime) {
            this.quicktime = quicktime;
        }

        /**
         * @return The movieDBYoutube
         */
        public List<MovieDBYoutube> getMovieDBYoutube() {
            return movieDBYoutube;
        }

        /**
         * @param movieDBYoutube The movieDBYoutube
         */
        public void setMovieDBYoutube(List<MovieDBYoutube> movieDBYoutube) {
            this.movieDBYoutube = movieDBYoutube;
        }
    }

    public class MovieDBYoutube {

        @Expose
        private String name;

        @Expose
        private String size;

        @Expose
        private String source;

        @Expose
        private String type;

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The size
         */
        public String getSize() {
            return size;
        }

        /**
         * @param size The size
         */
        public void setSize(String size) {
            this.size = size;
        }

        /**
         * @return The source
         */
        public String getSource() {
            return source;
        }

        /**
         * @param source The source
         */
        public void setSource(String source) {
            this.source = source;
        }

        /**
         * @return The type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type The type
         */
        public void setType(String type) {
            this.type = type;
        }
    }
}
