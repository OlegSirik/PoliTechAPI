package ru.pt.db.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.db.PolicyIndex;
import ru.pt.db.entity.PolicyEntity;
import ru.pt.db.entity.PolicyIndexEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class PolicyMapper {

    public PolicyIndex toDto(PolicyIndexEntity entity) {
        var dto = new PolicyIndex();
        dto.setPolicyId(entity.getPolicyId());
        dto.setEndDate(ZonedDateTime.ofInstant(entity.getEndDate().toInstant(), ZoneId.systemDefault()));
        dto.setStartDate(ZonedDateTime.ofInstant(entity.getStartDate().toInstant(), ZoneId.systemDefault()));
        dto.setPolicyNumber(entity.getPolicyNr());
        dto.setPolicyStatus(entity.getPolicyStatus());
        dto.setProductCode(entity.getProductCode());
        dto.setClientAccountId(entity.getClientAccountId());
        dto.setUserAccountId(entity.getUserAccountId());
        dto.setVersionStatus(entity.getVersionStatus());
        dto.setVersionNo(entity.getVersionNo());
        return dto;
    }

    public PolicyIndexEntity toEntity(PolicyIndex entity) {
        var dto = new PolicyIndexEntity();
        dto.setPolicyId(entity.getPolicyId());
        dto.setEndDate(entity.getEndDate().toOffsetDateTime());
        dto.setStartDate(entity.getStartDate().toOffsetDateTime());
        dto.setPolicyNumber(entity.getPolicyNumber());
        dto.setPolicyStatus(entity.getPolicyStatus());
        dto.setProductCode(entity.getProductCode());
        dto.setClientAccountId(entity.getClientAccountId());
        dto.setUserAccountId(entity.getUserAccountId());
        dto.setVersionStatus(entity.getVersionStatus());
        dto.setVersionNo(entity.getVersionNo());
        return dto;
    }

}
