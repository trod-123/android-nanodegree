package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */

@Generated("org.jsonschema2pojo")
public class Soccerseason {

    @SerializedName("_links")
    @Expose
    private SoccerseasonsLinks links;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("league")
    @Expose
    private String league;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("numberOfTeams")
    @Expose
    private Integer numberOfTeams;
    @SerializedName("numberOfGames")
    @Expose
    private Integer numberOfGames;
    @SerializedName("lastUpdated")
    @Expose
    private String lastUpdated;

    /**
     * @return The Links
     */
    public SoccerseasonsLinks getLinks() {
        return links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(SoccerseasonsLinks Links) {
        this.links = Links;
    }

    /**
     * @return The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption The caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return The league
     */
    public String getLeague() {
        return league;
    }

    /**
     * @param league The league
     */
    public void setLeague(String league) {
        this.league = league;
    }

    /**
     * @return The year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year The year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return The numberOfTeams
     */
    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    /**
     * @param numberOfTeams The numberOfTeams
     */
    public void setNumberOfTeams(Integer numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    /**
     * @return The numberOfGames
     */
    public Integer getNumberOfGames() {
        return numberOfGames;
    }

    /**
     * @param numberOfGames The numberOfGames
     */
    public void setNumberOfGames(Integer numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    /**
     * @return The lastUpdated
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated The lastUpdated
     */
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}