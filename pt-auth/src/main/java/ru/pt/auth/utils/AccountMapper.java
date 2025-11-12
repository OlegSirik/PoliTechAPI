package ru.pt.auth.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.Account;
import ru.pt.auth.entity.AccountEntity;
import ru.pt.auth.entity.AccountNodeType;
import ru.pt.auth.entity.ClientEntity;
import ru.pt.auth.entity.TenantEntity;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account dto) {
        if (dto == null) {
            return null;
        }

        AccountEntity entity = new AccountEntity();
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

        // Parent
        if (dto.getParentId() != null) {
            AccountEntity parent = new AccountEntity();
            parent.setId(dto.getParentId());
            entity.setParent(parent);
        }

        entity.setNodeType(AccountNodeType.valueOf(dto.getNodeType()));
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public Account toDto(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        Account dto = new Account();
        dto.setId(entity.getId());
        dto.setTid(entity.getTenant() != null ? entity.getTenant().getId() : null);
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null);
        dto.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
        dto.setNodeType(entity.getNodeType().name());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}