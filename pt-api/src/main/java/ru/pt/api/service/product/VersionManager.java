package ru.pt.api.service.product;

import ru.pt.api.dto.versioning.Version;

/**
 * Управление версиями внутри приложения
 * Версия относится к продукту, поэтому в этом модуле, а не в отдельном
 */
public interface VersionManager {

    /**
     * Получить версию по номеру полиса
     * @param policyNumber номер полиса
     * @return версия
     */
    Version getVersionByPolicyNumber(String policyNumber);

    /**
     * Получить последнюю версию по коду продукта
     * @param productCode код продукта
     * @return версия
     */
    Version getLatestVersionByProductCode(String productCode);

    /**
     * Установить версию для договора
     * @param policyNumber номер полиса
     * @param version версия
     */
    void setVersion(String policyNumber, Version version);

    /**
     * Обновить версию договора
     * @param policyNumber номер договора
     * @param version новая версия
     */
    void updateVersion(String policyNumber, Version version);

}
