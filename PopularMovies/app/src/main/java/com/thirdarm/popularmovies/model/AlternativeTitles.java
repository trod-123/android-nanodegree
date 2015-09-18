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
public class AlternativeTitles implements Parcelable {

    @Expose
    private List<Title> titles = new ArrayList<>();

    /**
     * @return The titles
     */
    public List<Title> getTitles() {
        return titles;
    }

    /**
     * @param titles The titles
     */
    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.titles);
    }

    public AlternativeTitles() {
    }

    protected AlternativeTitles(Parcel in) {
        this.titles = new ArrayList<>();
        in.readList(this.titles, Title.class.getClassLoader());
    }

    public static final Parcelable.Creator<AlternativeTitles> CREATOR = new Parcelable.Creator<AlternativeTitles>() {
        public AlternativeTitles createFromParcel(Parcel source) {
            return new AlternativeTitles(source);
        }

        public AlternativeTitles[] newArray(int size) {
            return new AlternativeTitles[size];
        }
    };
}
