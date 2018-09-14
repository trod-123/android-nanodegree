package com.zn.expirytracker.upcitemdb.model;

import com.squareup.moshi.Json;

import java.util.List;

public class UpcItem {

    @Json(name = "code")
    private String code;
    @Json(name = "total")
    private Integer total;
    @Json(name = "offset")
    private Integer offset;
    @Json(name = "items")
    private List<Item> items = null;

    @Json(name = "message")
    private String message;

    /**
     * No args constructor for use in serialization
     */
    public UpcItem() {
    }

    /**
     * @param total
     * @param items
     * @param code
     * @param offset
     * @param message
     */
    public UpcItem(String code, Integer total, Integer offset, List<Item> items, String message) {
        super();
        this.code = code;
        this.total = total;
        this.offset = offset;
        this.items = items;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
