package ru.pt.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.pt.domain.account.Account;
import ru.pt.domain.policydata.PolicyData;
import ru.pt.service.PolicyDataService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Sales Controller for managing policy data
 * Exposes PolicyDataService methods via REST API
 */
@RestController
@RequestMapping("/db")
public class DbController {

    private final PolicyDataService policyDataService;

    public DbController(PolicyDataService policyDataService) {
        this.policyDataService = policyDataService;
    }

    /**
     * Create a new policy
     * POST /sales/policies
     */
    @PostMapping("/policies")
    public ResponseEntity<PolicyData> createPolicy(@RequestBody String request) {
        // TODO: Get account from security context or request
        Account account = new Account();
        account.setId(15L);

        PolicyData created = policyDataService.createPolicy(account, request);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing policy
     * PUT /sales/policies/{id}
     */
    @PutMapping("/policies/{id}")
    public ResponseEntity<PolicyData> updatePolicy(@PathVariable("id") Long id, 
                                                    @RequestBody JsonNode policyData) {
        PolicyData updated = policyDataService.updatePolicy(id, policyData);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get policy by ID
     * GET /sales/policies/{id}
     */
    @GetMapping("/policies/{id}")
    public ResponseEntity<PolicyData> getPolicyById(@PathVariable("id") Long id) {
        PolicyData policy = policyDataService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    /**
     * Get policy by policy number
     * GET /sales/policies/by-number/{policyNumber}
     */
    @GetMapping("/policies/by-number/{policyNumber}")
    public ResponseEntity<PolicyData> getPolicyByNumber(@PathVariable("policyNumber") String policyNumber) {
        PolicyData policy = policyDataService.getPolicyByNumber(policyNumber);
        return ResponseEntity.ok(policy);
    }

    /**
     * Get all policies by user account ID
     * GET /sales/policies/by-account/{userAccountId}
     */
    @GetMapping("/policies/by-account/{userAccountId}")
    public ResponseEntity<List<PolicyData>> getPoliciesByUserAccountId(@PathVariable("userAccountId") Long userAccountId) {
        List<PolicyData> policies = policyDataService.getPoliciesByUserAccountId(userAccountId);
        return ResponseEntity.ok(policies);
    }

    /**
     * Mark policy as paid
     * POST /sales/policies/{policyNumber}/paid
     */
    @PostMapping("/policies/{policyNumber}/paid")
    public ResponseEntity<Void> markPolicyAsPaid(@PathVariable("policyNumber") String policyNumber,
                                                  @RequestBody PaymentRequest request) {
        OffsetDateTime paymentDate = request.getPaymentDate() != null 
            ? request.getPaymentDate() 
            : OffsetDateTime.now();
        
        policyDataService.policyStatusPaid(policyNumber, paymentDate);
        return ResponseEntity.ok().build();
    }


    /**
     * Request class for payment
     */
    public static class PaymentRequest {
        private OffsetDateTime paymentDate;

        public PaymentRequest() {
        }

        public OffsetDateTime getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(OffsetDateTime paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
}

