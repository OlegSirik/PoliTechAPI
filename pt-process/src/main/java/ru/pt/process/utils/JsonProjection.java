package ru.pt.process.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class JsonProjection {

    private final DocumentContext documentContext;

    public JsonProjection(String json) {
        this.documentContext = JsonPath.parse(json);
    }

    /**
     * Получить код продукта
     */
    public String getProductCode() {
        return documentContext.read("$.product.code", String.class);
    }

}
