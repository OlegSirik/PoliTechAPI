package ru.pt.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.db.entity.PolicyIndexEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyIndexRepository extends JpaRepository<PolicyIndexEntity, UUID> {

    Optional<PolicyIndexEntity> findPolicyIndexEntityByPolicyNumber(String policyNumber);

    List<PolicyIndexEntity> findAllByClientAccountIdAndUserAccountId(Long clientAccountId, Long userAccountId);

}
