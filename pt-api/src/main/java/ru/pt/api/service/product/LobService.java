package ru.pt.api.service.product;

import ru.pt.api.dto.product.LobModel;

import java.util.List;

/**
 * Методы для работы с общей частью продукта(Линия бизнеса - LOB)
 */
public interface LobService {
    // TODO сгенерировать описание
    List<Object[]> listActiveSummaries();

    LobModel getByCode(String code);

    // get by id
    LobModel getById(Integer id);

    LobModel create(LobModel payload);

    boolean softDeleteByCode(String code);

    boolean softDeleteById(Integer id);

    LobModel updateByCode(String code, LobModel payload);
}