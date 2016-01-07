package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Pdf implements Parcelable {

    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;

    /**
     * @return The isAvailable
     */
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    /**
     * @param isAvailable The isAvailable
     */
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isAvailable);
    }

    public Pdf() {
    }

    protected Pdf(Parcel in) {
        this.isAvailable = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Pdf> CREATOR = new Parcelable.Creator<Pdf>() {
        public Pdf createFromParcel(Parcel source) {
            return new Pdf(source);
        }

        public Pdf[] newArray(int size) {
            return new Pdf[size];
        }
    };
}