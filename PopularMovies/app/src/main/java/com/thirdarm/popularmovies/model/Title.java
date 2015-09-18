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
public class Title implements Parcelable {

    @SerializedName("iso_3166_1")
    @Expose
    private String iso31661;

    @Expose
    private String title;

    /**
     * @return The iso31661
     */
    public String getIso31661() {
        return iso31661;
    }

    /**
     * @param iso31661 The iso_3166_1
     */
    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iso31661);
        dest.writeString(this.title);
    }

    public Title() {
    }

    protected Title(Parcel in) {
        this.iso31661 = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Title> CREATOR = new Parcelable.Creator<Title>() {
        public Title createFromParcel(Parcel source) {
            return new Title(source);
        }

        public Title[] newArray(int size) {
            return new Title[size];
        }
    };
}
