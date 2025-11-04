package ru.pt.numbers.utils;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.numbers.NumberGeneratorDescription;
import ru.pt.numbers.entity.NumberGeneratorEntity;

@Component
public class NumberGeneratorMapper {

    public NumberGeneratorEntity toEntity(NumberGeneratorDescription numberGeneratorDescription) {
        var entity = new NumberGeneratorEntity();
        entity.setId(numberGeneratorDescription.getId());
        entity.setProductCode(numberGeneratorDescription.getProductCode());
        entity.setMask(numberGeneratorDescription.getMask());
        entity.setResetPolicy(numberGeneratorDescription.getResetPolicy());
        entity.setMaxValue(numberGeneratorDescription.getMaxValue());
        entity.setXorMask(numberGeneratorDescription.getXorMask());
        return entity;
    }

    public NumberGeneratorDescription toDto(NumberGeneratorEntity numberGeneratorEntity) {
        var dto = new NumberGeneratorDescription();
        dto.setId(numberGeneratorEntity.getId());
        dto.setProductCode(numberGeneratorEntity.getProductCode());
        dto.setMask(numberGeneratorEntity.getMask());
        dto.setResetPolicy(numberGeneratorEntity.getResetPolicy());
        dto.setXorMask(numberGeneratorEntity.getXorMask());
        dto.setMaxValue(numberGeneratorEntity.getMaxValue());
        return dto;
    }

}
