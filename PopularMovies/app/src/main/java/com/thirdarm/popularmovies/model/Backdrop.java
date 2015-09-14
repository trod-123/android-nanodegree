package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TROD on 20150913.
 */
public class Backdrop implements Parcelable {

    @SerializedName("aspect_ratio")
    @Expose
    private Double aspectRatio;

    @SerializedName("file_path")
    @Expose
    private String filePath;

    @Expose
    private Integer height;

    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    @Expose
    private Integer width;


    /**
     * @return The aspectRatio
     */
    public Double getAspectRatio() {
        return aspectRatio;
    }

    /**
     * @param aspectRatio The aspect_ratio
     */
    public void setAspectRatio(Double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     * @return The filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath The file_path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

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
     * @return The voteAverage
     */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     * @param voteAverage The vote_average
     */
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     * @return The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     * @param voteCount The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    /**
     * @return The width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.aspectRatio);
        dest.writeString(this.filePath);
        dest.writeValue(this.height);
        dest.writeString(this.iso6391);
        dest.writeValue(this.voteAverage);
        dest.writeValue(this.voteCount);
        dest.writeValue(this.width);
    }

    public Backdrop() {
    }

    protected Backdrop(Parcel in) {
        this.aspectRatio = (Double) in.readValue(Double.class.getClassLoader());
        this.filePath = in.readString();
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.iso6391 = in.readString();
        this.voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        this.voteCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Backdrop> CREATOR = new Creator<Backdrop>() {
        public Backdrop createFromParcel(Parcel source) {
            return new Backdrop(source);
        }

        public Backdrop[] newArray(int size) {
            return new Backdrop[size];
        }
    };
}