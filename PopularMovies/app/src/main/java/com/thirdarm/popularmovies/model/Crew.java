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
public class Crew implements Parcelable {

    @SerializedName("credit_id")
    @Expose
    private String creditId;

    @Expose
    private String department;

    @Expose
    private Integer id;

    @Expose
    private String job;

    @Expose
    private String name;

    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    /**
     * @return The creditId
     */
    public String getCreditId() {
        return creditId;
    }

    /**
     * @param creditId The credit_id
     */
    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    /**
     * @return The department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department The department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The job
     */
    public String getJob() {
        return job;
    }

    /**
     * @param job The job
     */
    public void setJob(String job) {
        this.job = job;
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
     * @return The profilePath
     */
    public Object getProfilePath() {
        return profilePath;
    }

    /**
     * @param profilePath The profile_path
     */
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creditId);
        dest.writeString(this.department);
        dest.writeValue(this.id);
        dest.writeString(this.job);
        dest.writeString(this.name);
        dest.writeString(this.profilePath);
    }

    public Crew() {
    }

    protected Crew(Parcel in) {
        this.creditId = in.readString();
        this.department = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.job = in.readString();
        this.name = in.readString();
        this.profilePath = in.readString();
    }

    public static final Parcelable.Creator<Crew> CREATOR = new Parcelable.Creator<Crew>() {
        public Crew createFromParcel(Parcel source) {
            return new Crew(source);
        }

        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };
}