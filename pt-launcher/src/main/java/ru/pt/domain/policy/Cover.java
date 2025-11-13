package ru.pt.domain.policy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cover {

    @JsonProperty("cover")
    private CoverInfo cover;

    @JsonProperty("risk")
    private List<String> risk;

    @JsonProperty("startDate")
    private OffsetDateTime startDate;

    @JsonProperty("endDate")
    private OffsetDateTime endDate;

    @JsonProperty("sumInsured")
    private Double sumInsured;

    @JsonProperty("premium")
    private Double premium;

    @JsonProperty("deductibleType")
    private String deductibleType;

    @JsonProperty("deductible")
    private Double deductible;

    @JsonProperty("sumInsuredCur")
    private Double sumInsuredCur;

    @JsonProperty("premiumCur")
    private Double premiumCur;

    @JsonProperty("deductibleCur")
    private Double deductibleCur;

    @JsonProperty("deductiblePercent")
    private Double deductiblePercent;

    @JsonProperty("deductibleMin")
    private Double deductibleMin;

    @JsonProperty("deductibleUnit")
    private String deductibleUnit;

    @JsonProperty("deductibleSpecific")
    private String deductibleSpecific;

    // Constructors
    public Cover() {}

    public Cover(CoverInfo cover, List<String> risk, OffsetDateTime startDate, OffsetDateTime endDate, 
                  Double sumInsured, Double premium, String deductibleType, Double deductible,
                  Double sumInsuredCur, Double premiumCur, Double deductibleCur, 
                  Double deductiblePercent, Double deductibleMin, String deductibleUnit, 
                  String deductibleSpecific) {
        this.cover = cover;
        this.risk = risk;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sumInsured = sumInsured;
        this.premium = premium;
        this.deductibleType = deductibleType;
        this.deductible = deductible;
        this.sumInsuredCur = sumInsuredCur;
        this.premiumCur = premiumCur;
        this.deductibleCur = deductibleCur;
        this.deductiblePercent = deductiblePercent;
        this.deductibleMin = deductibleMin;
        this.deductibleUnit = deductibleUnit;
        this.deductibleSpecific = deductibleSpecific;
    }

    // Getters and Setters
    public CoverInfo getCover() {
        return cover;
    }

    public void setCover(CoverInfo cover) {
        this.cover = cover;
    }

    public List<String> getRisk() {
        return risk;
    }

    public void setRisk(List<String> risk) {
        this.risk = risk;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(Double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getDeductibleType() {
        return deductibleType;
    }

    public void setDeductibleType(String deductibleType) {
        this.deductibleType = deductibleType;
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
    }

    public Double getSumInsuredCur() {
        return sumInsuredCur;
    }

    public void setSumInsuredCur(Double sumInsuredCur) {
        this.sumInsuredCur = sumInsuredCur;
    }

    public Double getPremiumCur() {
        return premiumCur;
    }

    public void setPremiumCur(Double premiumCur) {
        this.premiumCur = premiumCur;
    }

    public Double getDeductibleCur() {
        return deductibleCur;
    }

    public void setDeductibleCur(Double deductibleCur) {
        this.deductibleCur = deductibleCur;
    }

    public Double getDeductiblePercent() {
        return deductiblePercent;
    }

    public void setDeductiblePercent(Double deductiblePercent) {
        this.deductiblePercent = deductiblePercent;
    }

    public Double getDeductibleMin() {
        return deductibleMin;
    }

    public void setDeductibleMin(Double deductibleMin) {
        this.deductibleMin = deductibleMin;
    }

    public String getDeductibleUnit() {
        return deductibleUnit;
    }

    public void setDeductibleUnit(String deductibleUnit) {
        this.deductibleUnit = deductibleUnit;
    }

    public String getDeductibleSpecific() {
        return deductibleSpecific;
    }

    public void setDeductibleSpecific(String deductibleSpecific) {
        this.deductibleSpecific = deductibleSpecific;
    }
}
