package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SaleInfo implements Parcelable {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("saleability")
    @Expose
    private String saleability;
    @SerializedName("isEbook")
    @Expose
    private Boolean isEbook;

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
     * @return The saleability
     */
    public String getSaleability() {
        return saleability;
    }

    /**
     * @param saleability The saleability
     */
    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    /**
     * @return The isEbook
     */
    public Boolean getIsEbook() {
        return isEbook;
    }

    /**
     * @param isEbook The isEbook
     */
    public void setIsEbook(Boolean isEbook) {
        this.isEbook = isEbook;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeString(this.saleability);
        dest.writeValue(this.isEbook);
    }

    public SaleInfo() {
    }

    protected SaleInfo(Parcel in) {
        this.country = in.readString();
        this.saleability = in.readString();
        this.isEbook = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SaleInfo> CREATOR = new Parcelable.Creator<SaleInfo>() {
        public SaleInfo createFromParcel(Parcel source) {
            return new SaleInfo(source);
        }

        public SaleInfo[] newArray(int size) {
            return new SaleInfo[size];
        }
    };
}