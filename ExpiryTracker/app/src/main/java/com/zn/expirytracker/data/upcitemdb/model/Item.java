package com.zn.expirytracker.data.upcitemdb.model;

import com.squareup.moshi.Json;

import java.util.List;

public class Item {

    @Json(name = "ean")
    private String ean;
    @Json(name = "title")
    private String title;
    @Json(name = "description")
    private String description;
    @Json(name = "upc")
    private String upc;
    @Json(name = "brand")
    private String brand;
    @Json(name = "model")
    private String model;
    @Json(name = "color")
    private String color;
    @Json(name = "size")
    private String size;
    @Json(name = "dimension")
    private String dimension;
    @Json(name = "weight")
    private String weight;
    @Json(name = "currency")
    private String currency;
    @Json(name = "lowest_recorded_price")
    private Double lowestRecordedPrice;
    @Json(name = "highest_recorded_price")
    private Double highestRecordedPrice;
    @Json(name = "images")
    private List<String> images = null;
    @Json(name = "offers")
    private List<Offer> offers = null;
    @Json(name = "asin")
    private String asin;
    @Json(name = "elid")
    private String elid;

    /**
     * No args constructor for use in serialization
     */
    public Item() {
    }

    /**
     * @param highestRecordedPrice
     * @param model
     * @param weight
     * @param asin
     * @param offers
     * @param elid
     * @param upc
     * @param size
     * @param currency
     * @param ean
     * @param title
     * @param dimension
     * @param color
     * @param lowestRecordedPrice
     * @param description
     * @param images
     * @param brand
     */
    public Item(String ean, String title, String description, String upc, String brand, String model, String color, String size, String dimension, String weight, String currency, Double lowestRecordedPrice, Double highestRecordedPrice, List<String> images, List<Offer> offers, String asin, String elid) {
        super();
        this.ean = ean;
        this.title = title;
        this.description = description;
        this.upc = upc;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.size = size;
        this.dimension = dimension;
        this.weight = weight;
        this.currency = currency;
        this.lowestRecordedPrice = lowestRecordedPrice;
        this.highestRecordedPrice = highestRecordedPrice;
        this.images = images;
        this.offers = offers;
        this.asin = asin;
        this.elid = elid;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getLowestRecordedPrice() {
        return lowestRecordedPrice;
    }

    public void setLowestRecordedPrice(Double lowestRecordedPrice) {
        this.lowestRecordedPrice = lowestRecordedPrice;
    }

    public Double getHighestRecordedPrice() {
        return highestRecordedPrice;
    }

    public void setHighestRecordedPrice(Double highestRecordedPrice) {
        this.highestRecordedPrice = highestRecordedPrice;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getElid() {
        return elid;
    }

    public void setElid(String elid) {
        this.elid = elid;
    }

}