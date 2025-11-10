package ru.pt.api.service.process;

import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.product.LobVar;

import java.util.List;

/**
 * Дополнительная логика после выполнения определенных процессов в моменте обработки договора
 */
public interface PostProcessService {

    /**
     * Заполнить премию по рискам
     *
     * @param insuredObject    страхуемый объект
     * @param calculatedValues переменные после расчета
     * @return заполненный объект
     */
    InsuredObject setCovers(InsuredObject insuredObject, List<LobVar> calculatedValues);

}
