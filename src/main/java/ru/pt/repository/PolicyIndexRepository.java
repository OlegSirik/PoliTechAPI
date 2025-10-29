package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.pt.domain.policydata.PolicyIndex;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PolicyIndex entity - provides search capabilities
 */
public interface PolicyIndexRepository extends JpaRepository<PolicyIndex, Long> {

    /**
     * Find policy by ID
     */
    Optional<PolicyIndex> findById(Long id);

    /**
     * Find policy by draft ID
     */
    Optional<PolicyIndex> findByDraftId(String draftId);

    /**
     * Find all policies by draft ID
     */
    List<PolicyIndex> findAllByDraftId(String draftId);

    /**
     * Find policy by policy number
     */
    Optional<PolicyIndex> findByPolicyNr(String policyNr);

    /**
     * Find all versions of a policy by policy number
     */
    @Query(
        value = " WITH RECURSIVE category_levels AS ( " +
            "  SELECT id, parent_id, 0 as lvl " +
            "    FROM acc_accounts " +
            "    WHERE id in (:accounts) " +
            "    UNION " +
            "    ALL SELECT c.id, c.parent_id, cl.lvl + 1 " +
            "    FROM acc_accounts c " +
            "    JOIN category_levels cl ON c.parent_id = cl.id  " +
            " )  " +
            " select pi.* from policy_index pi  " +
            " join category_levels cl on cl.id = pi.user_account_id " +
            " where pi.policy_nr = :policyNr ", 
        nativeQuery = true)
    List<PolicyIndex> findAllByPolicyNr(@Param("accounts") List<Long> accounts, @Param("policyNr") String policyNr);

    /**
     * Find top version of a policy by policy number
     */
    @Query("SELECT pi FROM PolicyIndex pi WHERE pi.policyNr = :policyNr AND pi.topVersion = true")
    Optional<PolicyIndex> findTopVersionByPolicyNr(@Param("policyNr") String policyNr);

    /**
     * Find all policies by user account ID
     */
    @Query("SELECT pi FROM PolicyIndex pi WHERE pi.userAccountId = :userAccountId ORDER BY pi.createDate DESC")
    List<PolicyIndex> findByUserAccountId(@Param("userAccountId") Long userAccountId);


}


