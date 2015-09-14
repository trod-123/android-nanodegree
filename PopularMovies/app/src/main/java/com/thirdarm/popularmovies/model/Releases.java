package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20150913.
 */

public class Releases implements Parcelable {

    @Expose
    private List<Country> countries = new ArrayList<Country>();

    /**
     * @return The countries
     */
    public List<Country> getCountries() {
        return countries;
    }

    /**
     * @param countries The countries
     */
    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.countries);
    }

    public Releases() {
    }

    protected Releases(Parcel in) {
        this.countries = new ArrayList<Country>();
        in.readList(this.countries, Country.class.getClassLoader());
    }

    public static final Creator<Releases> CREATOR = new Creator<Releases>() {
        public Releases createFromParcel(Parcel source) {
            return new Releases(source);
        }

        public Releases[] newArray(int size) {
            return new Releases[size];
        }
    };
}