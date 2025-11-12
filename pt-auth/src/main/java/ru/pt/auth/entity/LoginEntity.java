package ru.pt.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acc_logins",
        uniqueConstraints = @UniqueConstraint(name = "acc_logins_uk", columnNames = {"user_login", "tid"}))
public class LoginEntity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tid", nullable = false)
    private TenantEntity tenantEntity;

    @Column(name = "user_login", nullable = false)
    private String userLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loginEntity", cascade = CascadeType.ALL)
    private List<AccountLoginEntity> accountLoginEntities = new ArrayList<>();

    // constructors, getters, setters
    public LoginEntity() {}

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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
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

    public List<AccountLoginEntity> getAccountLogins() {
        return accountLoginEntities;
    }

    public void setAccountLogins(List<AccountLoginEntity> accountLoginEntities) {
        this.accountLoginEntities = accountLoginEntities;
    }
}