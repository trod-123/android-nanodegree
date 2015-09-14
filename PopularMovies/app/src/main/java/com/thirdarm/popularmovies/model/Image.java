package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20150913.
 */
public class Image implements Parcelable {

    @Expose
    private List<Backdrop> backdrops = new ArrayList<Backdrop>();

    @Expose
    private List<Poster> posters = new ArrayList<Poster>();

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

    public Image() {
    }

    protected Image(Parcel in) {
        this.backdrops = new ArrayList<Backdrop>();
        in.readList(this.backdrops, Backdrop.class.getClassLoader());
        this.posters = new ArrayList<Poster>();
        in.readList(this.posters, Poster.class.getClassLoader());
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}