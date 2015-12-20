package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class TeamsResult {

    @SerializedName("_links")
    @Expose
    private TeamsLinks Links;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("teams")
    @Expose
    private List<Team> teams = new ArrayList<Team>();

    /**
     * @return The Links
     */
    public TeamsLinks getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(TeamsLinks Links) {
        this.Links = Links;
    }

    /**
     * @return The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return The teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * @param teams The teams
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
