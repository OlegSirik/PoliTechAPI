package ru.pt.api.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO с авторизационной информацией
 * Предполагается заполнение после проверки токена в момент получения запроса
 */
public class UserData implements UserDetails {
    // тип аккаунта
    private AccountType accountType;
    // почта пользователя
    private String username;
    // список ролей
    private List<String> roles;
    // пока будем прокидывать токен для ролей в 'моменте'
    private String token;

    private Long accountId;

    private Long clientId;

    public UserData() {
    }

    public UserData(AccountType accountType, String username, List<String> roles, String token, Long accountId, Long clientId) {
        this.accountType = accountType;
        this.username = username;
        this.roles = roles;
        this.token = token;
        this.accountId = accountId;
        this.clientId = clientId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String getPassword() {
        return "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
