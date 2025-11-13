package ru.pt.auth.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.AccountLogin;
import ru.pt.auth.entity.AccountEntity;
import ru.pt.auth.entity.AccountLoginEntity;
import ru.pt.auth.entity.ClientEntity;
import ru.pt.auth.entity.TenantEntity;

@Component
public class AccountLoginMapper {

    public AccountLoginEntity toEntity(AccountLogin dto) {
        if (dto == null) {
            return null;
        }

        AccountLoginEntity entity = new AccountLoginEntity();
        entity.setId(dto.getId());

        // Tenant
        if (dto.getTid() != null) {
            TenantEntity tenant = new TenantEntity();
            tenant.setId(dto.getTid());
            entity.setTenant(tenant);
        }

        // Client
        if (dto.getClientId() != null) {
            ClientEntity client = new ClientEntity();
            client.setId(dto.getClientId());
            entity.setClient(client);
        }

        // Account
        if (dto.getAccountId() != null) {
            AccountEntity account = new AccountEntity();
            account.setId(dto.getAccountId());
            entity.setAccount(account);
        }

        entity.setUserLogin(dto.getUserLogin());
        entity.setDefault(dto.getIsDefault());
        entity.setUserRole(dto.getUserRole());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public AccountLogin toDto(AccountLoginEntity entity) {
        if (entity == null) {
            return null;
        }

        AccountLogin dto = new AccountLogin();
        dto.setId(entity.getId());
        dto.setTid(entity.getTenant() != null ? entity.getTenant().getId() : null);
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null);
        dto.setAccountId(entity.getAccount() != null ? entity.getAccount().getId() : null);
        dto.setUserLogin(entity.getUserLogin());
        dto.setIsDefault(entity.getDefault());
        dto.setUserRole(entity.getUserRole());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}