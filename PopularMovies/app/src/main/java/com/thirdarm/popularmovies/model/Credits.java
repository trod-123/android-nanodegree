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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20150917.
 */
public class Credits implements Parcelable {

    @Expose
    private List<Cast> cast = new ArrayList<>();

    @Expose
    private List<Crew> crew = new ArrayList<>();

    /**
     * @return The cast
     */
    public List<Cast> getCast() {
        return cast;
    }

    /**
     * @param cast The cast
     */
    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    /**
     * @return The crew
     */
    public List<Crew> getCrew() {
        return crew;
    }

    /**
     * @param crew The crew
     */
    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cast);
        dest.writeList(this.crew);
    }

    public Credits() {
    }

    protected Credits(Parcel in) {
        this.cast = in.createTypedArrayList(Cast.CREATOR);
        this.crew = new ArrayList<>();
        in.readList(this.crew, Crew.class.getClassLoader());
    }

    public static final Parcelable.Creator<Credits> CREATOR = new Parcelable.Creator<Credits>() {
        public Credits createFromParcel(Parcel source) {
            return new Credits(source);
        }

        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };
}