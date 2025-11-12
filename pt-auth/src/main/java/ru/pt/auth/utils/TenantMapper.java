package ru.pt.auth.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.Tenant;
import ru.pt.auth.entity.TenantEntity;

@Component
public class TenantMapper {

    public TenantEntity toEntity(Tenant dto) {
        if (dto == null) {
            return null;
        }

        TenantEntity entity = new TenantEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDeleted(dto.getIsDeleted());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public Tenant toDto(TenantEntity entity) {
        if (entity == null) {
            return null;
        }

        Tenant dto = new Tenant();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIsDeleted(entity.getDeleted());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}