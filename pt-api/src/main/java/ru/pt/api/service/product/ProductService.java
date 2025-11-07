package ru.pt.api.service.product;

import ru.pt.api.dto.product.ProductVersionModel;

import java.util.List;
import java.util.Map;

/**
 * Методы для работы с продуктом
 */
public interface ProductService {
    /**
     * Получить все продукты
     * @return список описаний продуктов
     */
    List<Map<String, Object>> listSummaries();

    /**
     * Создать продукт
     * @param productVersionModel описание продукта
     * @return сохраненный продукт
     */
    ProductVersionModel create(ProductVersionModel productVersionModel);

    /**
     * Получить информацию о продукте по айди и номеру версии
     * @param id айди продукта
     * @param versionNo номер версии
     * @return описание продукта
     */
    ProductVersionModel getVersion(Integer id, Integer versionNo);

    /**
     * Создать версию продукта на основании предыдущей
     * @param id айди продукта
     * @param versionNo номер версии
     * @return новая версия продукта
     */
    ProductVersionModel createVersionFrom(Integer id, Integer versionNo);

    /**
     * Обновить версию продукта
     * @param id айди
     * @param versionNo версия
     * @param newProductVersionModel новое описание продукта
     * @return сохраненное описание продукта
     */
    ProductVersionModel updateVersion(Integer id, Integer versionNo, ProductVersionModel newProductVersionModel);

    /**
     * Мягкое удаление продукта
     * @param id айди продукта
     */
    void softDeleteProduct(Integer id);

    /**
     * Удаление версии продукта
     * @param id айди
     * @param versionNo номер версии
     */
    void deleteVersion(Integer id, Integer versionNo);

    /**
     * Получить пример запроса на предрасчет
     * @param id айди продукта
     * @param versionNo номер версии
     * @return пример json
     */
    String getJsonExampleQuote(Integer id, Integer versionNo);

    /**
     * Получить пример запроса на расчет
     * @param id айди продукта
     * @param versionNo номер версии
     * @return пример json
     */
    String getJsonExampleSave(Integer id, Integer versionNo);
    // TODO возможно надо прокидывать версию
    //  - или флаг дев или прод
    ProductVersionModel getProduct(Integer id, boolean forDev);

    // TODO возможно надо прокидывать версию
    //  - или флаг дев или прод
    ProductVersionModel getProductByCode(String code, boolean forDev);

}
