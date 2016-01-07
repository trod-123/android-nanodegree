package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class ImageLinks implements Parcelable {

    @SerializedName("smallThumbnail")
    @Expose
    private String smallThumbnail;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    /**
     * @return The smallThumbnail
     */
    public String getSmallThumbnail() {
        return smallThumbnail;
    }

    /**
     * @param smallThumbnail The smallThumbnail
     */
    public void setSmallThumbnail(String smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    /**
     * @return The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.smallThumbnail);
        dest.writeString(this.thumbnail);
    }

    public ImageLinks() {
    }

    protected ImageLinks(Parcel in) {
        this.smallThumbnail = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<ImageLinks> CREATOR = new Parcelable.Creator<ImageLinks>() {
        public ImageLinks createFromParcel(Parcel source) {
            return new ImageLinks(source);
        }

        public ImageLinks[] newArray(int size) {
            return new ImageLinks[size];
        }
    };
}