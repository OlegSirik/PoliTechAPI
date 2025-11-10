package ru.pt.numbers.service;

import org.springframework.stereotype.Service;
import ru.pt.api.dto.numbers.NumberGeneratorDescription;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.numbers.entity.NumberGeneratorEntity;
import ru.pt.numbers.repository.NumberGeneratorRepository;
import ru.pt.numbers.utils.NumberGeneratorMapper;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Кор реализация сервиса генерации номеров через таблицу в БД
 */
@Service
public class DatabaseNumberGeneratorService implements NumberGeneratorService {

    private final NumberGeneratorRepository repository;
    private final NumberGeneratorMapper mapper;

    public DatabaseNumberGeneratorService(NumberGeneratorRepository repository,
                                  NumberGeneratorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public String getNextNumber(Map<String, Object> values, String productCode) {
        NumberGeneratorEntity ng = null;
        if (productCode != null && !productCode.isEmpty()) {
            ng = repository.findByProductCode(productCode)
                    .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + productCode));
        }
        if (ng == null) {
            // TODO кастомное исключение
            throw new IllegalArgumentException("Generator not found: " + productCode);
        }
        ng = getNext(ng.getId());

        String mask = ng.getMask();
        StringBuilder resultMask = new StringBuilder(mask);
        LocalDate today = LocalDate.now();

        // Replace {KEY} patterns
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(mask);

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement;

            // Built-in date keys
            if ("YYYY".equals(key)) {
                replacement = Integer.toString(today.getYear());
            } else if ("YY".equals(key)) {
                replacement = String.format("%02d", today.getYear() % 100);
            } else if ("MM".equals(key)) {
                replacement = String.format("%02d", today.getMonthValue());
            } else if (key.matches("X+")) {
                // Handle any number of X's as sequence number (XXXX, XXX, XX, etc.)
                int xCount = key.length();
                Integer currentValue = ng.getCurrentValue();
                if (ng.getXorMask() != null && !ng.getXorMask().isEmpty()) {
                    currentValue = currentValue ^ Integer.parseInt(ng.getXorMask());
                }
                replacement = String.format("%0" + xCount + "d", currentValue);

            } else {
                // Get value from provided map
                replacement = values.getOrDefault(key, "").toString();
            }

            replaceAll(resultMask, matcher.group(0), replacement);
        }

        return resultMask.toString();
    }


    private static void replaceAll(StringBuilder sb, String target, String replacement) {
        int idx;
        while ((idx = sb.indexOf(target)) != -1) {
            sb.replace(idx, idx + target.length(), replacement);
        }
    }

    @Override
    public void create(NumberGeneratorDescription numberGeneratorDescription) {
        var entity = mapper.toEntity(numberGeneratorDescription);
        repository.save(entity);
    }

    @Override
    public void update(NumberGeneratorDescription numberGeneratorDescription) {
        if (numberGeneratorDescription.getId() == null) {
            throw new IllegalArgumentException("NumberGenerator ID must not be null for update");
        }
        var existing = repository.findById(numberGeneratorDescription.getId())
                .orElseThrow(() -> new IllegalArgumentException("NumberGenerator not found with id: " + numberGeneratorDescription.getId()));
        existing.setProductCode(numberGeneratorDescription.getProductCode());
        existing.setMask(numberGeneratorDescription.getMask());
        existing.setResetPolicy(numberGeneratorDescription.getResetPolicy());
        existing.setMaxValue(numberGeneratorDescription.getMaxValue());
        existing.setXorMask(numberGeneratorDescription.getXorMask());
        repository.save(existing);
    }

    private NumberGeneratorEntity getNext(Integer id) {
        NumberGeneratorEntity ng = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + id));

        LocalDate today = LocalDate.now();
        switch (ng.getResetPolicy()) {
            case YEARLY:
                if (ng.getLastReset().getYear() != today.getYear()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case MONTHLY:
                if (ng.getLastReset().getYear() != today.getYear() || ng.getLastReset().getMonth() != today.getMonth()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case NEVER:
        }

        int next = ng.getCurrentValue() + 1;
        if (ng.getMaxValue() != null && next > ng.getMaxValue()) {
            next = 1;
        }
        ng.setCurrentValue(next);

        return repository.save(ng);
    }

}
