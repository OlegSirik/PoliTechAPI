package ru.pt.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.service.process.ProcessOrchestrator;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Only for storage operations! Assumes no additional business logic
 */
@RestController
@RequestMapping("/db")
public class DbController {

    private final ProcessOrchestrator processOrchestrator;

    public DbController(ProcessOrchestrator processOrchestrator) {
        this.processOrchestrator = processOrchestrator;
    }


    /**
     * Create a new policy
     * POST /sales/policies
     */
    @PostMapping("/policies")
    public ResponseEntity<PolicyData> createPolicy(@RequestBody String request) {
        return ResponseEntity.ok(processOrchestrator.createPolicy(request));
    }

    /**
     * Update an existing policy
     * PUT /sales/policies/{id}
     */
    @PutMapping("/policies/{policyNumber}")
    public ResponseEntity<PolicyData> updatePolicy(@PathVariable("policyNumber") String policyNumber,
                                                    @RequestBody String request) {
        PolicyData updated = processOrchestrator.updatePolicy(policyNumber, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get policy by ID
     * GET /sales/policies/{id}
     */
    @GetMapping("/policies/{id}")
    public ResponseEntity<PolicyData> getPolicyById(@PathVariable("id") UUID id) {
        PolicyData policy = processOrchestrator.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    /**
     * Get policy by policy number
     * GET /sales/policies/by-number/{policyNumber}
     */
    @GetMapping("/policies/by-number/{policyNumber}")
    public ResponseEntity<PolicyData> getPolicyByNumber(@PathVariable("policyNumber") String policyNumber) {
        PolicyData policy = processOrchestrator.getPolicyByNumber(policyNumber);
        return ResponseEntity.ok(policy);
    }

    /**
     * Get all policies by user account ID
     * GET /sales/policies/by-account/{userAccountId}
     */
    // TODO securityContext and token
    @GetMapping("/policies/by-account/{userAccountId}")
    public ResponseEntity<List<PolicyData>> getPoliciesByUserAccountId(@PathVariable("userAccountId") Long userAccountId) {
        // List<PolicyData> policies = policyDataService.getPoliciesByUserAccountId(userAccountId);
        return ResponseEntity.ok(null);
    }

    /**
     * Mark policy as paid
     * POST /sales/policies/{policyNumber}/paid
     * TODO не тут должно быть + не та dto
     */
    @PostMapping("/policies/{policyNumber}/paid")
    public ResponseEntity<Void> markPolicyAsPaid(@PathVariable("policyNumber") String policyNumber,
                                                  @RequestBody PaymentRequest request) {
        OffsetDateTime paymentDate = request.getPaymentDate() != null 
            ? request.getPaymentDate() 
            : OffsetDateTime.now();
        
        // policyDataService.policyStatusPaid(policyNumber, paymentDate);
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

