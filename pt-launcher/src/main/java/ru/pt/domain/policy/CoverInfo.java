package ru.pt.domain.policy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoverInfo {

    @JsonProperty("code")
    private String code;

    @JsonProperty("option")
    private String option;

    @JsonProperty("description")
    private String description;

    // Constructors
    public CoverInfo() {}

    public CoverInfo(String code, String option, String description) {
        this.code = code;
        this.option = option;
        this.description = description;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
