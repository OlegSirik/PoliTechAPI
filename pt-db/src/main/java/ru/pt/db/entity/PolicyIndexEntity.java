package ru.pt.db.entity;

import jakarta.persistence.*;
import ru.pt.api.dto.db.PolicyStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity for policy index - provides fast search capabilities
 */
@Entity
@Table(name = "policy_index")
public class PolicyIndexEntity {

    @Id
    @Column(name = "id")
    private UUID policyId;

    @Column(name = "policy_nr", length = 30)
    private String policyNumber;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "top_version")
    private Boolean topVersion;

    @Column(name = "product_code", length = 30)
    private String productCode;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "issue_date")
    private OffsetDateTime issueDate;

    @Column(name = "issue_timezone", length = 50)
    private String issueTimezone;

    @Column(name = "payment_date")
    private OffsetDateTime paymentDate;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "user_account_id")
    private Long userAccountId;

    @Column(name = "client_account_id")
    private Long clientAccountId;

    @Column(name = "version_status", length = 30)
    private String versionStatus;

    @Column(name = "policy_status", length = 20)
    @Enumerated(EnumType.STRING)
    private PolicyStatus policyStatus;

    // Constructors
    public PolicyIndexEntity() {
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    // Getters and Setters
    public String getPolicyNr() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Boolean getTopVersion() {
        return topVersion;
    }

    public void setTopVersion(Boolean topVersion) {
        this.topVersion = topVersion;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public OffsetDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(OffsetDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueTimezone() {
        return issueTimezone;
    }

    public void setIssueTimezone(String issueTimezone) {
        this.issueTimezone = issueTimezone;
    }

    public OffsetDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(OffsetDateTime paymentDate) {
        this.paymentDate = paymentDate;
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

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Long getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(Long clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }
}