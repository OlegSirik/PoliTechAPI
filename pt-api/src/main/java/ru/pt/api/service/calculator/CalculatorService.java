package ru.pt.api.service.calculator;

import ru.pt.api.dto.calculator.CalculatorModel;
import ru.pt.api.dto.product.LobVar;

import java.util.List;

/**
 * Методы для CRUD операций с калькуляторами и для расчета страховой премии договора
 */
public interface CalculatorService {

    CalculatorModel getCalculator(Integer productId, Integer versionNo, Integer packageNo);

    CalculatorModel createCalculatorIfMissing(Integer productId, String productCode, Integer versionNo, Integer packageNo);

    CalculatorModel getCalculatorModel(Integer productId, Integer versionNo, Integer packageNo);

    List<LobVar> runCalculator(Integer productId, Integer versionNo, Integer packageNo, List<LobVar> inputValues);

    CalculatorModel replaceCalculator(Integer productId, String productCode, Integer versionNo,
                                      Integer packageNo, CalculatorModel newJson);

    void syncVars(Integer calculatorId);

}
