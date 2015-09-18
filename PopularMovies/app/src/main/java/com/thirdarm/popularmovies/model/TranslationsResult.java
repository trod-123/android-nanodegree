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
public class TranslationsResult implements Parcelable {

    @Expose
    private List<Translations> translationses = new ArrayList<>();

    /**
     * @return The translationses
     */
    public List<Translations> getTranslationses() {
        return translationses;
    }

    /**
     * @param translationses The translationses
     */
    public void setTranslationses(List<Translations> translationses) {
        this.translationses = translationses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(translationses);
    }

    public TranslationsResult() {
    }

    protected TranslationsResult(Parcel in) {
        this.translationses = in.createTypedArrayList(Translations.CREATOR);
    }

    public static final Parcelable.Creator<TranslationsResult> CREATOR = new Parcelable.Creator<TranslationsResult>() {
        public TranslationsResult createFromParcel(Parcel source) {
            return new TranslationsResult(source);
        }

        public TranslationsResult[] newArray(int size) {
            return new TranslationsResult[size];
        }
    };
}