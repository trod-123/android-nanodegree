package it.jaschke.alexandria.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class VolumeResults {

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
}