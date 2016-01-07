package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class IndustryIdentifier implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("identifier")
    @Expose
    private String identifier;

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier The identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.identifier);
    }

    public IndustryIdentifier() {
    }

    protected IndustryIdentifier(Parcel in) {
        this.type = in.readString();
        this.identifier = in.readString();
    }

    public static final Parcelable.Creator<IndustryIdentifier> CREATOR = new Parcelable.Creator<IndustryIdentifier>() {
        public IndustryIdentifier createFromParcel(Parcel source) {
            return new IndustryIdentifier(source);
        }

        public IndustryIdentifier[] newArray(int size) {
            return new IndustryIdentifier[size];
        }
    };
}