/*
 *  Copyright (C) 2015 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P1-2: Popular Movies
 *
 *  Currently for educational purposes only.
 */

package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TROD on 20150917.
 */
public class ListsResult implements Parcelable {

    @Expose
    private String description;
    @SerializedName("favorite_count")

    @Expose
    private Integer favoriteCount;

    @Expose
    private String id;

    @SerializedName("item_count")
    @Expose
    private Integer itemCount;

    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;

    @Expose
    private String name;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The favoriteCount
     */
    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * @param favoriteCount The favorite_count
     */
    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The itemCount
     */
    public Integer getItemCount() {
        return itemCount;
    }

    /**
     * @param itemCount The item_count
     */
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * @return The iso6391
     */
    public String getIso6391() {
        return iso6391;
    }

    /**
     * @param iso6391 The iso_639_1
     */
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
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
     * @return The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeValue(this.favoriteCount);
        dest.writeString(this.id);
        dest.writeValue(this.itemCount);
        dest.writeString(this.iso6391);
        dest.writeString(this.name);
        dest.writeString(this.posterPath);
    }

    public ListsResult() {
    }

    protected ListsResult(Parcel in) {
        this.description = in.readString();
        this.favoriteCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = in.readString();
        this.itemCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.iso6391 = in.readString();
        this.name = in.readString();
        this.posterPath = in.readString();
    }

    public static final Parcelable.Creator<ListsResult> CREATOR = new Parcelable.Creator<ListsResult>() {
        public ListsResult createFromParcel(Parcel source) {
            return new ListsResult(source);
        }

        public ListsResult[] newArray(int size) {
            return new ListsResult[size];
        }
    };
}