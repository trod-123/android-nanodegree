package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class ReadingModes implements Parcelable {

    @SerializedName("text")
    @Expose
    private Boolean text;
    @SerializedName("image")
    @Expose
    private Boolean image;

    /**
     * @return The text
     */
    public Boolean getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(Boolean text) {
        this.text = text;
    }

    /**
     * @return The image
     */
    public Boolean getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(Boolean image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.text);
        dest.writeValue(this.image);
    }

    public ReadingModes() {
    }

    protected ReadingModes(Parcel in) {
        this.text = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.image = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<ReadingModes> CREATOR = new Parcelable.Creator<ReadingModes>() {
        public ReadingModes createFromParcel(Parcel source) {
            return new ReadingModes(source);
        }

        public ReadingModes[] newArray(int size) {
            return new ReadingModes[size];
        }
    };
}