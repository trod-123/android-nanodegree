package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class TeamsLinks {

    @SerializedName("self")
    @Expose
    private Link self;
    @SerializedName("fixtures")
    @Expose
    private Link fixtures;
    @SerializedName("players")
    @Expose
    private Link players;

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
     * @return The players
     */
    public Link getPlayers() {
        return players;
    }

    /**
     * @param players The players
     */
    public void setPlayers(Link players) {
        this.players = players;
    }
}
