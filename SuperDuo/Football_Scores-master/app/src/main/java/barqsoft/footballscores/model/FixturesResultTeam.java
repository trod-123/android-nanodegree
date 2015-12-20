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
public class FixturesResultTeam {

    @SerializedName("_links")
    @Expose
    private Link Links;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("fixtures")
    @Expose
    private List<Fixture> fixtures = new ArrayList<Fixture>();

    /**
     * @return The Links
     */
    public Link getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(Link Links) {
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
     * @return The fixtures
     */
    public List<Fixture> getFixtures() {
        return fixtures;
    }

    /**
     * @param fixtures The fixtures
     */
    public void setFixtures(List<Fixture> fixtures) {
        this.fixtures = fixtures;
    }
}
