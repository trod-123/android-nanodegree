package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class SoccerseasonsLinks {

    @SerializedName("self")
    @Expose
    private Link self;
    @SerializedName("teams")
    @Expose
    private Link teams;
    @SerializedName("fixtures")
    @Expose
    private Link fixtures;
    @SerializedName("leagueTable")
    @Expose
    private Link leagueTable;

    /**
     * @return The self
     */
    public Link getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(Link self) {
        this.self = self;
    }

    /**
     * @return The teams
     */
    public Link getTeams() {
        return teams;
    }

    /**
     * @param teams The teams
     */
    public void setTeams(Link teams) {
        this.teams = teams;
    }

    /**
     * @return The fixtures
     */
    public Link getFixtures() {
        return fixtures;
    }

    /**
     * @param fixtures The fixtures
     */
    public void setFixtures(Link fixtures) {
        this.fixtures = fixtures;
    }

    /**
     * @return The leagueTable
     */
    public Link getLeagueTable() {
        return leagueTable;
    }

    /**
     * @param leagueTable The leagueTable
     */
    public void setLeagueTable(Link leagueTable) {
        this.leagueTable = leagueTable;
    }
}