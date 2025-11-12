package ru.pt.auth.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.ProductRole;
import ru.pt.auth.entity.AccountEntity;
import ru.pt.auth.entity.ClientEntity;
import ru.pt.auth.entity.ProductRoleEntity;
import ru.pt.auth.entity.TenantEntity;

@Component
public class ProductRoleMapper {

    public ProductRoleEntity toEntity(ProductRole dto) {
        if (dto == null) {
            return null;
        }

        ProductRoleEntity entity = new ProductRoleEntity();
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

        // Role Account
        if (dto.getRoleAccountId() != null) {
            AccountEntity roleAccount = new AccountEntity();
            roleAccount.setId(dto.getRoleAccountId());
            entity.setRoleAccount(roleAccount);
        }

        entity.setRoleProductId(dto.getRoleProductId());
        entity.setDeleted(dto.getIsDeleted());
        entity.setCanRead(dto.getCanRead());
        entity.setCanQuote(dto.getCanQuote());
        entity.setCanPolicy(dto.getCanPolicy());
        entity.setCanAddendum(dto.getCanAddendum());
        entity.setCanCancel(dto.getCanCancel());
        entity.setCanProlongate(dto.getCanProlongate());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public ProductRole toDto(ProductRoleEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductRole dto = new ProductRole();
        dto.setId(entity.getId());
        dto.setTid(entity.getTenant() != null ? entity.getTenant().getId() : null);
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null);
        dto.setAccountId(entity.getAccount() != null ? entity.getAccount().getId() : null);
        dto.setRoleProductId(entity.getRoleProductId());
        dto.setRoleAccountId(entity.getRoleAccount() != null ? entity.getRoleAccount().getId() : null);
        dto.setIsDeleted(entity.getDeleted());
        dto.setCanRead(entity.getCanRead());
        dto.setCanQuote(entity.getCanQuote());
        dto.setCanPolicy(entity.getCanPolicy());
        dto.setCanAddendum(entity.getCanAddendum());
        dto.setCanCancel(entity.getCanCancel());
        dto.setCanProlongate(entity.getCanProlongate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}