package ru.pt.api.service.process;

import ru.pt.api.dto.errors.ValidationError;
import ru.pt.api.dto.process.ValidatorType;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.ProductVersionModel;

import java.util.List;

/**
 * Валидация договора(возможно должно быть в модуле продукта или вообще в отдельном модуле)
 */
public interface ValidatorService {

    /**
     * Валидация полиса
     *
     * @param policy        договор
     * @param validatorType тип валидации
     * @return список ошибок
     */
    List<ValidationError> validate(String policy, ValidatorType validatorType);

    /**
     * Валидация полиса по переменным
     *
     * @param validatorType       тип валидатора
     * @param productVersionModel версия продукта
     * @param lobVars             переменные для валидации
     * @return список ошибок
     */
    List<ValidationError> validate(ValidatorType validatorType, ProductVersionModel productVersionModel, List<LobVar> lobVars);

}
