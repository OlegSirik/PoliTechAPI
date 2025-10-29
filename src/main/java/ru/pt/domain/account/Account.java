package ru.pt.domain.account;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acc_accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "node_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AccountNodeType nodeType;

    @Column(name = "name", length = 250)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Self-referencing relationship for parent-child accounts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Account parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> children = new ArrayList<>();

    // Relationships with other entities
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductRole> productRoles = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountLogin> accountLogins = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountToken> accountTokens = new ArrayList<>();

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public AccountNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(AccountNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Account getParent() {
        return parent;
    }

    public void setParent(Account parent) {
        this.parent = parent;
    }

    public List<Account> getChildren() {
        return children;
    }

    public void setChildren(List<Account> children) {
        this.children = children;
    }

    public List<ProductRole> getProductRoles() {
        return productRoles;
    }

    public void setProductRoles(List<ProductRole> productRoles) {
        this.productRoles = productRoles;
    }

    public List<AccountLogin> getAccountLogins() {
        return accountLogins;
    }

    public void setAccountLogins(List<AccountLogin> accountLogins) {
        this.accountLogins = accountLogins;
    }

    public List<AccountToken> getAccountTokens() {
        return accountTokens;
    }

    public void setAccountTokens(List<AccountToken> accountTokens) {
        this.accountTokens = accountTokens;
    }
}