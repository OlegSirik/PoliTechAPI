package ru.pt.api.service.projection;

import java.util.Map;

/**
 * Получить данные по полису в формате json
 */
public interface ProjectionService {

    String getProductCode(String json);

    Map<String, String> getProductParams(String json);

}
