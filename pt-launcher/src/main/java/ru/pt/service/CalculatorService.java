package ru.pt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pt.domain.CalculatorEntity;
import ru.pt.domain.Product;
import ru.pt.domain.calculator.CalculatorModel;
import ru.pt.domain.calculator.CoefficientDef;
import ru.pt.domain.calculator.FormulaDef;
import ru.pt.domain.calculator.FormulaLine;

import ru.pt.domain.lob.LobModel;
import ru.pt.domain.lob.LobVar;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.repository.CalculatorRepository;
import ru.pt.repository.LobRepository;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class CalculatorService {

    private final CalculatorRepository calculatorRepository;

    private final CoefficientService coefficientService;
    private final ProductService productService;
    private final LobService lobService;  

    public CalculatorService(CalculatorRepository calculatorRepository, ProductRepository productRepository, ProductVersionRepository productVersionRepository, LobRepository lineOfBusinessRepository, CoefficientService coefficientService, ProductService productService, LobService lobService) {
        this.calculatorRepository = calculatorRepository;
        this.coefficientService = coefficientService;
        this.productService = productService;
        this.lobService = lobService;
    }

    @Transactional(readOnly = true)
    public CalculatorModel getCalculator(Integer productId, Integer versionNo, Integer packageNo) {
        return calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .map(CalculatorEntity::getCalculator)
                .orElse(null);
    }

    @Transactional
    public CalculatorModel createCalculatorIfMissing(Integer productId, String productCode, Integer versionNo, Integer packageNo) {
        return calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .map(CalculatorEntity::getCalculator)
                .orElseGet(() -> {
                    // get product, product version and line of business from services
                    // Example:
                    Product product = productService.getProduct(productId);

                    if (product.getDevVersionNo() == null) {
                        throw new IllegalArgumentException("Product has no dev version");
                    }

                    ProductVersionModel productVersion = productService.getVersion(productId, product.getDevVersionNo());
                    if (productVersion == null) {
                        throw new IllegalArgumentException("Product version not found: " + product.getDevVersionNo());
                    }
                    
                    LobModel lobModel = lobService.getByCode(product.getLob());

                    if (lobModel == null) {
                        throw new IllegalArgumentException("LOB not found: " + product.getLob());
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

                
                    lobModel.getMpVars().forEach(var -> {
                        calculatorModel.getVars().add(var);
                    });
                   
                    // INSERT_YOUR_CODE
                    // Find the package in productVersion.packages with code == packageNo
                    productVersion.getPackages().forEach(pkg -> {

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
                    e.setCalculator(calculatorModel);
                    CalculatorEntity saved = calculatorRepository.save(e);

                    return saved.getCalculator();
                });
    }

    @Transactional(readOnly = true)
    public CalculatorModel getCalculatorModel(Integer productId, Integer versionNo, Integer packageNo) {
        CalculatorEntity entity = calculatorRepository.findByKeys(productId, versionNo, packageNo)
                .orElseThrow(() -> new IllegalArgumentException("Calculator not found for productId=" + productId + ", versionNo=" + versionNo + ", packageNo=" + packageNo));
        CalculatorModel calculatorModel = entity.getCalculator();
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
            FormulaDef f = model.getFormulas().get(0);
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
                
                if (line.getConditionOperator() != "" && line.getConditionLeft() != "") {
                    if (!ValidatorImpl.validate(modelVars, line.getConditionLeft(), line.getConditionOperator(), line.getConditionRight(), line.getConditionOperator())) {
                        continue;
                    }
                }
                
                LobVar lv = modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionLeft())).findFirst().orElse(null);
                LobVar rv = modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionRight())).findFirst().orElse(null);


                if ( lv != null && lv.getVarType().equals("COEFFICIENT") ) {
                    CoefficientDef cd = model.getCoefficients().stream().filter(c -> c.getVarCode().equals(lv.getVarCode())).findFirst().orElse(null);
                    if (cd != null) {
                        Map<String, String> modelVarsMap = modelVars.stream().collect(Collectors.toMap(LobVar::getVarCode, LobVar::getVarValue));
                        String s = coefficientService.getCoefficientValue(model.getId(), lv.getVarCode(), modelVarsMap, cd.getColumns());
                        lv.setVarValue(s);
                    }
                }
                if ( rv != null && rv.getVarType().equals("COEFFICIENT") ) {
                    CoefficientDef cd = model.getCoefficients().stream().filter(c -> c.getVarCode().equals(rv.getVarCode())).findFirst().orElse(null);
                    if (cd != null) {
                        Map<String, String> modelVarsMap = modelVars.stream().collect(Collectors.toMap(LobVar::getVarCode, LobVar::getVarValue));
                        String s = coefficientService.getCoefficientValue(model.getId(), rv.getVarCode(), modelVarsMap, cd.getColumns());
                        rv.setVarValue(s);
                    }
                }

                Double dlv = null;
                Double drv = null;

                if ( lv != null ) {
                    dlv = tryParseDouble(lv);
                }
                if ( rv != null ) {
                    drv = tryParseDouble(rv);
                }
                

                Double res = compute(dlv, line.getExpressionOperator(), drv);
                if (line.getPostProcessor() != null) {
                    res = postProcess(res, line.getPostProcessor());
                }

                if (line.getExpressionResult() != null && line.getExpressionResult() != "") {
                    modelVars.stream().filter(v -> v.getVarCode().equals(line.getExpressionResult())).findFirst().orElse(null).setVarValue(res==null ? null : res.toString());
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
                if (!digits.isEmpty()) try { scale = Integer.parseInt(digits); } catch (Exception ignored) {}
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
        } catch (Exception e) { return null; }
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

                entity.setCalculator(newJson);
                
        CalculatorEntity saved = calculatorRepository.save(entity);
        return saved.getCalculator();
    }

    public void syncVars(Integer calculatorId) {
        // TODO Auto-generated method stub
        // get calculator by id from repository
        CalculatorEntity entity = calculatorRepository.findById(calculatorId)
                .orElseThrow(() -> new IllegalArgumentException("Calculator not found for id=" + calculatorId));
        CalculatorModel calculatorModel = entity.getCalculator();
        if (calculatorModel == null) {
            throw new IllegalStateException("Calculator JSON is null for id=" + calculatorId);
        }
        // get product from repository
        Product product = productService.getProduct(entity.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found for id=" + entity.getProductId());
        }
                
        // get product version from repository
        ProductVersionModel productVersion = productService.getVersion(entity.getProductId(), entity.getVersionNo());
        if (productVersion == null) {
            throw new IllegalArgumentException("Product version not found for id=" + entity.getProductId() + " and versionNo=" + entity.getVersionNo());
        }
            
            
        // get lob from repository
        LobModel lobModel = lobService.getByCode(product.getLob());
        if (lobModel == null) {
            throw new IllegalArgumentException("LOB not found for code=" + product.getLob());
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


