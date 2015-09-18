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
public class Translations implements Parcelable {

    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;

    @Expose
    private String name;
    @SerializedName("english_name")

    @Expose
    private String englishName;

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
     * @return The englishName
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * @param englishName The english_name
     */
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iso6391);
        dest.writeString(this.name);
        dest.writeString(this.englishName);
    }

    public Translations() {
    }

    protected Translations(Parcel in) {
        this.iso6391 = in.readString();
        this.name = in.readString();
        this.englishName = in.readString();
    }

    public static final Parcelable.Creator<Translations> CREATOR = new Parcelable.Creator<Translations>() {
        public Translations createFromParcel(Parcel source) {
            return new Translations(source);
        }

        public Translations[] newArray(int size) {
            return new Translations[size];
        }
    };
}