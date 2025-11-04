package ru.pt.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.pt.domain.calculator.CalculatorModel;
import ru.pt.service.CalculatorService;
import ru.pt.service.CoefficientService;

@RestController
@RequestMapping("/admin")
public class AdminCalculatorController {

    private final CalculatorService calculateService;
    private final CoefficientService coefficientService;

    public AdminCalculatorController(CalculatorService calculateService, CoefficientService coefficientService) {
        this.calculateService = calculateService;
        this.coefficientService = coefficientService;
    }

    @GetMapping("/products/{productId}/versions/{versionNo}/packages/{packageNo}/calculator")
    public ResponseEntity<CalculatorModel> getCalculator(@PathVariable("productId") Integer productId,
                                                  @PathVariable("versionNo") Integer versionNo,
                                                  @PathVariable("packageNo") Integer packageNo) {
        CalculatorModel json = calculateService.getCalculator(productId, versionNo, packageNo);
        return json != null ? ResponseEntity.ok(json) : ResponseEntity.notFound().build();
    }

    // coefficients endpoints
    @GetMapping("/calculator/{calculatorId}/coefficients/{code}")
    public ResponseEntity<JsonNode> getCoefficients(@PathVariable("calculatorId") Integer calculatorId,
                                                    @PathVariable("code") String code) {
        return ResponseEntity.ok(coefficientService.getTable(calculatorId, code));
    }

    @PostMapping("/calculator/{calculatorId}/coefficients/{code}")
    public ResponseEntity<ArrayNode> createCoefficients(@PathVariable("calculatorId") Integer calculatorId,
                                                       @PathVariable("code") String code,
                                                       @RequestBody ArrayNode tableJson) {
        // if any exists -> error
        if (coefficientService.getTable(calculatorId, code).withArray("data").size() > 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(coefficientService.replaceTable(calculatorId, code, tableJson));
    }

    @PutMapping("/calculator/{calculatorId}/coefficients/{code}")
    public ResponseEntity<ArrayNode> replaceCoefficients(@PathVariable("calculatorId") Integer calculatorId,
                                                        @PathVariable("code") String code,
                                                        @RequestBody ArrayNode tableJson) {
        return ResponseEntity.ok(coefficientService.replaceTable(calculatorId, code, tableJson));
    }

    @PostMapping("/products/{productId}/versions/{versionNo}/packages/{packageNo}/calculator")
    public ResponseEntity<CalculatorModel> createCalculator(@PathVariable("productId") Integer productId,
                                                     @PathVariable("versionNo") Integer versionNo,
                                                     @PathVariable("packageNo") Integer packageNo,
                                                     @RequestParam(name = "productCode", required = false, defaultValue = "") String productCode) {
        CalculatorModel json = calculateService.createCalculatorIfMissing(productId, productCode, versionNo, packageNo);
        return ResponseEntity.ok(json);
    }

    @PutMapping("/products/{productId}/versions/{versionNo}/packages/{packageNo}/calculator")
    public ResponseEntity<CalculatorModel> replaceCalculator(@PathVariable("productId") Integer productId,
                                                      @PathVariable("versionNo") Integer versionNo,
                                                      @PathVariable("packageNo") Integer packageNo,
                                                      @RequestParam(name = "productCode", required = false, defaultValue = "") String productCode,
                                                      @RequestBody CalculatorModel newJson) {
        CalculatorModel json = calculateService.replaceCalculator(productId, productCode, versionNo, packageNo, newJson);
        return ResponseEntity.ok(json);
    }

// INSERT_YOUR_CODE
    @PostMapping("/calculator/{id}/prc/syncvars")
    public ResponseEntity<Void> syncVars(@PathVariable("id") Integer calculatorId) {
        calculateService.syncVars(calculatorId);
        return ResponseEntity.ok().build();
    }
}


