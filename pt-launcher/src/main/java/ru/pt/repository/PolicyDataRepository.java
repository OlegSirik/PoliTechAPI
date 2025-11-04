package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.pt.domain.policydata.PolicyData;

/**
 * Repository for PolicyData entity
 */
public interface PolicyDataRepository extends JpaRepository<PolicyData, Long> {

    /**
     * Get next policy ID from sequence
     */
    @Query(value = "SELECT nextval('policy_seq')", nativeQuery = true)
    Long getNextPolicyId();
}


