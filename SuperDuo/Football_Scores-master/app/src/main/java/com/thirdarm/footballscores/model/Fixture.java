package com.thirdarm.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class Fixture {

    @SerializedName("_links")
    @Expose
    private FixturesLinks fixturesLinks;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("matchday")
    @Expose
    private Integer matchday;
    @SerializedName("homeTeamName")
    @Expose
    private String homeTeamName;
    @SerializedName("awayTeamName")
    @Expose
    private String awayTeamName;
    @SerializedName("result")
    @Expose
    private Result result;

    /**
     * @return The Links
     */
    public FixturesLinks getFixturesLinks() {
        return fixturesLinks;
    }

    /**
     * @param fixturesLinks The _links
     */
    public void setFixturesLinks(FixturesLinks fixturesLinks) {
        this.fixturesLinks = fixturesLinks;
    }

    /**
     * @returnThe date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
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
     * @return The matchday
     */
    public Integer getMatchday() {
        return matchday;
    }

    /**
     * @param matchday The matchday
     */
    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    /**
     * @return The homeTeamName
     */
    public String getHomeTeamName() {
        return homeTeamName;
    }

    /**
     * @param homeTeamName The homeTeamName
     */
    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    /**
     * @return The awayTeamName
     */
    public String getAwayTeamName() {
        return awayTeamName;
    }

    /**
     * @param awayTeamName The awayTeamName
     */
    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    /**
     * @return The result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result The result
     */
    public void setResult(Result result) {
        this.result = result;
    }


    @Generated("org.jsonschema2pojo")
    public class Result {

        @SerializedName("goalsHomeTeam")
        @Expose
        private Integer goalsHomeTeam;
        @SerializedName("goalsAwayTeam")
        @Expose
        private Integer goalsAwayTeam;

        /**
         * @return The goalsHomeTeam
         */
        public Integer getGoalsHomeTeam() {
            return goalsHomeTeam;
        }

        /**
         * @param goalsHomeTeam The goalsHomeTeam
         */
        public void setGoalsHomeTeam(Integer goalsHomeTeam) {
            this.goalsHomeTeam = goalsHomeTeam;
        }

        /**
         * @return The goalsAwayTeam
         */
        public Integer getGoalsAwayTeam() {
            return goalsAwayTeam;
        }

        /**
         * @param goalsAwayTeam The goalsAwayTeam
         */
        public void setGoalsAwayTeam(Integer goalsAwayTeam) {
            this.goalsAwayTeam = goalsAwayTeam;
        }
    }
}