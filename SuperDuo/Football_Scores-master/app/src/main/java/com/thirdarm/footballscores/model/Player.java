package com.thirdarm.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */
@Generated("org.jsonschema2pojo")
public class Player {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("jerseyNumber")
    @Expose
    private Integer jerseyNumber;
    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("contractUntil")
    @Expose
    private String contractUntil;
    @SerializedName("marketValue")
    @Expose
    private String marketValue;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return The jerseyNumber
     */
    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    /**
     * @param jerseyNumber The jerseyNumber
     */
    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    /**
     * @return The dateOfBirth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth The dateOfBirth
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return The nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * @param nationality The nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /**
     * @return The contractUntil
     */
    public String getContractUntil() {
        return contractUntil;
    }

    /**
     * @param contractUntil The contractUntil
     */
    public void setContractUntil(String contractUntil) {
        this.contractUntil = contractUntil;
    }

    /**
     * @return The marketValue
     */
    public String getMarketValue() {
        return marketValue;
    }

    /**
     * @param marketValue The marketValue
     */
    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }
}