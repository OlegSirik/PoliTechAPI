package ru.pt.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "acc_product_roles",
        uniqueConstraints = @UniqueConstraint(name = "acc_product_roles_uk",
                columnNames = {"account_id", "role_product_id", "role_account_id"}))
public class ProductRoleEntity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tid", nullable = false)
    private TenantEntity tenantEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity clientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;

    @Column(name = "role_product_id", nullable = false)
    private Long roleProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_account_id", nullable = false)
    private AccountEntity roleAccountEntity;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "can_read")
    private Boolean canRead = false;

    @Column(name = "can_quote")
    private Boolean canQuote = false;

    @Column(name = "can_policy")
    private Boolean canPolicy = false;

    @Column(name = "can_addendum")
    private Boolean canAddendum = false;

    @Column(name = "can_cancel")
    private Boolean canCancel = false;

    @Column(name = "can_prolongate")
    private Boolean canProlongate = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // constructors, getters, setters
    public ProductRoleEntity() {
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

    public Long getRoleProductId() {
        return roleProductId;
    }

    public void setRoleProductId(Long roleProductId) {
        this.roleProductId = roleProductId;
    }

    public AccountEntity getRoleAccount() {
        return roleAccountEntity;
    }

    public void setRoleAccount(AccountEntity roleAccountEntity) {
        this.roleAccountEntity = roleAccountEntity;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanQuote() {
        return canQuote;
    }

    public void setCanQuote(Boolean canQuote) {
        this.canQuote = canQuote;
    }

    public Boolean getCanPolicy() {
        return canPolicy;
    }

    public void setCanPolicy(Boolean canPolicy) {
        this.canPolicy = canPolicy;
    }

    public Boolean getCanAddendum() {
        return canAddendum;
    }

    public void setCanAddendum(Boolean canAddendum) {
        this.canAddendum = canAddendum;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public Boolean getCanProlongate() {
        return canProlongate;
    }

    public void setCanProlongate(Boolean canProlongate) {
        this.canProlongate = canProlongate;
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
