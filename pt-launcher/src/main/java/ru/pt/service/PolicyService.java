package ru.pt.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.domain.FileEntity;
import ru.pt.domain.Product;

import ru.pt.domain.error.ErrorModel.ErrorDetail;
import ru.pt.domain.error.ErrorModel;
import ru.pt.domain.error.ValidationError;

import ru.pt.domain.lob.LobModel;
import ru.pt.domain.lob.LobVar;
import ru.pt.domain.lob.VarDataType;
import ru.pt.domain.policy.Cover;
import ru.pt.domain.policy.CoverInfo;
import ru.pt.domain.policy.InsuredObject;
import ru.pt.domain.policy.Policy;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.domain.productVersion.PvCover;
import ru.pt.domain.productVersion.PvDeductible;
import ru.pt.domain.productVersion.PvLimit;
import ru.pt.domain.productVersion.PvPackage;
import ru.pt.domain.productVersion.ValidatorRule;
import ru.pt.domain.productVersion.ValidatorType;
import ru.pt.exception.BadRequestException;


import ru.pt.hz.PeriodUtils;
import ru.pt.repository.FileRepository;

import ru.pt.repository.ProductVersionRepository;


@Service
public class PolicyService {

    private final FileRepository fileRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final FileService fileService;
    private final CalculatorService calculatorService;

    //private final CalculatorImpl calculatorImpl;
    private final NumberGeneratorService numberGeneratorService;
    private final LobService lobService;
    public PolicyService(
                       ProductService productService,
                       ObjectMapper objectMapper,
                       ProductVersionRepository productVersionRepository,
                       FileRepository fileRepository,
                       FileService fileService,
                       CalculatorService calculatorService,
                       NumberGeneratorService numberGeneratorService,
                       LobService lobService) 
    {
        this.fileRepository = fileRepository;
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.fileService = fileService;
        this.calculatorService = calculatorService;
        this.numberGeneratorService = numberGeneratorService;
        this.lobService = lobService;
    }

    public ObjectNode quoteValidator(String requestBody) {
        return Validator(requestBody,ValidatorType.QUOTE);
    }

    public ObjectNode saveValidator(String requestBody) {
        return Validator(requestBody,ValidatorType.SAVE);
    }

