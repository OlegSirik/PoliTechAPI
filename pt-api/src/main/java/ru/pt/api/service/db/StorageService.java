package ru.pt.api.service.db;

import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.dto.versioning.Version;

import java.util.List;
import java.util.UUID;

/**
 * Сервис хранения договоров + методы доступа
 */
public interface StorageService {

    /**
     * Сохранить полис
     * @param policy договор
     * @param userData данные о пользователе
     * @param version версия под которой создан договор
     * @param uuid идентификатор договора/процесса
     * @return сохраненный договор
     */
    PolicyData save(String policy, UserData userData, Version version, UUID uuid);

    void update(PolicyData policyData);

    /**
     * Обновить полис в статусе NEW, оплаченные не обновляем
     * Между тем это интерфейс хранилища - проверка статуса должна быть перед вызовом этого метода
     * @param policy полис
     * @param userData данные о пользователе
     * @param version версия под которой был создан ИЗНАЧАЛЬНЫЙ договор
     * @param policyNumber номер полиса для обновления
     * @return обновленный договор
     */
    PolicyData update(String policy, UserData userData, Version version, String policyNumber);

    /**
     * Получить данные по айди договора
     * @param policyId UUID договора
     * @return договор
     */
    PolicyData getPolicyById(UUID policyId);

    /**
     * Получить данные по номеру договора
     * @param policyNumber номер договора
     * @return договор
     */
    PolicyData getPolicyByNumber(String policyNumber);

    /**
     * Получить все договоры пользователя
     * @param userData данные о пользователе
     * @return список договоров
     */
    List<PolicyData> getPoliciesForUser(UserData userData);
    // TODO методы для фильтрации списка полисов
}
