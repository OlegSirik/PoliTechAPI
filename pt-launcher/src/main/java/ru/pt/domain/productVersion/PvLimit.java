package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PvLimit {
    
    @JsonProperty("premium")
    private Double premium;

    @JsonProperty("sumInsured")
    private Double sumInsured;

    public Double getPremium() { return premium; }
    public void setPremium(Double premium) { this.premium = premium; }
    public Double getSumInsured() { return sumInsured; }
    public void setSumInsured(Double sumInsured) { this.sumInsured = sumInsured; }
}
