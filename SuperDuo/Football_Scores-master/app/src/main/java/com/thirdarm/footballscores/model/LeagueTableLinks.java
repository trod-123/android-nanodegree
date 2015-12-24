package com.thirdarm.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class LeagueTableLinks {

    @SerializedName("self")
    @Expose
    private Link self;
    @SerializedName("soccerseason")
    @Expose
    private Link soccerseason;

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
     * @return The soccerseason
     */
    public Link getSoccerseason() {
        return soccerseason;
    }

    /**
     * @param soccerseason The soccerseason
     */
    public void setSoccerseason(Link soccerseason) {
        this.soccerseason = soccerseason;
    }
}
