package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class FixturesLinks {

    @SerializedName("self")
    @Expose
    private Link linksSelf;
    @SerializedName("soccerseason")
    @Expose
    private Link linksSoccerseason;
    @SerializedName("homeTeam")
    @Expose
    private Link linksHomeTeam;
    @SerializedName("awayTeam")
    @Expose
    private Link linksAwayTeam;

    /**
     * @return The self
     */
    public Link getLinksSelf() {
        return linksSelf;
    }

    /**
     * @param linksSelf The self
     */
    public void setLinksSelf(Link linksSelf) {
        this.linksSelf = linksSelf;
    }

    /**
     * @return The soccerseason
     */
    public Link getLinksSoccerseason() {
        return linksSoccerseason;
    }

    /**
     * @param linksSoccerseason The soccerseason
     */
    public void setLinksSoccerseason(Link linksSoccerseason) {
        this.linksSoccerseason = linksSoccerseason;
    }

    /**
     * @return The homeTeam
     */
    public Link getLinksHomeTeam() {
        return linksHomeTeam;
    }

    /**
     * @param linksHomeTeam The homeTeam
     */
    public void setLinksHomeTeam(Link linksHomeTeam) {
        this.linksHomeTeam = linksHomeTeam;
    }

    /**
     * @return The awayTeam
     */
    public Link getLinksAwayTeam() {
        return linksAwayTeam;
    }

    /**
     * @param linksAwayTeam The awayTeam
     */
    public void setLinksAwayTeam(Link linksAwayTeam) {
        this.linksAwayTeam = linksAwayTeam;
    }
}