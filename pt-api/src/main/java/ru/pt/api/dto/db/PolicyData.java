package ru.pt.api.dto.db;

import java.util.Map;
import java.util.UUID;

/**
 * DTO для передачи между компонентами приложения
 */
public class PolicyData {
    // uuid-идентификатор полиса
    private UUID policyId;
    // номер полиса
    private String policyNumber;
    // статус полиса
    private PolicyStatus policyStatus;
    // данные по договору в формате ключ-значение
    private Map<String, String> parameterMap;
    // проекция полиса
    private PolicyIndex policyIndex;
    // полис в формате json
    private String policy;

    public PolicyData() {
    }

    public PolicyData(UUID policyId, String policyNumber, PolicyStatus policyStatus, Map<String, String> parameterMap, PolicyIndex policyIndex, String policy) {
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.policyStatus = policyStatus;
        this.parameterMap = parameterMap;
        this.policyIndex = policyIndex;
        this.policy = policy;
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

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public PolicyIndex getPolicyIndex() {
        return policyIndex;
    }

    public void setPolicyIndex(PolicyIndex policyIndex) {
        this.policyIndex = policyIndex;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
