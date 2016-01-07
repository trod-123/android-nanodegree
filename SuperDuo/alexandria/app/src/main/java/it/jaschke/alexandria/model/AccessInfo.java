package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class AccessInfo implements Parcelable {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("viewability")
    @Expose
    private String viewability;
    @SerializedName("embeddable")
    @Expose
    private Boolean embeddable;
    @SerializedName("publicDomain")
    @Expose
    private Boolean publicDomain;
    @SerializedName("textToSpeechPermission")
    @Expose
    private String textToSpeechPermission;
    @SerializedName("epub")
    @Expose
    private Epub epub;
    @SerializedName("pdf")
    @Expose
    private Pdf pdf;
    @SerializedName("webReaderLink")
    @Expose
    private String webReaderLink;
    @SerializedName("accessViewStatus")
    @Expose
    private String accessViewStatus;
    @SerializedName("quoteSharingAllowed")
    @Expose
    private Boolean quoteSharingAllowed;

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The viewability
     */
    public String getViewability() {
        return viewability;
    }

    /**
     * @param viewability The viewability
     */
    public void setViewability(String viewability) {
        this.viewability = viewability;
    }

    /**
     * @return The embeddable
     */
    public Boolean getEmbeddable() {
        return embeddable;
    }

    /**
     * @param embeddable The embeddable
     */
    public void setEmbeddable(Boolean embeddable) {
        this.embeddable = embeddable;
    }

    /**
     * @return The publicDomain
     */
    public Boolean getPublicDomain() {
        return publicDomain;
    }

    /**
     * @param publicDomain The publicDomain
     */
    public void setPublicDomain(Boolean publicDomain) {
        this.publicDomain = publicDomain;
    }

    /**
     * @return The textToSpeechPermission
     */
    public String getTextToSpeechPermission() {
        return textToSpeechPermission;
    }

    /**
     * @param textToSpeechPermission The textToSpeechPermission
     */
    public void setTextToSpeechPermission(String textToSpeechPermission) {
        this.textToSpeechPermission = textToSpeechPermission;
    }

    /**
     * @return The epub
     */
    public Epub getEpub() {
        return epub;
    }

    /**
     * @param epub The epub
     */
    public void setEpub(Epub epub) {
        this.epub = epub;
    }

    /**
     * @return The pdf
     */
    public Pdf getPdf() {
        return pdf;
    }

    /**
     * @param pdf The pdf
     */
    public void setPdf(Pdf pdf) {
        this.pdf = pdf;
    }

    /**
     * @return The webReaderLink
     */
    public String getWebReaderLink() {
        return webReaderLink;
    }

    /**
     * @param webReaderLink The webReaderLink
     */
    public void setWebReaderLink(String webReaderLink) {
        this.webReaderLink = webReaderLink;
    }

    /**
     * @return The accessViewStatus
     */
    public String getAccessViewStatus() {
        return accessViewStatus;
    }

    /**
     * @param accessViewStatus The accessViewStatus
     */
    public void setAccessViewStatus(String accessViewStatus) {
        this.accessViewStatus = accessViewStatus;
    }

    /**
     * @return The quoteSharingAllowed
     */
    public Boolean getQuoteSharingAllowed() {
        return quoteSharingAllowed;
    }

    /**
     * @param quoteSharingAllowed The quoteSharingAllowed
     */
    public void setQuoteSharingAllowed(Boolean quoteSharingAllowed) {
        this.quoteSharingAllowed = quoteSharingAllowed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeString(this.viewability);
        dest.writeValue(this.embeddable);
        dest.writeValue(this.publicDomain);
        dest.writeString(this.textToSpeechPermission);
        dest.writeParcelable(this.epub, 0);
        dest.writeParcelable(this.pdf, 0);
        dest.writeString(this.webReaderLink);
        dest.writeString(this.accessViewStatus);
        dest.writeValue(this.quoteSharingAllowed);
    }

    public AccessInfo() {
    }

    protected AccessInfo(Parcel in) {
        this.country = in.readString();
        this.viewability = in.readString();
        this.embeddable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.publicDomain = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.textToSpeechPermission = in.readString();
        this.epub = in.readParcelable(Epub.class.getClassLoader());
        this.pdf = in.readParcelable(Pdf.class.getClassLoader());
        this.webReaderLink = in.readString();
        this.accessViewStatus = in.readString();
        this.quoteSharingAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AccessInfo> CREATOR = new Parcelable.Creator<AccessInfo>() {
        public AccessInfo createFromParcel(Parcel source) {
            return new AccessInfo(source);
        }

        public AccessInfo[] newArray(int size) {
            return new AccessInfo[size];
        }
    };
}