    public ObjectNode Validator(String requestBody, ValidatorType validatorType) {
        List<ValidationError> errorModel = new ArrayList<ValidationError>();

        try {

            DocumentContext ctx = JsonPath.parse(requestBody);
            Policy policy = objectMapper.convertValue(ctx.read("$"), Policy.class);

            String productCode = ctx.read("$.product.code", String.class);
            String packageCode = ctx.read("insuredObject.packageCode", String.class);

            Product product = productService.getProductByCode(productCode);

            if (product.getDevVersionNo() == null) {
                throw new IllegalArgumentException("Product has no dev version");
            }

            ProductVersionModel productVersionModel = productService.getVersion(product.getId(), product.getDevVersionNo());

            LobModel lobModel = lobService.getByCode(product.getLob());

            // Policy
            if (policy.getIssueDate() == null) {
                policy.setIssueDate(OffsetDateTime.now());
            }
            // ToDO add time zone
            try {
            policy = setActivationDelay(policy, productVersionModel);
            policy = setPolicyTerm(policy, productVersionModel);
            } catch (Exception e) {
                errorModel.add(new ValidationError("activationDelay", "Error setting activation delay: " + e.getMessage()));
                throw e;
            }
            // Fill Key-Value pairs for LOB Variables
            List<LobVar> lobVars = lobModel.getMpVars();
            
            ObjectNode response = objectMapper.createObjectNode();

                for (LobVar var : lobVars) {
                    if ("IN".equals(var.getVarType())) {
                        try {
                            String value = ctx.read(var.getVarPath());
                            var.setVarValue(value == null ? "" : value);
                        } catch (Exception e) {
                            var.setVarValue("");
                        }
                    }
                }
                    
            for (LobVar var : lobVars) {
                if ("MAGIC".equals(var.getVarType())) {
                    var.setVarValue(getMagicValue(lobVars, var.getVarCode(), policy));
                }
            }

            lobVars.add(new LobVar("product", "product", "product", "IN", productCode, VarDataType.STRING));
            lobVars.add(new LobVar("packageCode", "package", "package", "IN", packageCode, VarDataType.STRING));

            List<ValidatorRule> validatorRules = null;

            if (ValidatorType.QUOTE.equals(validatorType)) {
                validatorRules = productVersionModel.getQuoteValidator();
            } else if (ValidatorType.SAVE.equals(validatorType)) {
                validatorRules = productVersionModel.getSaveValidator();
            }
            if (validatorRules != null) {
                // sort validatorRules by lineNr
                validatorRules.sort(Comparator.comparingInt(v -> v.getLineNr() != null ? v.getLineNr() : 0));

                boolean isValidAnd = true;

                for (ValidatorRule validatorRule : validatorRules) {
                    // ToDO change it 
                    boolean isValid = ValidatorImpl.validate(lobVars, validatorRule.getKeyLeft(), validatorRule.getKeyRight(), validatorRule.getValueRight(), validatorRule.getRuleType());

                    if (validatorRule.getErrorText().equals("AND")) {
                        isValidAnd = isValidAnd && isValid;
                    } else {
                        if (isValidAnd) {
                            if (!isValid) {
                                errorModel.add(new ValidationError(validatorRule.getKeyLeft() + " " + validatorRule.getKeyRight() + " " + validatorRule.getValueRight() + " " + validatorRule.getRuleType(), validatorRule.getErrorText()));
                            }
                        
                        }
                        isValidAnd = true;
                    }
                }
            }
            
            InsuredObject insObject = getInsuredObject( policy,  productVersionModel);

            // INSERT_YOUR_CODE
            if (insObject != null && insObject.getCovers() != null) {
                for (Cover cover : insObject.getCovers()) {
                    // You can add your logic here for each cover
                    // For example, you could log, validate, or manipulate cover objects
                    // Example: System.out.println("Cover code: " + (cover != null && cover.getCover() != null ? cover.getCover().getCode() : "null"));
                    if (cover.getCover() != null) {
                        Double sumInsured = cover.getSumInsured();
                        Double premium = cover.getPremium();
                        //Double deductibleNr = cover.getDeductible();
                        
                        String sumInsuredVarCode = cover.getCover().getCode() + "_SumIns";
                        String premiumVarCode = cover.getCover().getCode() + "_Prem";
                        String deductibleNrVarCode = cover.getCover().getCode() + "_DedNr";

                        LobVar lobVar = new LobVar();
                        lobVar.setVarCode(sumInsuredVarCode);
                        lobVar.setVarValue(sumInsured != null ? sumInsured.toString() : null);
                        lobVar.setVarType("VAR");
                        lobVars.add(lobVar);
                        
                        lobVar = new LobVar();
                        lobVar.setVarCode(premiumVarCode);
                        lobVar.setVarValue(premium != null ? premium.toString() : null);
                        lobVar.setVarType("VAR");
                        lobVars.add(lobVar);
                        
                        //lobVar = new LobVar();
                        //lobVar.setVarCode(deductibleNrVarCode);
                        //lobVar.setVarValue(deductibleNr != null ? deductibleNr.toString() : null);
                        //lobVar.setVarType("VAR");
                        //lobVars.add(lobVar);
                    }
                }
            }
            try {
            lobVars = calculatorService.runCalculator(product.getId(), product.getDevVersionNo(), insObject.getPackageCode(), lobVars);

            // INSERT_YOUR_CODE
            } catch (Exception e) {
//                throw new IllegalArgumentException("Error running calculator: " + e.getMessage());
                errorModel.add(new ValidationError("calculator", "Error running calculator: " + e.getMessage()));
            }

            if (insObject != null && insObject.getCovers() != null) {
                for (Cover cover : insObject.getCovers()) {
                    if (cover == null || cover.getCover() == null) continue;
                    String sumInsuredVarCode = cover.getCover().getCode() + "_SumIns";
                    String premiumVarCode = cover.getCover().getCode() + "_Prem";
                    String deductibleNrVarCode = cover.getCover().getCode() + "_DedNr";

                    // Find values in lobVars
                    String sumInsured = lobVars.stream()
                        .filter(v -> sumInsuredVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse(null);

                    String premium = lobVars.stream()
                        .filter(v -> premiumVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse("0.0");

                    String deductibleNr = lobVars.stream()
                        .filter(v -> deductibleNrVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse(null);

                    // Set values to cover if setters exist
                    if (sumInsured != null) {
                        try { cover.setSumInsured(Double.parseDouble(sumInsured)); } catch (Exception ignored) {}
                    }
                    if (premium != null) {
                        try { cover.setPremium(Double.parseDouble(premium)); } catch (Exception ignored) {}
                    }
                    if (deductibleNr != null) {
                        try { cover.setDeductible(Double.parseDouble(deductibleNr)); } catch (Exception ignored) {}
                    }
                }
            }

           
            policy.setInsuredObject(insObject);

            try {
                String policyNumber = numberGeneratorService.getNextNumber(getMapVars(lobVars), productCode);
                policy.setPolicyNumber(policyNumber);
                lobVars.add(new LobVar("policyNumber", "Номер договора", "policy.policyNumber", "IN", policyNumber, VarDataType.STRING));
            } catch (Exception e) {
                errorModel.add(new ValidationError("policyNumber", "Error generating policy number: " + e.getMessage()));
            }

            Double premium = 0.0;

            for ( Cover cover : policy.getInsuredObject().getCovers() ) {
                if (cover.getPremium() != null) {
                    premium += cover.getPremium();
                }
            }

            policy.setPremium(premium);

            response.put("policy", objectMapper.convertValue(policy, JsonNode.class));

            // Build context from LOB variables
            ArrayNode context = response.putArray("context");
            
            context.addAll(lobVars.stream()
                    .map(var -> {
                        ObjectNode contextItem = objectMapper.createObjectNode();
                        contextItem.put("varCode", var.getVarCode());
                        contextItem.put("varValue", var.getVarValue());
                        return contextItem;
                    })
                    .collect(Collectors.toList()));
            
            response.put("errorText", objectMapper.convertValue(errorModel, JsonNode.class));
            

            return response;
        } catch (Exception e) {
            ErrorDetail errorDetail = new ErrorDetail("Policy", "error", e.getMessage(), "");
            ErrorModel errorModel1 = new ErrorModel(400, e.getMessage(), Arrays.asList(errorDetail));
            throw new BadRequestException(errorModel1);
           
        }
    }

    public Map<String, String> getMapVars(List<LobVar> lobVars) {
        Map<String, String> mapVars = new HashMap<>();
        for (LobVar lobVar : lobVars) {
            mapVars.put(lobVar.getVarCode(), lobVar.getVarValue());
        }
        return mapVars;
    }

    public Policy setActivationDelay(Policy policy, ProductVersionModel policyVersionModel) {
        // activationDelay - RANGE LIST NEXT_MONTH
        String validatorType = policyVersionModel.getWaitingPeriod().getValidatorType();
        String validatorValue = policyVersionModel.getWaitingPeriod().getValidatorValue();
        // get issueDate,startDate,endDate, activationDelay, policyTerm
        OffsetDateTime issueDate = policy.getIssueDate();
        OffsetDateTime startDate = policy.getStartDate();
//        OffsetDateTime endDate = policy.getEndDate();
        String waitingPeriod = policy.getWaitingPeriod();
//        String policyTerm = policy.getPolicyTerm();

        if (issueDate == null) {
            throw new IllegalAccessError("Issue date is required");
        }
        switch (validatorType) {
            case "RANGE":
                if (startDate == null) {
                    throw new IllegalAccessError("Start date is required");
                }

                if ( !PeriodUtils.isDateInRange(policy.getIssueDate(), policy.getStartDate(), validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                policy.setWaitingPeriod(validatorValue);
                break;
            case "LIST":
                // список доступных значений из модели полиса
                // взять из договора policyTerm, проверить что это значение есть в списке. вычислить дату2
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                }
                else if (list.length == 1) {
                    waitingPeriod = list[0];
                }
                else {
                    if (waitingPeriod == null) {
                        throw new IllegalAccessError("Waiting period is required");
                    }
    
                    boolean found = false;
                    // check if policyTerm is in list array. loop through list and check if policyTerm is in list
                    for (String period : list) {
                        if (waitingPeriod.equals(period.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalAccessError("Waiting period is not in list");
                    }
                }

                startDate = issueDate.plus(Period.parse(waitingPeriod));

                policy.setStartDate(startDate);
                policy.setWaitingPeriod(waitingPeriod);
                break;
            case "NEXT_MONTH":
                startDate = issueDate.plus(Period.parse("P1M")).withDayOfMonth(1);
                policy.setStartDate(startDate);
                break;
        }

        return policy;
    }

    public Policy setPolicyTerm(Policy policy, ProductVersionModel policyVersionModel) {
        // activationDelay - RANGE LIST NEXT_MONTH
        String validatorType = policyVersionModel.getPolicyTerm().getValidatorType();
        String validatorValue = policyVersionModel.getPolicyTerm().getValidatorValue();
        // get issueDate,startDate,endDate, activationDelay, policyTerm
//        OffsetDateTime issueDate = policy.getIssueDate();
        OffsetDateTime startDate = policy.getStartDate();
        OffsetDateTime endDate = policy.getEndDate();
//        String waitingPeriod = policy.getWaitingPeriod();
        String policyTerm = policy.getPolicyTerm();

        if (startDate == null) {
            throw new BadRequestException("start date is required");
        }

        switch (validatorType) {
            case "RANGE":
                if (endDate == null) {
                    throw new BadRequestException("End date is required");
                }

                if ( !PeriodUtils.isDateInRange(policy.getStartDate(), policy.getEndDate(), validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                policy.setPolicyTerm(validatorValue);
                break;
            case "LIST":
                // должно быть startDate и policyTerm в договоре и policyTerms в модели полиса
                // список доступных значений из модели полиса
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                }
                else if (list.length == 1) {
                    policyTerm = list[0];
                }
                else {
                    if (policyTerm == null) {
                        throw new IllegalAccessError("Policy term is required");
                    }

                    boolean found = false;
                    // check if policyTerm is in list array. loop through list and check if policyTerm is in list
                    for (String period : list) {
                        if (policyTerm.equals(period.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Policy term is not in list");
                    }
                }

                endDate = startDate.plus(Period.parse(policyTerm));

                policy.setEndDate(endDate);
                policy.setPolicyTerm(policyTerm);
                break;
        }

        return policy;
    }



    public InsuredObject getInsuredObject(Policy policy, ProductVersionModel policyVersionModel) {
        // if no insObject in policy then create new one
        if (policy.getInsuredObject() == null) {
            policy.setInsuredObject(new InsuredObject());
        }
        // if new covers in insObject then add new empty covers
        if (policy.getInsuredObject().getCovers() == null) {
            policy.getInsuredObject().setCovers(new ArrayList<>());
        }
        Integer inPackageNo = policy.getInsuredObject().getPackageCode();
        if (inPackageNo == null ) inPackageNo = 0;
        final Integer pkgCode = inPackageNo;

        PvPackage pvPackage = policyVersionModel.getPackages().stream().filter(p -> p.getCode().equals(pkgCode)).findFirst().orElse(null);
        if (pvPackage == null) {
            throw new IllegalArgumentException("Package not found: " + inPackageNo);
        }

        policy.getInsuredObject().setPackageCode(pkgCode);

        List<PvCover> covers = pvPackage.getCovers();
        for (PvCover pvCover : covers) {
        // Check if the cover.code exists in policy.covers
            List<Cover> policyCovers = policy.getInsuredObject().getCovers();
            boolean coverExists = false;
            if (policyCovers != null) {
                for (Cover policyCover : policyCovers) {
                    if (policyCover != null && policyCover.getCover() != null && pvCover.getCode().equals(policyCover.getCover().getCode())) {
                        coverExists = true;
                        break;
                    }
                } 
            }
            if (!coverExists && pvCover.getIsMandatory()) {
                Cover newCover = new Cover();
                newCover.setCover( new CoverInfo(pvCover.getCode(), "", ""));
                coverExists = true;
                policy.getInsuredObject().getCovers().add(newCover);
            }
            if (coverExists) {
                Cover policyCover = policyCovers.stream().filter(c -> c.getCover() != null && c.getCover().getCode().equals(pvCover.getCode())).findFirst().orElse(null);
                if (policyCover != null) {
                    String waitingPeriod = pvCover.getWaitingPeriod();
                    if (waitingPeriod != null && !waitingPeriod.isEmpty()) {
                        OffsetDateTime startDate = policy.getStartDate().plus(Period.parse(waitingPeriod));
                        policyCover.setStartDate(startDate);
                    } else {
                        policyCover.setStartDate(policy.getStartDate());  
                    }
                    String coverageTerm = pvCover.getCoverageTerm();
                    if (coverageTerm != null && !coverageTerm.isEmpty()) {
                        OffsetDateTime endDate = policyCover.getStartDate().plus(Period.parse(coverageTerm));
                        policyCover.setEndDate(endDate);
                    } else {
                        policyCover.setEndDate(policy.getEndDate());  
                    }
             

                    PvLimit pvLimit = getPvLimit(pvCover, policyCover.getSumInsured());
                    if (pvLimit != null) {
                        policyCover.setSumInsured(pvLimit.getSumInsured());
                        policyCover.setPremium(pvLimit.getPremium());
                    }

                    policyCover.setDeductibleCur(null);
                    policyCover.setDeductibleMin(null);
                    policyCover.setDeductiblePercent(null);
                    

                    PvDeductible pvDeductible = getPvDeductible(pvCover, policyCover);
                    if (pvDeductible != null) {
                        policyCover.setDeductible(pvDeductible.getDeductible());
                        policyCover.setDeductibleType(pvDeductible.getDeductibleType());
                        policyCover.setDeductibleSpecific(pvDeductible.getDeductibleSpecific());
                        policyCover.setDeductibleUnit(pvDeductible.getDeductibleUnit());
                    } else {
                        policyCover.setDeductible(null);
                        policyCover.setDeductibleType(null);
                        policyCover.setDeductibleSpecific(null);
                        policyCover.setDeductibleUnit(null);
                    }

                    policyCover.setCover(new CoverInfo(pvCover.getCode(), "", ""));
                }
            }
        }
        return policy.getInsuredObject();

    }

    public PvLimit getPvLimit(PvCover pvCover, Double sumInsured) {
        
        // если на покрытии только 1 лимит то он является единственно возможным
        // иначе проверяем, что переданная страховая сумма есть в списке возможных сумм
        if (pvCover.getLimits() != null && pvCover.getLimits().size() == 1) {
            return pvCover.getLimits().get(0);
        }

        if (sumInsured == null) {
            return null;
        }
    
        
        for (PvLimit pvLimit : pvCover.getLimits()) {
            if (pvLimit.getSumInsured() == sumInsured) {
                return pvLimit;
            }
        }
        return null;
    }

    public PvDeductible getPvDeductible(PvCover pvCover, Cover policyCover) {
        // если франшиза обязательна и в списке только одно значение то берем его
        // если франшиза обязательна а щапросе не ередена ничего, то берем франшизу с минимальным номером
        // если чтото передано, то проверяем по списку что это значение есть
        Double deductible = policyCover.getDeductible();
        String deductibleType = policyCover.getDeductibleType();
        String deductibleSpecific = policyCover.getDeductibleSpecific();
        String deductibleUnit = policyCover.getDeductibleUnit();

        if (pvCover.getDeductibles() == null || pvCover.getDeductibles().size() == 0) {
            return null;
        }
        for (PvDeductible pvDed : pvCover.getDeductibles()) {
            boolean deductibleMatch = deductible != null && deductible.equals(pvDed.getDeductible());
            boolean typeMatch = deductibleType != null && deductibleType.equals(pvDed.getDeductibleType());
            boolean specificMatch = deductibleSpecific != null && deductibleSpecific.equals(pvDed.getDeductibleSpecific());
            boolean unitMatch = deductibleUnit != null && deductibleUnit.equals(pvDed.getDeductibleUnit());
            if (deductibleMatch && typeMatch && specificMatch && unitMatch) {
                return pvDed;
            }
        }
        if ( pvCover.getIsDeductibleMandatory() ) {
            List<PvDeductible> deductibles = pvCover.getDeductibles();
            if (deductibles != null && !deductibles.isEmpty()) {
                deductibles.sort(java.util.Comparator.comparingInt(d -> {
                // Try to get "nr" property, default to Integer.MAX_VALUE if not present or not a number
                try {
                    java.lang.reflect.Method getNr = d.getClass().getMethod("getNr");
                    Object nrObj = getNr.invoke(d);
                    if (nrObj instanceof Number) {
                        return ((Number) nrObj).intValue();
                    } else if (nrObj != null) {
                        return Integer.parseInt(nrObj.toString());
                    }
                } catch (Exception e) {
                    // ignore and use max value
                }
                return Integer.MAX_VALUE;
            }));
            return deductibles.get(0);
        }
        }

        return null;
    }

    public ObjectNode quoteCalculator(String requestBody) {
        
        return Validator(requestBody,ValidatorType.QUOTE);
        
    }

    public ObjectNode policyValidator(String requestBody) {
        return Validator(requestBody,ValidatorType.SAVE);
    }

    public String policyCalculator(String requestBody) {
        return requestBody;
    }

    public byte[] printpf(String pf_type, String requestBody) {
        // TODO Auto-generated method stub
        ObjectNode dataMap = quoteValidator(requestBody);
        // Take context[] from dataMap and loop through its objects
        java.util.Map<String, String> kv = new java.util.HashMap<>();
        if (dataMap.has("context") && dataMap.get("context").isArray()) {
            for (JsonNode obj : dataMap.get("context")) {
                String varCode = obj.has("varCode") ? obj.get("varCode").asText() : null;
                String varValue = obj.has("varValue") ? obj.get("varValue").asText() : null;
                if (varCode != null && varValue != null) {
                    kv.put(varCode, varValue);
                }
            }
        }
        String productCode = dataMap.get("product").asText();

        FileEntity fileEntity = fileRepository.findActiveByProductandFileType(productCode, pf_type);

              
        //
        //throw new UnsupportedOperationException("Unimplemented method 'printpf'");
//        java.util.Map<String, String> kv = new java.util.HashMap<>();
//        for (Map<String, String> p : pairs) {
//            kv.put(p.get("key"), p.get("value"));
//        }
        return fileService.process(fileEntity.getId(), kv);
        
    }
    
    // Временно тут
    public static String getMagicValue(List<LobVar> varDefs, String key, Policy policy) {
        LobVar varDef = null;
        try {
        switch (key) {
            case "ph_isMale":
                varDef = varDefs.stream().filter(v -> v.getVarCode().equals("ph_gender")).findFirst().orElse(null);
                if (varDef != null) {
                    return "M".equals(varDef.getVarValue()) ? "X" : "";
                }
                return "";
            case "ph_isFemale":
                varDef = varDefs.stream().filter(v -> v.getVarCode().equals("ph_gender")).findFirst().orElse(null);
                if (varDef != null) {
                    return "F".equals(varDef.getVarValue()) ? "X" : "";
                }
                return "";
            case "ph_age_issue":

                varDef = varDefs.stream().filter(v -> v.getVarCode().equals("ph_birthdate")).findFirst().orElse(null);
                LocalDate birthDate = LocalDate.parse(varDef.getVarValue());
                LocalDate issueDate = policy.getIssueDate().toLocalDate();
                if (varDef != null) {
                    // find year between localdate policy.getIssueDate() and localdate varDef.getVarValue()
                    String age = Integer.toString(Period.between(birthDate, issueDate).getYears());
                    return age;
                }
                return "";
            case "io_age_issue":
                try {
                varDef = varDefs.stream().filter(v -> v.getVarCode().equals("io_birthDate")).findFirst().orElse(null);
                if (varDef != null) {
                    return Integer.toString(Period.between(LocalDate.parse(varDef.getVarValue()), policy.getIssueDate().toLocalDate()).getYears());
                }
            } catch (Exception e) {
                return "-1";
            }
                return "";
            case "io_age_end":
                try {
                varDef = varDefs.stream().filter(v -> v.getVarCode().equals("io_birthDate")).findFirst().orElse(null);
                if (varDef != null) {
                    return Integer.toString(Period.between(LocalDate.parse(varDef.getVarValue()), policy.getEndDate().toLocalDate()).getYears());
                }
                return "-1";
            } catch (Exception e) {
                return "-1";
            }
            case "policyTermMonths":
                LocalDate st = policy.getStartDate().toLocalDate();
                LocalDate ed = policy.getEndDate().toLocalDate();
                Period p = Period.between(st,ed);
                int m = p.getYears() * 12 + p.getMonths();
                return Integer.toString(m);
                
            default:
            return key +" Not Found";
        }
        } catch (Exception e) {
            return "";
        } 
    }
 
}
