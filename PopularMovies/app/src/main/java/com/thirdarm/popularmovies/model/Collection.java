package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TROD on 20150913.
 */
public class Collection implements Parcelable {

    @Expose
    private int id;

    @Expose
    private String name;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) { this.id = id; }

    /**
     * @return The name
     */
    public String getName() { return name; }

    /**
     * @param name The name
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return The posterPath
     */
    public String getPosterPath() { return posterPath; }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    /**
     * @return The backdropPath
     */
    public String getBackdropPath() { return backdropPath; }

    /**
     * @param backdropPath The backdrop_path
     */
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}