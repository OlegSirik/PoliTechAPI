package ru.pt.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "acc_account_tokens",
        uniqueConstraints = @UniqueConstraint(name = "acc_account_tokens_uk", columnNames = {"token", "client_id"}))
public class AccountTokenEntity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tid", nullable = false)
    private TenantEntity tenantEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity clientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;

    @Column(name = "token", nullable = false)
    private String token;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // constructors, getters, setters
    public AccountTokenEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TenantEntity getTenant() {
        return tenantEntity;
    }

    public void setTenant(TenantEntity tenantEntity) {
        this.tenantEntity = tenantEntity;
    }

    public ClientEntity getClient() {
        return clientEntity;
    }

    public void setClient(ClientEntity clientEntity) {
        this.clientEntity = clientEntity;
    }

    public AccountEntity getAccount() {
        return accountEntity;
    }

    public void setAccount(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}