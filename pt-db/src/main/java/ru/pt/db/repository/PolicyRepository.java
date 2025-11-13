package ru.pt.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.db.entity.PolicyEntity;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<PolicyEntity, UUID> {
}
