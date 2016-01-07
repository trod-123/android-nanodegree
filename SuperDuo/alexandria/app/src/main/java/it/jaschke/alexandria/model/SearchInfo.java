package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SearchInfo implements Parcelable {

    @SerializedName("textSnippet")
    @Expose
    private String textSnippet;

    /**
     * @return The textSnippet
     */
    public String getTextSnippet() {
        return textSnippet;
    }

    /**
     * @param textSnippet The textSnippet
     */
    public void setTextSnippet(String textSnippet) {
        this.textSnippet = textSnippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.textSnippet);
    }

    public SearchInfo() {
    }

    protected SearchInfo(Parcel in) {
        this.textSnippet = in.readString();
    }

    public static final Parcelable.Creator<SearchInfo> CREATOR = new Parcelable.Creator<SearchInfo>() {
        public SearchInfo createFromParcel(Parcel source) {
            return new SearchInfo(source);
        }

        public SearchInfo[] newArray(int size) {
            return new SearchInfo[size];
        }
    };
}