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
public class PlayersResult {

    @SerializedName("_links")
    @Expose
    private PlayersLinks Links;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("players")
    @Expose
    private List<Player> players = new ArrayList<Player>();

    /**
     * @return The Links
     */
    public PlayersLinks getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(PlayersLinks Links) {
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
     * @return The players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param players The players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}