package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20150913.
 */
public class Trailers implements Parcelable {

    @Expose
    private List<Object> quicktime = new ArrayList<Object>();

    @Expose
    private List<Youtube> youtube = new ArrayList<Youtube>();

    /**
     * @return The quicktime
     */
    public List<Object> getQuicktime() {
        return quicktime;
    }

    /**
     * @param quicktime The quicktime
     */
    public void setQuicktime(List<Object> quicktime) {
        this.quicktime = quicktime;
    }

    /**
     * @return The youtube
     */
    public List<Youtube> getYoutube() {
        return youtube;
    }

    /**
     * @param youtube The youtube
     */
    public void setYoutube(List<Youtube> youtube) {
        this.youtube = youtube;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.quicktime);
        dest.writeList(this.youtube);
    }

    public Trailers() {
    }

    protected Trailers(Parcel in) {
        this.quicktime = new ArrayList<Object>();
        in.readList(this.quicktime, List.class.getClassLoader());
        this.youtube = new ArrayList<Youtube>();
        in.readList(this.youtube, Youtube.class.getClassLoader());
    }

    public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
        public Trailers createFromParcel(Parcel source) {
            return new Trailers(source);
        }

        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}