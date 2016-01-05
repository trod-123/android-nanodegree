package it.jaschke.alexandria.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class VolumeInfo {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("authors")
    @Expose
    private List<String> authors = new ArrayList<String>();
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("publishedDate")
    @Expose
    private String publishedDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("industryIdentifiers")
    @Expose
    private List<IndustryIdentifier> industryIdentifiers = new ArrayList<IndustryIdentifier>();
    @SerializedName("readingModes")
    @Expose
    private ReadingModes readingModes;
    @SerializedName("pageCount")
    @Expose
    private Integer pageCount;
    @SerializedName("printType")
    @Expose
    private String printType;
    @SerializedName("categories")
    @Expose
    private List<String> categories = new ArrayList<String>();
    @SerializedName("averageRating")
    @Expose
    private Double averageRating;
    @SerializedName("ratingsCount")
    @Expose
    private Integer ratingsCount;
    @SerializedName("maturityRating")
    @Expose
    private String maturityRating;
    @SerializedName("allowAnonLogging")
    @Expose
    private Boolean allowAnonLogging;
    @SerializedName("contentVersion")
    @Expose
    private String contentVersion;
    @SerializedName("imageLinks")
    @Expose
    private ImageLinks imageLinks;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("previewLink")
    @Expose
    private String previewLink;
    @SerializedName("infoLink")
    @Expose
    private String infoLink;
    @SerializedName("canonicalVolumeLink")
    @Expose
    private String canonicalVolumeLink;

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @param authors The authors
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     * @return The publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher The publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return The publishedDate
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * @param publishedDate The publishedDate
     */
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The industryIdentifiers
     */
    public List<IndustryIdentifier> getIndustryIdentifiers() {
        return industryIdentifiers;
    }

    /**
     * @param industryIdentifiers The industryIdentifiers
     */
    public void setIndustryIdentifiers(List<IndustryIdentifier> industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
    }

    /**
     * @return The readingModes
     */
    public ReadingModes getReadingModes() {
        return readingModes;
    }

    /**
     * @param readingModes The readingModes
     */
    public void setReadingModes(ReadingModes readingModes) {
        this.readingModes = readingModes;
    }

    /**
     * @return The pageCount
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * @param pageCount The pageCount
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * @return The printType
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * @param printType The printType
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * @return The categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * @return The averageRating
     */
    public Double getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating The averageRating
     */
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * @return The ratingsCount
     */
    public Integer getRatingsCount() {
        return ratingsCount;
    }

    /**
     * @param ratingsCount The ratingsCount
     */
    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    /**
     * @return The maturityRating
     */
    public String getMaturityRating() {
        return maturityRating;
    }

    /**
     * @param maturityRating The maturityRating
     */
    public void setMaturityRating(String maturityRating) {
        this.maturityRating = maturityRating;
    }

    /**
     * @return The allowAnonLogging
     */
    public Boolean getAllowAnonLogging() {
        return allowAnonLogging;
    }

    /**
     * @param allowAnonLogging The allowAnonLogging
     */
    public void setAllowAnonLogging(Boolean allowAnonLogging) {
        this.allowAnonLogging = allowAnonLogging;
    }

    /**
     * @return The contentVersion
     */
    public String getContentVersion() {
        return contentVersion;
    }

    /**
     * @param contentVersion The contentVersion
     */
    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    /**
     * @return The imageLinks
     */
    public ImageLinks getImageLinks() {
        return imageLinks;
    }

    /**
     * @param imageLinks The imageLinks
     */
    public void setImageLinks(ImageLinks imageLinks) {
        this.imageLinks = imageLinks;
    }

    /**
     * @return The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return The previewLink
     */
    public String getPreviewLink() {
        return previewLink;
    }

    /**
     * @param previewLink The previewLink
     */
    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    /**
     * @return The infoLink
     */
    public String getInfoLink() {
        return infoLink;
    }

    /**
     * @param infoLink The infoLink
     */
    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    /**
     * @return The canonicalVolumeLink
     */
    public String getCanonicalVolumeLink() {
        return canonicalVolumeLink;
    }

    /**
     * @param canonicalVolumeLink The canonicalVolumeLink
     */
    public void setCanonicalVolumeLink(String canonicalVolumeLink) {
        this.canonicalVolumeLink = canonicalVolumeLink;
    }
}