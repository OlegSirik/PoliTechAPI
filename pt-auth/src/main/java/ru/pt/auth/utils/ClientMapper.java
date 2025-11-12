package ru.pt.auth.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.Client;
import ru.pt.auth.entity.ClientEntity;
import ru.pt.auth.entity.TenantEntity;

@Component
public class ClientMapper {

    public ClientEntity toEntity(Client dto) {
        if (dto == null) {
            return null;
        }

        ClientEntity entity = new ClientEntity();
        entity.setId(dto.getId());

        // Создаем Tenant только с id для связи
        if (dto.getTid() != null) {
            TenantEntity tenant = new TenantEntity();
            tenant.setId(dto.getTid());
            entity.setTenant(tenant);
        }

        entity.setClientId(dto.getClientId());
        entity.setDefaultAccountId(dto.getDefaultAccountId());
        entity.setName(dto.getName());
        entity.setDeleted(dto.getIsDeleted());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public Client toDto(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        Client dto = new Client();
        dto.setId(entity.getId());
        dto.setTid(entity.getTenant() != null ? entity.getTenant().getId() : null);
        dto.setClientId(entity.getClientId());
        dto.setDefaultAccountId(entity.getDefaultAccountId());
        dto.setName(entity.getName());
        dto.setIsDeleted(entity.getDeleted());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}