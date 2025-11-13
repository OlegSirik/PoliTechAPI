package ru.pt.api.dto.auth;

import java.time.LocalDateTime;

public class ProductRole {
    private Long id;
    private Long tid;
    private Long clientId;
    private Long accountId;
    private Long roleProductId;
    private Long roleAccountId;
    private Boolean isDeleted;
    private Boolean canRead;
    private Boolean canQuote;
    private Boolean canPolicy;
    private Boolean canAddendum;
    private Boolean canCancel;
    private Boolean canProlongate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public ProductRole() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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