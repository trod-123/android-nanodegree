package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Volume implements Parcelable {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("selfLink")
    @Expose
    private String selfLink;
    @SerializedName("volumeInfo")
    @Expose
    private VolumeInfo volumeInfo;
    @SerializedName("saleInfo")
    @Expose
    private SaleInfo saleInfo;
    @SerializedName("accessInfo")
    @Expose
    private AccessInfo accessInfo;
    @SerializedName("searchInfo")
    @Expose
    private SearchInfo searchInfo;

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
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     * @param etag The etag
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * @return The selfLink
     */
    public String getSelfLink() {
        return selfLink;
    }

    /**
     * @param selfLink The selfLink
     */
    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * @return The volumeInfo
     */
    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    /**
     * @param volumeInfo The volumeInfo
     */
    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    /**
     * @return The saleInfo
     */
    public SaleInfo getSaleInfo() {
        return saleInfo;
    }

    /**
     * @param saleInfo The saleInfo
     */
    public void setSaleInfo(SaleInfo saleInfo) {
        this.saleInfo = saleInfo;
    }

    /**
     * @return The accessInfo
     */
    public AccessInfo getAccessInfo() {
        return accessInfo;
    }

    /**
     * @param accessInfo The accessInfo
     */
    public void setAccessInfo(AccessInfo accessInfo) {
        this.accessInfo = accessInfo;
    }

    /**
     * @return The searchInfo
     */
    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    /**
     * @param searchInfo The searchInfo
     */
    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kind);
        dest.writeString(this.id);
        dest.writeString(this.etag);
        dest.writeString(this.selfLink);
        dest.writeParcelable(this.volumeInfo, 0);
        dest.writeParcelable(this.saleInfo, flags);
        dest.writeParcelable(this.accessInfo, flags);
        dest.writeParcelable(this.searchInfo, flags);
    }

    public Volume() {
    }

    protected Volume(Parcel in) {
        this.kind = in.readString();
        this.id = in.readString();
        this.etag = in.readString();
        this.selfLink = in.readString();
        this.volumeInfo = in.readParcelable(VolumeInfo.class.getClassLoader());
        this.saleInfo = in.readParcelable(SaleInfo.class.getClassLoader());
        this.accessInfo = in.readParcelable(AccessInfo.class.getClassLoader());
        this.searchInfo = in.readParcelable(SearchInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<Volume> CREATOR = new Parcelable.Creator<Volume>() {
        public Volume createFromParcel(Parcel source) {
            return new Volume(source);
        }

        public Volume[] newArray(int size) {
            return new Volume[size];
        }
    };
}