package ru.pt.api.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.pt.api.dto.numbers.NumberGeneratorDescription;


import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVersionModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("lob")
    private String lob;

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("versionNo")
    private Integer versionNo;

    @JsonProperty("versionStatus")
    private String versionStatus;

    @JsonProperty("waitingPeriod")
    private PeriodRule waitingPeriod;

    @JsonProperty("policyTerm")
    private PeriodRule policyTerm;

    @JsonProperty("vars")
    private List<PvVar> vars;

    @JsonProperty("quoteValidator")
    private List<ValidatorRule> quoteValidator;

    @JsonProperty("saveValidator")
    private List<ValidatorRule> saveValidator;

    @JsonProperty("packages")
    private List<PvPackage> packages;

    @JsonProperty("numberGenerator")
    private NumberGeneratorDescription numberGeneratorDescription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PvPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<PvPackage> packages) {
        this.packages = packages;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public PeriodRule getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(PeriodRule policyTerm) {
        this.policyTerm = policyTerm;
    }

    public List<ValidatorRule> getSaveValidator() {
        return saveValidator;
    }

    public void setSaveValidator(List<ValidatorRule> saveValidator) {
        this.saveValidator = saveValidator;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }

    public List<ValidatorRule> getQuoteValidator() {
        return quoteValidator;
    }

    public void setQuoteValidator(List<ValidatorRule> quoteValidator) {
        this.quoteValidator = quoteValidator;
    }

    public PeriodRule getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(PeriodRule waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public NumberGeneratorDescription getNumberGeneratorDescription() {
        return numberGeneratorDescription;
    }

    public void setNumberGeneratorDescription(NumberGeneratorDescription numberGeneratorDescription) {
        this.numberGeneratorDescription = numberGeneratorDescription;
    }

    public List<PvVar> getVars() {
        return vars;
    }

    public void setVars(List<PvVar> vars) {
        this.vars = vars;
    }
}