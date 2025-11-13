package ru.pt.api.service.process;

import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.product.LobModel;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.ProductVersionModel;

import java.util.List;

/**
 * Действия до выполнения операций с договором
 */
public interface PreProcessService {
    /**
     * Дозаполнить договор вычисляемыми значениями
     *
     * @param policy              договор
     * @param productVersionModel данные по продукту
     * @return дозаполненный договор
     */
    String enrichPolicy(String policy, ProductVersionModel productVersionModel);

    /**
     * Вычислить значения переменных по jsonPath договора
     *
     * @param policy      договор
     * @param lobModel    данные о переменных Линии Бизнеса
     * @param productCode код продукта
     * @return список переменных и значений
     */
    List<LobVar> evaluateAndEnrichVariables(String policy, LobModel lobModel, String productCode);

    /**
     * Добавить переменные для сохранения результатов расчета
     *
     * @param insObject объект с рисками и покрытиями
     * @param lobVars   переменные от продукта
     */
    void enrichVariablesBeforeCalculation(InsuredObject insObject, List<LobVar> lobVars);

    /**
     * Дозаполнить риски и покрытия в страхуемом объекте
     *
     * @param policy              договор
     * @param productVersionModel продукт
     * @return страхуемый объект с дозаполненными полями
     */
    InsuredObject getInsuredObject(String policy, ProductVersionModel productVersionModel);
}
