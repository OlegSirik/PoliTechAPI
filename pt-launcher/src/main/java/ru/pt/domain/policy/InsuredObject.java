package ru.pt.domain.policy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsuredObject {

    @JsonProperty("packageCode")
    private Integer packageCode;

    @JsonProperty("covers")
    private List<Cover> covers;

    @JsonProperty("objectId")
    private String objectId;

    // Constructors
    public InsuredObject() {}

    public InsuredObject(Integer packageCode, List<Cover> covers, String objectId) {
        this.packageCode = packageCode;
        this.covers = covers;
        this.objectId = objectId;
    }

    // Getters and Setters
    public Integer getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(Integer packageCode) {
        this.packageCode = packageCode;
    }

    public List<Cover> getCovers() {
        return covers;
    }

    public void setCovers(List<Cover> covers) {
        this.covers = covers;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
