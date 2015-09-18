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
public class Videos implements Parcelable {

    @Expose
    private List<VideosResult> videosResults = new ArrayList<>();

    /**
     * @return The videosResults
     */
    public List<VideosResult> getVideosResults() {
        return videosResults;
    }

    /**
     * @param videosResults The videosResults
     */
    public void setVideosResults(List<VideosResult> videosResults) {
        this.videosResults = videosResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.videosResults);
    }

    public Videos() {
    }

    protected Videos(Parcel in) {
        this.videosResults = new ArrayList<>();
        in.readList(this.videosResults, VideosResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        public Videos createFromParcel(Parcel source) {
            return new Videos(source);
        }

        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}