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
public class KeywordsResult implements Parcelable {

    @Expose
    private List<Keywords> keywords = new ArrayList<>();

    /**
     * @return The keywords
     */
    public List<Keywords> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords The keywords
     */
    public void setKeywords(List<Keywords> keywords) {
        this.keywords = keywords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(keywords);
    }

    public KeywordsResult() {
    }

    protected KeywordsResult(Parcel in) {
        this.keywords = in.createTypedArrayList(Keywords.CREATOR);
    }

    public static final Parcelable.Creator<KeywordsResult> CREATOR = new Parcelable.Creator<KeywordsResult>() {
        public KeywordsResult createFromParcel(Parcel source) {
            return new KeywordsResult(source);
        }

        public KeywordsResult[] newArray(int size) {
            return new KeywordsResult[size];
        }
    };
}