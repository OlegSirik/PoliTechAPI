package ru.pt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pt.domain.NumberGenerator;
import ru.pt.repository.NumberGeneratorRepository;

import java.time.LocalDate;
//import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class NumberGeneratorService {

    private final NumberGeneratorRepository repository;

    public NumberGeneratorService(NumberGeneratorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NumberGenerator getNext(Integer id) {
        NumberGenerator ng = repository.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + id));

        LocalDate today = LocalDate.now();
        switch (ng.getResetPolicy()) {
            case "YEARLY":
                if (ng.getLastReset().getYear() != today.getYear()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case "MONTHLY":
                if (ng.getLastReset().getYear() != today.getYear() || ng.getLastReset().getMonth() != today.getMonth()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case "NEVER":
            default:
                // no-op
        }

        int next = ng.getCurrentValue() + 1;
        if (ng.getMaxValue() != null && next > ng.getMaxValue()) {
            next = 1;
        }
        ng.setCurrentValue(next);

        return repository.save(ng);
    }

    @Transactional
    public String getNumber(Map<String, String> values, Long id, String productCode) {
        NumberGenerator ng = null;
        if ( productCode != null && !productCode.isEmpty() ) {
            ng = repository.findByProductCode(productCode)
                    .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + productCode));
        }
        if ( ng == null && id == null ) {
            throw new IllegalArgumentException("Generator not found: " + id + " " + productCode);
        }
        ng = getNext(ng.getId());

        String mask = ng.getMask();
        StringBuilder resultMask = new StringBuilder(mask);
        LocalDate today = LocalDate.now();
        
        // Replace {KEY} patterns
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(mask);
        
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
                if (ng.getXorMask() != null && ng.getXorMask().length() > 0) {
                    currentValue = currentValue ^ Integer.parseInt(ng.getXorMask());
                }
                replacement = String.format("%0" + xCount + "d", currentValue);
                
            } else {
                // Get value from provided map
                replacement = values.getOrDefault(key, "");
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

    private static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

//create void create(NumberGenerator numberGenerator)
    @Transactional
    public void create(NumberGenerator numberGenerator) {
        repository.save(numberGenerator);
    }

    @Transactional
    public void update(NumberGenerator numberGenerator) {
        if (numberGenerator.getId() == null) {
            throw new IllegalArgumentException("NumberGenerator ID must not be null for update");
        }
        NumberGenerator existing = repository.findById(numberGenerator.getId())
                .orElseThrow(() -> new IllegalArgumentException("NumberGenerator not found with id: " + numberGenerator.getId()));
        existing.setProductCode(numberGenerator.getProductCode());
        existing.setMask(numberGenerator.getMask());
        existing.setResetPolicy(numberGenerator.getResetPolicy());
        existing.setMaxValue(numberGenerator.getMaxValue());
        existing.setLastReset(numberGenerator.getLastReset());
        existing.setCurrentValue(numberGenerator.getCurrentValue());
        existing.setXorMask(numberGenerator.getXorMask());
        repository.save(existing);
    }

}


