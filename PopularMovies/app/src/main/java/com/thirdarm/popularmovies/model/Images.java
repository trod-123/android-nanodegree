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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20150913.
 *
 * POJO created using jsonschema2pojo (http://www.jsonschema2pojo.org/). May not work for all
 *  JSON data
 */
public class Images implements Parcelable {

    @Expose
    private List<Backdrop> backdrops = new ArrayList<>();

    @Expose
    private List<Poster> posters = new ArrayList<>();

    /**
     * @return The backdrops
     */
    public List<Backdrop> getBackdrops() {
        return backdrops;
    }

    /**
     * @param backdrops The backdrops
     */
    public void setBackdrops(List<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    /**
     * @return The posters
     */
    public List<Poster> getPosters() {
        return posters;
    }

    /**
     * @param posters The posters
     */
    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.backdrops);
        dest.writeList(this.posters);
    }

    public Images() {
    }

    protected Images(Parcel in) {
        this.backdrops = new ArrayList<>();
        in.readList(this.backdrops, Backdrop.class.getClassLoader());
        this.posters = new ArrayList<>();
        in.readList(this.posters, Poster.class.getClassLoader());
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        public Images createFromParcel(Parcel source) {
            return new Images(source);
        }

        public Images[] newArray(int size) {
            return new Images[size];
        }
    };
}