package com.zn.expirytracker.data.upcitemdb.model;

import com.squareup.moshi.Json;

public class Offer {

    @Json(name = "merchant")
    private String merchant;
    @Json(name = "domain")
    private String domain;
    @Json(name = "title")
    private String title;
    @Json(name = "currency")
    private String currency;
    @Json(name = "list_price")
    private String listPrice;
    @Json(name = "price")
    private Double price;
    @Json(name = "shipping")
    private String shipping;
    @Json(name = "condition")
    private String condition;
    @Json(name = "availability")
    private String availability;
    @Json(name = "link")
    private String link;
    @Json(name = "updated_t")
    private Integer updatedT;

    /**
     * No args constructor for use in serialization
     */
    public Offer() {
    }

    /**
     * @param shipping
     * @param title
     * @param price
     * @param condition
     * @param listPrice
     * @param merchant
     * @param link
     * @param domain
     * @param updatedT
     * @param availability
     * @param currency
     */
    public Offer(String merchant, String domain, String title, String currency, String listPrice, Double price, String shipping, String condition, String availability, String link, Integer updatedT) {
        super();
        this.merchant = merchant;
        this.domain = domain;
        this.title = title;
        this.currency = currency;
        this.listPrice = listPrice;
        this.price = price;
        this.shipping = shipping;
        this.condition = condition;
        this.availability = availability;
        this.link = link;
        this.updatedT = updatedT;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getUpdatedT() {
        return updatedT;
    }

    public void setUpdatedT(Integer updatedT) {
        this.updatedT = updatedT;
    }

}