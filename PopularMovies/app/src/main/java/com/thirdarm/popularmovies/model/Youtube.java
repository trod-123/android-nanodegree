/*
 * Copyright (C) 2015 Teddy Rodriguez (TROD)
 *   email: cia.123trod@gmail.com
 *   github: TROD-123
 *
 * For Udacity's Android Developer Nanodegree
 * P1-2: Popular Movies
 *
 * Currently for educational purposes only.
 */

package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by TROD on 20150913.
 *
 * POJO created using jsonschema2pojo (http://www.jsonschema2pojo.org/). May not work for all
 *  JSON data
 */
public class Youtube implements Parcelable {

    @Expose
    private String name;

    @Expose
    private String size;

    @Expose
    private String source;

    @Expose
    private String type;

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
     * @return The size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return The source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.size);
        dest.writeString(this.source);
        dest.writeString(this.type);
    }

    public Youtube() {
    }

    protected Youtube(Parcel in) {
        this.name = in.readString();
        this.size = in.readString();
        this.source = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Youtube> CREATOR = new Creator<Youtube>() {
        public Youtube createFromParcel(Parcel source) {
            return new Youtube(source);
        }

        public Youtube[] newArray(int size) {
            return new Youtube[size];
        }
    };
}