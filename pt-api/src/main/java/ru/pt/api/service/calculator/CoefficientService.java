package ru.pt.api.service.calculator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.pt.api.dto.calculator.CoefficientColumn;

import java.util.List;
import java.util.Map;

/**
 * Получение и редактирование коэффициентов
 */
public interface CoefficientService {

    ArrayNode getTable(Integer calculatorId, String code);

    ArrayNode replaceTable(Integer calculatorId, String code, ArrayNode tableJson);

    String getCoefficientValue(Integer calculatorId,
                               String coefficientCode,
                               Map<String, String> values,
                               List<CoefficientColumn> columns);

}
