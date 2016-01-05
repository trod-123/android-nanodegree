package it.jaschke.alexandria.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Volume {

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
}