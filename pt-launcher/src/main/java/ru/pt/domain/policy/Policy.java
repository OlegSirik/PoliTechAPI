package ru.pt.domain.policy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Policy {

    @JsonProperty("issueDate")
    private OffsetDateTime issueDate;

    @JsonProperty("startDate")
    private OffsetDateTime startDate;

    @JsonProperty("endDate")
    private OffsetDateTime endDate;
  
    @JsonProperty("insuredObject")
    private InsuredObject insuredObject;

    @JsonProperty("waitingPeriod")
    private String waitingPeriod;

    @JsonProperty("policyTerm")
    private String policyTerm;

    @JsonProperty("policyNumber")
    private String policyNumber;

    @JsonProperty("premium")
    private Double premium;
    
    // Constructors
    public Policy() {}

    public Policy(OffsetDateTime startDate, OffsetDateTime endDate, InsuredObject insuredObject) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.insuredObject = insuredObject;
    }

    // JSON mapping methods
    public static Policy fromJson(JsonNode node, ObjectMapper mapper) {
        if (node == null) return null;
        return mapper.convertValue(node, Policy.class);
    }

    public ObjectNode toJson(ObjectMapper mapper) {
        return mapper.valueToTree(this);
    }

    // Getters and Setters  
    public OffsetDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(OffsetDateTime issueDate) {
        this.issueDate = issueDate;
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

    public InsuredObject getInsuredObject() {
        return insuredObject;
    }

    public void setInsuredObject(InsuredObject insuredObject) {
        this.insuredObject = insuredObject;
    }

    public String getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(String waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public String getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(String policyTerm) {
        this.policyTerm = policyTerm;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }
}
