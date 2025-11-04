package ru.pt.domain.lob;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LobCover {
    
    @JsonProperty("risks")
    private String risks;
    
    @JsonProperty("coverCode")
    private String coverCode;
    
    @JsonProperty("coverName")
    private String coverName;
    
    // Constructors
    public LobCover() {}
    
    public LobCover(String risks, String coverCode, String coverName) {
        this.risks = risks;
        this.coverCode = coverCode;
        this.coverName = coverName;
    }
    
    // Getters and Setters
    public String getRisks() {
        return risks;
    }
    
    public void setRisks(String risks) {
        this.risks = risks;
    }
    
    public String getCoverCode() {
        return coverCode;
    }
    
    public void setCoverCode(String coverCode) {
        this.coverCode = coverCode;
    }
    
    public String getCoverName() {
        return coverName;
    }
    
    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }
}
