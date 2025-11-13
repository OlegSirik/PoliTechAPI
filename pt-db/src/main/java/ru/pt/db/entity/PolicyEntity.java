package ru.pt.db.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Entity for storing complete policy data as JSON
 */
@Entity
@Table(name = "policy_data")
public class PolicyEntity {

    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "policy", columnDefinition = "jsonb", nullable = false)
    private String policy;

    public PolicyEntity() {
    }

    public PolicyEntity(UUID id, String policy) {
        this.id = id;
        this.policy = policy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
