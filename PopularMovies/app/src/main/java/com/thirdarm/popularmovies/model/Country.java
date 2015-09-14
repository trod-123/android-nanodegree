package com.thirdarm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TROD on 20150913.
 */
public class Country implements Parcelable {

    @Expose
    private String certification;

    @SerializedName("iso_3166_1")
    @Expose
    private String iso31661;

    @Expose
    private Boolean primary;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    /**
     * @return The certification
     */
    public String getCertification() {
        return certification;
    }

    /**
     * @param certification The certification
     */
    public void setCertification(String certification) {
        this.certification = certification;
    }

    /**
     * @return The iso31661
     */
    public String getIso31661() {
        return iso31661;
    }

    /**
     * @param iso31661 The iso_3166_1
     */
    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    /**
     * @return The primary
     */
    public Boolean getPrimary() {
        return primary;
    }

    /**
     * @param primary The primary
     */
    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    /**
     * @return The releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate The release_date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.certification);
        dest.writeString(this.iso31661);
        dest.writeValue(this.primary);
        dest.writeString(this.releaseDate);
    }

    public Country() {
    }

    protected Country(Parcel in) {
        this.certification = in.readString();
        this.iso31661 = in.readString();
        this.primary = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.releaseDate = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}