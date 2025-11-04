package ru.pt.domain.policydata;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity for storing complete policy data as JSON
 */
@Entity
@Table(name = "policy_data")
public class PolicyData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_seq")
    @SequenceGenerator(name = "policy_seq", sequenceName = "policy_seq", allocationSize = 1)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "policy", columnDefinition = "jsonb", nullable = false)
    private JsonNode policy;

    // Constructors
    public PolicyData() {
    }

    public PolicyData(JsonNode policy) {
        this.policy = policy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JsonNode getPolicy() {
        return policy;
    }

    public void setPolicy(JsonNode policy) {
        this.policy = policy;
    }
}


