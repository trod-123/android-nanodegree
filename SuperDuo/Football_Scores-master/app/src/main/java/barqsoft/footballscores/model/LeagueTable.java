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
public class LeagueTable {

    @SerializedName("_links")
    @Expose
    private LeagueTableLinks Links;
    @SerializedName("leagueCaption")
    @Expose
    private String leagueCaption;
    @SerializedName("matchday")
    @Expose
    private Integer matchday;
    @SerializedName("standing")
    @Expose
    private List<Standing> standing = new ArrayList<Standing>();

    /**
     * @return The Links
     */
    public LeagueTableLinks getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(LeagueTableLinks Links) {
        this.Links = Links;
    }

    /**
     * @return The leagueCaption
     */
    public String getLeagueCaption() {
        return leagueCaption;
    }

    /**
     * @param leagueCaption The leagueCaption
     */
    public void setLeagueCaption(String leagueCaption) {
        this.leagueCaption = leagueCaption;
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
     * @return The standing
     */
    public List<Standing> getStanding() {
        return standing;
    }

    /**
     * @param standing The standing
     */
    public void setStanding(List<Standing> standing) {
        this.standing = standing;
    }
}