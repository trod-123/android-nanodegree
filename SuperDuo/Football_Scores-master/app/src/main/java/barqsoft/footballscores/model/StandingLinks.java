package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class StandingLinks {

    @SerializedName("team")
    @Expose
    private Link team;

    /**
     * @return The team
     */
    public Link getTeam() {
        return team;
    }

    /**
     * @param team The team
     */
    public void setTeam(Link team) {
        this.team = team;
    }
}