package ru.pt.calculator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pt.api.dto.calculator.CalculatorModel;
import ru.pt.api.dto.calculator.CoefficientDef;
import ru.pt.api.dto.calculator.FormulaDef;
import ru.pt.api.dto.calculator.FormulaLine;
import ru.pt.api.dto.product.LobModel;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.product.ProductVersionModel;
import ru.pt.api.service.calculator.CalculatorService;
import ru.pt.api.service.calculator.CoefficientService;
import ru.pt.api.service.product.LobService;
import ru.pt.api.service.product.ProductService;
import ru.pt.calculator.entity.CalculatorEntity;
import ru.pt.calculator.repository.CalculatorRepository;
import ru.pt.calculator.utils.ValidatorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CalculatorServiceImpl implements CalculatorService {

    private final CalculatorRepository calculatorRepository;
    private final CoefficientService coefficientService;
    private final ProductService productService;
    private final LobService lobService;
    private final ObjectMapper objectMapper;

    public CalculatorServiceImpl(CalculatorRepository calculatorRepository, CoefficientService coefficientService, ProductService productService, LobService lobService, ObjectMapper objectMapper) {
        this.calculatorRepository = calculatorRepository;
        this.coefficientService = coefficientService;
        this.productService = productService;
        this.lobService = lobService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public CalculatorModel getCalculator(Integer productId, Integer versionNo, Integer packageNo) {
        return calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .map(CalculatorEntity::getCalculator)
                .map(c -> {
                    try {
                        return objectMapper.readValue(c, CalculatorModel.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);
    }

    @Transactional
    public CalculatorModel createCalculatorIfMissing(Integer productId, String productCode, Integer versionNo, Integer packageNo) {
        return calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .map(CalculatorEntity::getCalculator)
                .map(c -> {
                    try {
                        return objectMapper.readValue(c, CalculatorModel.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseGet(() -> {
                    // get product, product version and line of business from services
                    // Example:
                    ru.pt.api.dto.product.ProductVersionModel productVersionModel = productService.getProduct(productId, true);

                    LobModel lobModel = lobService.getByCode(productVersionModel.getLob());

                    if (lobModel == null) {
                        throw new IllegalArgumentException("LOB not found: " + productVersionModel.getLob());
                    }

                    Integer id = calculatorRepository.nextCalculatorId();
                    // create empty calculator JSON as per spec
                    CalculatorModel calculatorModel = new CalculatorModel();
                    calculatorModel.setId(id);
                    calculatorModel.setProductId(productId.intValue());
                    calculatorModel.setProductCode(productCode);
                    calculatorModel.setVersionNo(versionNo);
                    calculatorModel.setPackageNo(packageNo);
                    calculatorModel.setVars(new ArrayList<>());
                    calculatorModel.setFormulas(new ArrayList<>());
                    calculatorModel.setCoefficients(new ArrayList<>());


                    lobModel.getMpVars()
                            .forEach(var -> calculatorModel.getVars().add(var));

                    // INSERT_YOUR_CODE
                    // Find the package in productVersion.packages with code == packageNo
                    productVersionModel.getPackages().forEach(pkg -> {

                        if (pkg.getCode().equals(packageNo)) {
                            pkg.getCovers().forEach(cover -> {
                                LobVar var = new LobVar();
                                var.setVarCode(cover.getCode() + "_SumIns");
                                var.setVarName(cover.getCode() + " Страховая сумма");
                                var.setVarType("VAR");
                                calculatorModel.getVars().add(var);

                                var = new LobVar();
                                var.setVarCode(cover.getCode() + "_Prem");
                                var.setVarName(cover.getCode() + " Премия");
                                var.setVarType("VAR");
                                calculatorModel.getVars().add(var);

                                var = new LobVar();
                                var.setVarCode(cover.getCode() + "_DedNr");
                                var.setVarName(cover.getCode() + " Номер франшизы");
                                var.setVarType("VAR");
                                calculatorModel.getVars().add(var);

                            });
                        }
                    });

                    FormulaDef formulaDef = new FormulaDef();
                    formulaDef.setVarCode("pkg" + packageNo + "_formula");
                    formulaDef.setVarName("Calculator for package:" + packageNo);

                    formulaDef.setLines(new ArrayList<>());
                    calculatorModel.getFormulas().add(formulaDef);


                    CalculatorEntity e = new CalculatorEntity();
                    e.setId(id);
                    e.setProductId(productId);
                    e.setProductCode(productCode);
                    e.setVersionNo(versionNo);
                    e.setPackageNo(packageNo);
                    calculatorModel.setId(id);
                    String calculatorJson;
                    try {
                        calculatorJson = objectMapper.writeValueAsString(calculatorModel);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    e.setCalculator(calculatorJson);
                    CalculatorEntity saved = calculatorRepository.save(e);

                    String savedCalculatorJson = saved.getCalculator();
                    try {
                        return objectMapper.readValue(savedCalculatorJson, CalculatorModel.class);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    @Transactional(readOnly = true)
    public CalculatorModel getCalculatorModel(Integer productId, Integer versionNo, Integer packageNo) {
        CalculatorEntity entity = calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .orElseThrow(() -> new IllegalArgumentException("Calculator not found for productId=" + productId + ", versionNo=" + versionNo + ", packageNo=" + packageNo));
        String calculatorJson = entity.getCalculator();
        CalculatorModel calculatorModel = null;
        try {
            calculatorModel = objectMapper.readValue(calculatorJson, CalculatorModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (calculatorModel == null) {
            throw new IllegalStateException("Calculator JSON is null for productId=" + productId + ", versionNo=" + versionNo + ", packageNo=" + packageNo);
        }
        try {
            return calculatorModel;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse calculator JSON for productId=" + productId + ", versionNo=" + versionNo + ", packageNo=" + packageNo, e);
        }
    }

    //@Transactional(readOnly = true)
    public List<LobVar> runCalculator(Integer productId, Integer versionNo, Integer packageNo, List<LobVar> inputValues) {
        CalculatorModel model = getCalculatorModel(productId, versionNo, packageNo);
        if (model == null) return inputValues;

        // INSERT_YOUR_CODE
        if (model.getVars() == null) {
            model.setVars(new ArrayList<>());
        }
        List<LobVar> modelVars = model.getVars();
        for (LobVar inputVar : inputValues) {
            boolean found = false;
            for (LobVar modelVar : modelVars) {
                if (modelVar.getVarCode() != null && modelVar.getVarCode().equals(inputVar.getVarCode())) {
                    modelVar.setVarValue(inputVar.getVarValue());
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Add a copy of inputVar to modelVars
                LobVar newVar = new LobVar();
                newVar.setVarCode(inputVar.getVarCode());
                newVar.setVarValue(inputVar.getVarValue());
                newVar.setVarType(inputVar.getVarType());
                newVar.setVarPath(inputVar.getVarPath());
                // Copy other fields if needed
                modelVars.add(newVar);
            }
        }

        if (model.getFormulas() != null && !model.getFormulas().isEmpty()) {
            FormulaDef f = model.getFormulas().getFirst();
            // Для пакета есть только 1 формула. Поэтому берем всегда 0-й элемент
            // сортируем строки формулы по nr
            // INSERT_YOUR_CODE
            List<FormulaLine> lines = new ArrayList<>(f.getLines());
            lines.sort((a, b) -> {
                Integer na = a.getNr();
                Integer nb = b.getNr();
                if (na == null && nb == null) return 0;
                if (na == null) return 1;
                if (nb == null) return -1;
                return na.compareTo(nb);
            });

            for (FormulaLine line : lines) {

                if (!Objects.equals(line.getConditionOperator(), "") && !Objects.equals(line.getConditionLeft(), "")) {
                    if (!ValidatorImpl.validate(modelVars, line.getConditionLeft(), line.getConditionOperator(), line.getConditionRight(), line.getConditionOperator())) {
                        continue;
                    }
                }

                LobVar lv = modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionLeft())).findFirst().orElse(null);
                LobVar rv = modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionRight())).findFirst().orElse(null);


                if (lv != null && lv.getVarType().equals("COEFFICIENT")) {
                    CoefficientDef cd = model.getCoefficients().stream().filter(c -> c.getVarCode().equals(lv.getVarCode())).findFirst().orElse(null);
                    if (cd != null) {
                        Map<String, String> modelVarsMap = modelVars.stream().collect(Collectors.toMap(LobVar::getVarCode, LobVar::getVarValue));
                        String s = coefficientService.getCoefficientValue(model.getId(), lv.getVarCode(), modelVarsMap, cd.getColumns());
                        lv.setVarValue(s);
                    }
                }
                if (rv != null && rv.getVarType().equals("COEFFICIENT")) {
                    CoefficientDef cd = model.getCoefficients().stream().filter(c -> c.getVarCode().equals(rv.getVarCode())).findFirst().orElse(null);
                    if (cd != null) {
                        Map<String, String> modelVarsMap = modelVars.stream().collect(Collectors.toMap(LobVar::getVarCode, LobVar::getVarValue));
                        String s = coefficientService.getCoefficientValue(model.getId(), rv.getVarCode(), modelVarsMap, cd.getColumns());
                        rv.setVarValue(s);
                    }
                }

                Double dlv = null;
                Double drv = null;

                if (lv != null) {
                    dlv = tryParseDouble(lv);
                }
                if (rv != null) {
                    drv = tryParseDouble(rv);
                }


                Double res = compute(dlv, line.getExpressionOperator(), drv);
                if (line.getPostProcessor() != null) {
                    res = postProcess(res, line.getPostProcessor());
                }

                if (line.getExpressionResult() != null && line.getExpressionResult() != "") {
                    modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionResult())).findFirst().orElse(null).setVarValue(res == null ? null : res.toString());
                }
            }
        }

        return modelVars;
    }


    private Double compute(Double left, String operator, Double right) {
        if (operator == null || operator.isBlank()) return left == null ? null : left;

        switch (operator.trim()) {
            case "+":
                if (left != null && right != null) return trimZeros(left + right);
                if (left == null && right == null) return null;
                if (left == null) return trimZeros(right);
                if (right == null) return trimZeros(left);
                return null;
            case "-":
                if (left != null && right != null) return trimZeros(left - right);
                if (left == null && right == null) return null;
                if (left == null) return trimZeros(0 - right);
                if (right == null) return trimZeros(left);
                return null;
            case "*":
                if (left != null && right != null) return trimZeros(left * right);
                return null;
            case "/":
                if (left != null && right != null && right != 0d) return trimZeros(left / right);
                return null;
            default:
                return left;
        }
    }

    private Double postProcess(Double value, String postProcessor) {
        if (value == null) return null;
        String pp = postProcessor.trim().toLowerCase();
        if (pp.startsWith("round")) {
            int scale = 0;
            if ("round2".equals(pp)) scale = 2;
            else {
                String digits = pp.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) try {
                    scale = Integer.parseInt(digits);
                } catch (Exception ignored) {
                }
            }
            Double d = value;
            if (d != null) {
                java.math.BigDecimal bd = new java.math.BigDecimal(d).setScale(scale, java.math.RoundingMode.HALF_UP);
                return trimZeros(bd.doubleValue());
            }
            return value;
        }
        return value;
    }

    private Double tryParseDouble(LobVar s) {
        if (s == null) return null;

        try {
            return Double.parseDouble(s.getVarValue());
        } catch (Exception e) {
            return null;
        }
    }

    private Double trimZeros(double d) {
        String s = Double.toString(d);
        if (s.contains("E") || s.contains("e")) {
            // Convert scientific notation to plain string and remove trailing zeros
            s = new java.math.BigDecimal(s).stripTrailingZeros().toPlainString();
        }
        if (s.indexOf('.') >= 0) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return d;
        }
    }

    public CalculatorModel replaceCalculator(Integer productId, String productCode, Integer versionNo,
                                             Integer packageNo, CalculatorModel newJson) {
        CalculatorEntity entity = calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .orElseThrow(() -> new IllegalArgumentException("Calculator not found for productId=" + productId + ", versionNo=" + versionNo + ", packageNo=" + packageNo));

        newJson.setProductId(productId);
        newJson.setProductCode(productCode);
        newJson.setVersionNo(versionNo);
        newJson.setPackageNo(packageNo);

        String calculatorJson;
        try {
            calculatorJson = objectMapper.writeValueAsString(newJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        entity.setCalculator(calculatorJson);

        CalculatorEntity saved = calculatorRepository.save(entity);
        String calculator = saved.getCalculator();
        try {
            return objectMapper.readValue(calculator, CalculatorModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void syncVars(Integer calculatorId) {
        // TODO Auto-generated method stub
        // get calculator by id from repository
        CalculatorEntity entity = calculatorRepository.findById(calculatorId)
                .orElseThrow(() -> new IllegalArgumentException("Calculator not found for id=" + calculatorId));
        String calculatorModelJson = entity.getCalculator();
        CalculatorModel calculatorModel;
        try {
            calculatorModel = objectMapper.readValue(calculatorModelJson, CalculatorModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (calculatorModel == null) {
            throw new IllegalStateException("Calculator JSON is null for id=" + calculatorId);
        }
        // get productVersionModel from repository
        ProductVersionModel productVersionModel = productService.getProduct(entity.getProductId(), false);
        if (productVersionModel == null) {
            throw new IllegalArgumentException("Product not found for id=" + entity.getProductId());
        }

        // get productVersionModel version from repository
        ProductVersionModel productVersion = productService.getVersion(entity.getProductId(), entity.getVersionNo());
        if (productVersion == null) {
            throw new IllegalArgumentException("Product version not found for id=" + entity.getProductId() + " and versionNo=" + entity.getVersionNo());
        }


        // get lob from repository
        LobModel lobModel = lobService.getByCode(productVersionModel.getLob());
        if (lobModel == null) {
            throw new IllegalArgumentException("LOB not found for code=" + productVersionModel.getLob());
        }


        // get vars from lob
        List<LobVar> vars = lobModel.getMpVars();
        // add vars to calculator if it not found by code
        for (LobVar var : vars) {
            if (calculatorModel.getVars().stream().noneMatch(v -> v.getVarCode().equals(var.getVarCode()))) {
                calculatorModel.getVars().add(var);
            }
        }
        // save calculator
        calculatorRepository.save(entity);
    }

}
