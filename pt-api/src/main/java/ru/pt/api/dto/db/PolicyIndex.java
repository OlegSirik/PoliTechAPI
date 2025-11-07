package ru.pt.api.dto.db;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Проекция договора
 * Связывает договор с версией и пользователем
 */
public class PolicyIndex {

    private UUID policyId;

    private String policyNumber;
    // TODO версия не по формату
    private Integer versionNo;

    private String productCode;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private Long userAccountId;

    private Long clientAccountId;

    private String versionStatus;

    private PolicyStatus policyStatus;

    public PolicyIndex() {
    }

    public PolicyIndex(UUID policyId, String policyNumber, Integer versionNo, String productCode, ZonedDateTime startDate, ZonedDateTime endDate, Long userAccountId, Long clientAccountId, String versionStatus, PolicyStatus policyStatus) {
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.versionNo = versionNo;
        this.productCode = productCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userAccountId = userAccountId;
        this.clientAccountId = clientAccountId;
        this.versionStatus = versionStatus;
        this.policyStatus = policyStatus;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public String getPolicyNumber() {
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
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

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

}
