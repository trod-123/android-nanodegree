package com.thirdarm.popularmovies.constant;

/**
 * Created by TROD on 20150911.
 *
 * Class of constants for url params
 */
public class PARAMS {

    public class GLOBAL {
        public static final String API_KEY = "api_key";
    }

    public class DISCOVER {
        public static final String CERTIFICATION_COUNTRY = "certification_country"; // ISO 3166-1
        public static final String CERTIFICATION = "certification";
        public static final String INCLUDE_ADULT = "include_adult"; // boolean
        public static final String INCLUDE_VIDEO = "include_video"; // boolean
        public static final String LANGUAGE = "language"; // ISO 639-1
        public static final String PAGE = "page"; // [1, 1000]
        public static final String PRIMARY_RELEASE_YEAR = "primary_release_year"; // year
        public static final String PRIMARY_RELEASE_DATE_GTE = "primary_release_date.gte";
        public static final String PRIMARY_RELEASE_DATE_LTE = "primary_release_date.lte";
        public static final String RELEASE_DATE_GTE = "release_date.gte";
        public static final String RELEASE_DATE_LTE = "release_date.lte";
        public static final String SORT_BY = "sort_by"; // see DISCOVER.SORT
        public static final String VOTE_COUNT_GTE = "vote_count.gte"; // int
        public static final String VOTE_COUNT_LTE = "vote_count.lte"; // int
        public static final String VOTE_AVERAGE_GTE = "vote_average.gte"; // float
        public static final String VOTE_AVERAGE_LTE = "vote_aveage.lte"; // float
        public static final String WITH_CAST = "with_cast"; // int - id of person
        public static final String WITH_CREW = "with_crew"; // int - id of person
        public static final String WITH_COMPANIES = "with_companies"; // int - id of company
        public static final String WITH_GENRES = "with_genres"; // int - id of genre
        public static final String WITH_KEYWORDS = "with_keywords"; // int - id of keyword
        public static final String WITH_PEOPLE = "with_people"; // int - id of person
        public static final String YEAR = "year"; // int (year)
    }

    public class MOVIE {
        public static final String LANGUAGE = "language";
        public static final String APPEND_TO_RESPONSE = "append_to_response";
    }
}
