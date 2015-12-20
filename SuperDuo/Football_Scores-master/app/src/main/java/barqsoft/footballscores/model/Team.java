package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class Team {

    @SerializedName("_links")
    @Expose
    private TeamsLinks Links;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("squadMarketValue")
    @Expose
    private String squadMarketValue;
    @SerializedName("crestUrl")
    @Expose
    private String crestUrl;

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
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return The shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName The shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return The squadMarketValue
     */
    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    /**
     * @param squadMarketValue The squadMarketValue
     */
    public void setSquadMarketValue(String squadMarketValue) {
        this.squadMarketValue = squadMarketValue;
    }

    /**
     * @return The crestUrl
     */
    public String getCrestUrl() {
        return crestUrl;
    }

    /**
     * @param crestUrl The crestUrl
     */
    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
    }
}