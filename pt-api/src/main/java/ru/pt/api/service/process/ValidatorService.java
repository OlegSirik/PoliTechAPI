package ru.pt.api.service.process;

import ru.pt.api.dto.errors.ValidationError;
import ru.pt.api.dto.process.ValidatorType;

import java.util.List;

/**
 * Валидация договора(возможно должно быть в модуле продукта или вообще в отдельном модуле)
 */
public interface ValidatorService {

    /**
     * Валидация полиса
     * @param policy договор
     * @param validatorType тип валидации
     * @return список ошибок
     */
    List<ValidationError> validate(String policy, ValidatorType validatorType);

}
