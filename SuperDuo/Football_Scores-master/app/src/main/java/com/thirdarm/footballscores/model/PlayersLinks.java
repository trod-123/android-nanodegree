package com.thirdarm.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class PlayersLinks {

    @SerializedName("self")
    @Expose
    private Link self;
    @SerializedName("team")
    @Expose
    private Link team;

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
