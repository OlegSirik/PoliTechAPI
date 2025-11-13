package ru.pt.numbers.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.api.dto.numbers.NumberGeneratorDescription;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.numbers.service.DatabaseNumberGeneratorService;

import java.net.URI;
import java.util.Map;
// TODO пока не используется - предполагается как часть настройки продукта в админке
@RestController("restNumberGeneratorService")
@RequestMapping("/api/v1/numbers")
public class RestNumberGeneratorService {

    private final NumberGeneratorService numberGeneratorService;

    public RestNumberGeneratorService(NumberGeneratorService numberGeneratorService) {
        this.numberGeneratorService = numberGeneratorService;
    }


    @GetMapping("/next")
    public ResponseEntity<String> getNextNumber(Map<String, Object> values, String productCode) {
        return ResponseEntity.ok(numberGeneratorService.getNextNumber(values, productCode));
    }

    @PostMapping
    public ResponseEntity<Void> create(NumberGeneratorDescription numberGeneratorDescription) {
        numberGeneratorService.create(numberGeneratorDescription);
        return ResponseEntity.created(URI.create("/" + numberGeneratorDescription.getProductCode())).build();
    }

    @PutMapping
    public ResponseEntity<Boolean> update(NumberGeneratorDescription numberGeneratorDescription) {
        numberGeneratorService.update(numberGeneratorDescription);
        return ResponseEntity.ok(Boolean.TRUE);
    }

}
