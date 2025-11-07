/*
package ru.pt.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.service.db.StorageService;
import ru.pt.exception.BadRequestException;



import jakarta.transaction.Transactional;

*/
/**
 * Service for managing policy data and index
 * Хранение данных и доступ к ним.
 * Поиск по фио страхователя и номеру договора-драфта
 * возможно по объекту страхования
 * <p>
 * для поиска нужен account_idи его тип ( продавец или админ )
 * поиск по продавцу будет быстрее, так как поиск по одному портфелю.
 *//*


@Service
public class PolicyDataService {

    private final StorageService storageService;
    private final PolicyDataRepository policyDataRepository;
    private final PolicyIndexRepository policyIndexRepository;

    public PolicyDataService(StorageService storageService, PolicyDataRepository policyDataRepository,
                             PolicyIndexRepository policyIndexRepository) {
        this.storageService = storageService;

        this.policyDataRepository = policyDataRepository;
        this.policyIndexRepository = policyIndexRepository;
    }

    public PolicyData createPolicy(UserData userData, String policy, UUID uuid) {
        var version = versionResolver.getVersion(policy);

        return storageService.save(policy, userData, version, uuid);
    }

    */
/**
     * Update an existing policy
     * Updates both policy data and index
     *//*

    @Transactional
    public PolicyData updatePolicy(Long policyId, JsonNode policyData) {
        try {
            // Find existing policy
            PolicyData existingData = policyDataRepository.findById(policyId)
                    .orElseThrow(() -> new BadRequestException("Policy not found with id: " + policyId));

            // Update policy data
            existingData.setPolicy(policyData);
            policyDataRepository.save(existingData);

            // Find and update policy index
            PolicyIndex index = policyIndexRepository.findById(policyId)
                    .orElseThrow(() -> new BadRequestException("Policy index not found with id: " + policyId));

            // Extract fields from JSON using JsonPath
            DocumentContext ctx = JsonPath.parse(policyData.toString());

            try {
                index.setDraftId(ctx.read("$.draftId", String.class));
            } catch (Exception e) {
            }
            try {
                index.setPolicyNr(ctx.read("$.policyNumber", String.class));
            } catch (Exception e) {
            }
            try {
                index.setVersionNo(ctx.read("$.versionNo", Integer.class));
            } catch (Exception e) {
            }
            try {
                index.setTopVersion(ctx.read("$.topVersion", Boolean.class));
            } catch (Exception e) {
            }
            try {
                index.setProductCode(ctx.read("$.product.code", String.class));
            } catch (Exception e) {
            }
            try {
                String issueDateStr = ctx.read("$.issueDate", String.class);
                if (issueDateStr != null) {
                    index.setIssueDate(OffsetDateTime.parse(issueDateStr));
                }
            } catch (Exception e) {
            }
            try {
                index.setIssueTimezone(ctx.read("$.issueTimezone", String.class));
            } catch (Exception e) {
            }
            try {
                String paymentDateStr = ctx.read("$.paymentDate", String.class);
                if (paymentDateStr != null) {
                    index.setPaymentDate(OffsetDateTime.parse(paymentDateStr));
                }
            } catch (Exception e) {
            }
            try {
                String startDateStr = ctx.read("$.startDate", String.class);
                if (startDateStr != null) {
                    index.setStartDate(OffsetDateTime.parse(startDateStr));
                }
            } catch (Exception e) {
            }
            try {
                String endDateStr = ctx.read("$.endDate", String.class);
                if (endDateStr != null) {
                    index.setEndDate(OffsetDateTime.parse(endDateStr));
                }
            } catch (Exception e) {
            }
            try {
                index.setUserAccountId(ctx.read("$.userAccountId", Long.class));
            } catch (Exception e) {
            }
            //try { index.setPartnerAccountId(ctx.read("$.partnerAccountId", Long.class)); } catch (Exception e) {}
            try {
                index.setVersionStatus(ctx.read("$.versionStatus", String.class));
            } catch (Exception e) {
            }

            policyIndexRepository.save(index);

            return existingData;
        } catch (Exception e) {
            throw new BadRequestException("Error updating policy: " + e.getMessage());
        }
    }

    */
/**
     * Mark policy as paid
     * Updates policy status to PAID and sets payment date
     *//*

    @Transactional
    public void policyStatusPaid(String policyNumber, OffsetDateTime paymentDate) {
        try {
            // Find policy by policy number
            PolicyIndex index = policyIndexRepository.findByPolicyNr(policyNumber)
                    .orElseThrow(() -> new BadRequestException("Policy not found with number: " + policyNumber));

            // Update status and payment date
            index.setVersionStatus("PAID");
            index.setPaymentDate(paymentDate);

            policyIndexRepository.save(index);

            // Optionally update the policy data JSON as well
            PolicyData data = policyDataRepository.findById(index.getId())
                    .orElseThrow(() -> new BadRequestException("Policy data not found"));

            // Update JSON with new status and payment date
            ObjectNode policyJson = (ObjectNode) data.getPolicy();
            policyJson.put("versionStatus", "PAID");
            policyJson.put("paymentDate", paymentDate.toString());

            policyDataRepository.save(data);

        } catch (Exception e) {
            throw new BadRequestException("Error updating policy status: " + e.getMessage());
        }
    }

    */
/**
     * Get policy data by ID
     *//*

    public PolicyData getPolicyById(Long id) {
        return policyDataRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Policy not found with id: " + id));
    }

    */
/**
     * Get policy by policy number
     *//*

    public PolicyData getPolicyByNumber(String policyNumber) {
        PolicyIndex index = policyIndexRepository.findByPolicyNr(policyNumber)
                .orElseThrow(() -> new BadRequestException("Policy not found with number: " + policyNumber));
        return getPolicyById(index.getId());
    }

    */
/**
     * Get all policies by user account ID
     *//*

    public List<PolicyData> getPoliciesByUserAccountId(Long userAccountId) {
        List<PolicyIndex> indices = policyIndexRepository.findByUserAccountId(userAccountId);
        return indices.stream()
                .map(index -> getPolicyById(index.getId()))
                .collect(Collectors.toList());
    }


}
*/
