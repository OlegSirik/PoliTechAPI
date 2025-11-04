package ru.pt.domain.account;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "acc_product_roles", 
       uniqueConstraints = @UniqueConstraint(name = "acc_product_roles_uk", 
                                           columnNames = {"account_id", "role_product_id", "role_account_id"}))
public class ProductRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "role_product_id", nullable = false)
    private Long roleProductId;

    @Column(name = "role_account_id", nullable = false)
    private Long roleAccountId;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship with Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRoleProductId() {
        return roleProductId;
    }

    public void setRoleProductId(Long roleProductId) {
        this.roleProductId = roleProductId;
    }

    public Long getRoleAccountId() {
        return roleAccountId;
    }

    public void setRoleAccountId(Long roleAccountId) {
        this.roleAccountId = roleAccountId;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
