package ru.pt.api.service.versioning;

import ru.pt.api.dto.versioning.Version;

/**
 * Управление версиями внутри приложения
 */
public interface VersionManager {

    /**
     * Получить версию по номеру полиса
     * @param policyNumber номер полиса
     * @return версия
     */
    Version getVersion(String policyNumber);

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
