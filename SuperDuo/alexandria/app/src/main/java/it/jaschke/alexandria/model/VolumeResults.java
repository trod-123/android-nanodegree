package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class VolumeResults implements Parcelable {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("totalItems")
    @Expose
    private Integer totalItems;
    @SerializedName("items")
    @Expose
    private List<Volume> volumes = new ArrayList<Volume>();

    /**
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * @return The totalItems
     */
    public Integer getTotalItems() {
        return totalItems;
    }

    /**
     * @param totalItems The totalItems
     */
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * @return The volumes
     */
    public List<Volume> getVolumes() {
        return volumes;
    }

    /**
     * @param volumes The volumes
     */
    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kind);
        dest.writeValue(this.totalItems);
        dest.writeList(this.volumes);
    }

    public VolumeResults() {
    }

    protected VolumeResults(Parcel in) {
        this.kind = in.readString();
        this.totalItems = (Integer) in.readValue(Integer.class.getClassLoader());
        this.volumes = new ArrayList<Volume>();
        in.readList(this.volumes, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<VolumeResults> CREATOR = new Parcelable.Creator<VolumeResults>() {
        public VolumeResults createFromParcel(Parcel source) {
            return new VolumeResults(source);
        }

        public VolumeResults[] newArray(int size) {
            return new VolumeResults[size];
        }
    };
}