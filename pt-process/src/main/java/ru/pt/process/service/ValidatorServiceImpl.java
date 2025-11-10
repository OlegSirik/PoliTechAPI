package ru.pt.process.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.errors.ErrorModel;
import ru.pt.api.dto.errors.ValidationError;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.process.Cover;
import ru.pt.api.dto.process.CoverInfo;
import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.process.ValidatorType;
import ru.pt.api.dto.product.*;
import ru.pt.api.service.calculator.CalculatorService;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.api.service.process.PostProcessService;
import ru.pt.api.service.process.ValidatorService;
import ru.pt.api.service.product.LobService;
import ru.pt.api.service.product.ProductService;
import ru.pt.process.utils.JsonProjection;
import ru.pt.process.utils.JsonSetter;
import ru.pt.process.utils.PeriodUtils;
import ru.pt.process.utils.ValidatorImpl;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO разбить на классы
@Component
public class ValidatorServiceImpl implements ValidatorService {

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final LobService lobService;
    private final CalculatorService calculatorService;
    private final NumberGeneratorService numberGeneratorService;
    private final PostProcessService postProcessService;

    public ValidatorServiceImpl(
            ObjectMapper objectMapper,
            ProductService productService,
            LobService lobService,
            CalculatorService calculatorService,
            NumberGeneratorService numberGeneratorService, PostProcessService postProcessService
    ) {
        this.objectMapper = objectMapper;
        this.productService = productService;
        this.lobService = lobService;
        this.calculatorService = calculatorService;
        this.numberGeneratorService = numberGeneratorService;
        this.postProcessService = postProcessService;
    }

    @Override
    public List<ValidationError> validate(String policy, ValidatorType validatorType) {
        // TODO продолжить валидацию + добавить дозаполнение блока cover
        return List.of();
    }


    public ObjectNode createValidator(String requestBody, ValidatorType validatorType) {
        List<ValidationError> errorModel = new ArrayList<>();

        JsonProjection projection = new JsonProjection(requestBody);

        JsonSetter setter = new JsonSetter(requestBody);

        try {

            DocumentContext ctx = JsonPath.parse(requestBody);

            String productCode = projection.getProductCode();
            Integer packageCode = projection.getPackageCode();

            ProductVersionModel productVersionModel = productService.getProductByCode(productCode, true);

            LobModel lobModel = lobService.getByCode(productVersionModel.getLob());

            // Policy
            if (projection.getIssueDate() == null) {
                setter.setRawValue("issueDate", ZonedDateTime.now().toString());
            }

            // TODO add time zone
            try {
                String newJson = setActivationDelay(setter.writeValue(), productVersionModel);
                requestBody = setPolicyTerm(newJson, productVersionModel);
            } catch (Exception e) {
                errorModel.add(new ValidationError("activationDelay", "Error setting activation delay: " + e.getMessage(), "root"));
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
                    var.setVarValue(getMagicValue(lobVars, var.getVarCode(), requestBody));
                }
            }

            lobVars.add(new LobVar("product", "product", "product", "IN", productCode, VarDataType.STRING));
            lobVars.add(new LobVar("packageCode", "package", "package", "IN", packageCode.toString(), VarDataType.STRING));

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

                Map<String, LobVar> context = lobVars.stream()
                        .collect(Collectors.toMap(LobVar::getVarCode, Function.identity()));

                for (ValidatorRule validatorRule : validatorRules) {
                    boolean isValid = ValidatorImpl.validate(
                            context,
                            validatorRule
                    );

                    if (validatorRule.getErrorText().equals("AND")) {
                        isValidAnd = isValidAnd && isValid;
                    } else {
                        if (isValidAnd) {
                            if (!isValid) {
                                errorModel.add(
                                        new ValidationError(
                                                validatorRule.getKeyLeft() + " " + validatorRule.getKeyRight() + " " + validatorRule.getValueRight() + " " + validatorRule.getRuleType(),
                                                validatorRule.getErrorText(),
                                                "validation"
                                        ));
                            }

                        }
                        isValidAnd = true;
                    }
                }
            }

            if (!errorModel.isEmpty()) {
                // TODO change for regular error processing
                throw new IllegalStateException("Data is invalid, can't calculate policy!");
            }

            InsuredObject insObject = getInsuredObject(requestBody, productVersionModel);

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
                lobVars = calculatorService.runCalculator(
                        productVersionModel.getId(),
                        productVersionModel.getVersionNo(),
                        insObject.getPackageCode(),
                        lobVars
                );

                // INSERT_YOUR_CODE
            } catch (Exception e) {
                errorModel.add(new ValidationError("calculator", "Error running calculator: " + e.getMessage(), "calculator"));
            }

            insObject = postProcessService.setCovers(insObject, lobVars);

            setter.setObjectValue("insuredObject", insObject);

            try {
                // TODO не должно быть тут!!
                String policyNumber = numberGeneratorService.getNextNumber(getMapVars(lobVars), productCode);
                setter.setRawValue("policyNumber", policyNumber);
                lobVars.add(new LobVar("policyNumber", "Номер договора", "policy.policyNumber", "IN", policyNumber, VarDataType.STRING));
            } catch (Exception e) {
                errorModel.add(new ValidationError("policyNumber", "Error generating policy number: " + e.getMessage(), "numberGenerator"));
            }

            Double premium = 0.0;

            for (Cover cover : insObject.getCovers()) {
                if (cover.getPremium() != null) {
                    premium += cover.getPremium();
                }
            }

            setter.setRawValue("premium", premium.toString());

            response.put("policy", objectMapper.convertValue(setter.writeValue(), JsonNode.class));

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
            ErrorModel.ErrorDetail errorDetail = new ErrorModel.ErrorDetail("Policy", "error", e.getMessage(), "");
            ErrorModel errorModel1 = new ErrorModel(400, e.getMessage(), Arrays.asList(errorDetail));
            throw new BadRequestException(errorModel1);

        }
    }

    public InsuredObject getInsuredObject(String policy, ProductVersionModel policyVersionModel) {

        JsonProjection projection = new JsonProjection(policy);
        JsonSetter setter = new JsonSetter(policy);

        var insuredObject = projection.getInsuredObject();

        if (insuredObject == null || insuredObject.getCovers() == null) {
            var emptyInsuredObject = new InsuredObject();
            emptyInsuredObject.setCovers(new ArrayList<>());
            insuredObject = emptyInsuredObject;
        }
        Integer inPackageNo;
        if (insuredObject.getPackageCode() == null) {
            inPackageNo = 0;
        } else {
            inPackageNo = insuredObject.getPackageCode();
        }
        final Integer pkgCode = inPackageNo;

        PvPackage pvPackage = policyVersionModel.getPackages().stream()
                .filter(p -> p.getCode().equals(pkgCode))
                .findFirst()
                .orElse(null);

        if (pvPackage == null) {
            throw new IllegalArgumentException("Package not found: " + inPackageNo);
        }

        insuredObject.setPackageCode(pkgCode);

        List<PvCover> covers = pvPackage.getCovers();
        for (PvCover pvCover : covers) {
            // Check if the cover.code exists in policy.covers
            List<Cover> policyCovers = insuredObject.getCovers();
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
                newCover.setCover(new CoverInfo(pvCover.getCode(), "", ""));
                coverExists = true;
                insuredObject.getCovers().add(newCover);
            }
            if (coverExists) {
                Cover policyCover = policyCovers.stream()
                        .filter(c -> c.getCover() != null && c.getCover().getCode().equals(pvCover.getCode()))
                        .findFirst()
                        .orElse(null);
                if (policyCover != null) {
                    String waitingPeriod = pvCover.getWaitingPeriod();
                    if (waitingPeriod != null && !waitingPeriod.isEmpty()) {
                        ZonedDateTime startDate = projection.getStartDate().plus(Period.parse(waitingPeriod));
                        policyCover.setStartDate(startDate);
                    } else {
                        policyCover.setStartDate(projection.getStartDate());
                    }
                    String coverageTerm = pvCover.getCoverageTerm();
                    if (coverageTerm != null && !coverageTerm.isEmpty()) {
                        ZonedDateTime endDate = policyCover.getStartDate().plus(Period.parse(coverageTerm));
                        policyCover.setEndDate(endDate);
                    } else {
                        policyCover.setEndDate(projection.getEndDate());
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

        // setter.setObjectValue("insuredObject", insuredObject);

        return insuredObject;

    }


    public String setActivationDelay(String policy, ProductVersionModel policyVersionModel) {

        JsonProjection projection = new JsonProjection(policy);

        String validatorType = policyVersionModel.getWaitingPeriod().getValidatorType();

        String validatorValue = policyVersionModel.getWaitingPeriod().getValidatorValue();

        ZonedDateTime issueDate = projection.getIssueDate();

        ZonedDateTime startDate = projection.getStartDate();

        String waitingPeriod = projection.getWaitingPeriod();

        JsonSetter setter = new JsonSetter(policy);

        if (issueDate == null) {
            throw new IllegalAccessError("Issue date is required");
        }
        switch (validatorType) {
            case "RANGE":
                if (startDate == null) {
                    throw new IllegalAccessError("Start date is required");
                }

                if (!PeriodUtils.isDateInRange(issueDate, startDate, validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                setter.setRawValue("waitingPeriod", validatorValue);
                break;
            case "LIST":
                // список доступных значений из модели полиса
                // взять из договора policyTerm, проверить что это значение есть в списке. вычислить дату2
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                } else if (list.length == 1) {
                    waitingPeriod = list[0];
                } else {
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

                setter.setRawValue("startDate", startDate.toString());
                setter.setRawValue("waitingPeriod", waitingPeriod);
                break;
            case "NEXT_MONTH":
                startDate = issueDate.plus(Period.parse("P1M")).withDayOfMonth(1);
                setter.setRawValue("startDate", startDate.toString());
                break;
        }

        return setter.writeValue();
    }

    public String setPolicyTerm(String policy, ProductVersionModel policyVersionModel) {
        // activationDelay - RANGE LIST NEXT_MONTH
        String validatorType = policyVersionModel.getPolicyTerm().getValidatorType();
        String validatorValue = policyVersionModel.getPolicyTerm().getValidatorValue();

        var projection = new JsonProjection(policy);

        ZonedDateTime startDate = projection.getStartDate();
        ZonedDateTime endDate = projection.getEndDate();

        String policyTerm = projection.getPolicyTerm();

        JsonSetter setter = new JsonSetter(policy);

        if (startDate == null) {
            throw new BadRequestException("start date is required");
        }

        switch (validatorType) {
            case "RANGE":
                if (endDate == null) {
                    throw new BadRequestException("End date is required");
                }

                if (!PeriodUtils.isDateInRange(startDate, endDate, validatorValue)) {
                    throw new IllegalArgumentException("Activation delay is not in range");
                }
                setter.setRawValue("policyTerm", validatorValue);
                break;
            case "LIST":
                // должно быть startDate и policyTerm в договоре и policyTerms в модели полиса
                // список доступных значений из модели полиса
                String[] list = validatorValue.split(",");
                // если только одно значение, то только оно и возможно
                if (list.length == 0) {
                    throw new IllegalAccessError("validatorValue is invalid");
                } else if (list.length == 1) {
                    policyTerm = list[0];
                } else {
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

                setter.setRawValue("endDate", endDate.toString());
                setter.setRawValue("policyTerm", policyTerm);
                break;
        }

        return policy;
    }

    public static String getMagicValue(List<LobVar> varDefs, String key, String policy) {
        JsonProjection projection = new JsonProjection(policy);

        LobVar varDef;
        try {
            switch (key) {
                case "ph_isMale":
                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_gender"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        return "M".equals(varDef.getVarValue()) ? "X" : "";
                    }
                    return "";
                case "ph_isFemale":
                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_gender"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        return "F".equals(varDef.getVarValue()) ? "X" : "";
                    }
                    return "";
                case "ph_age_issue":

                    varDef = varDefs.stream()
                            .filter(v -> v.getVarCode().equals("ph_birthdate"))
                            .findFirst()
                            .orElse(null);
                    if (varDef != null) {
                        LocalDate birthDate = LocalDate.parse(varDef.getVarValue());
                        LocalDate issueDate = projection.getIssueDate().toLocalDate();
                        return Integer.toString(Period.between(birthDate, issueDate).getYears());
                    } else {
                        return null;
                    }
                case "io_age_issue":
                    try {
                        varDef = varDefs.stream()
                                .filter(v -> v.getVarCode().equals("io_birthDate"))
                                .findFirst()
                                .orElse(null);
                        if (varDef != null) {
                            return Integer.toString(
                                    Period.between(
                                            LocalDate.parse(varDef.getVarValue()), projection.getIssueDate().toLocalDate()
                                    ).getYears()
                            );
                        }
                    } catch (Exception e) {
                        return "-1";
                    }
                    return "";
                case "io_age_end":
                    try {
                        varDef = varDefs.stream()
                                .filter(v -> v.getVarCode().equals("io_birthDate"))
                                .findFirst()
                                .orElse(null);
                        if (varDef != null) {
                            return Integer.toString(
                                    Period.between(
                                                    LocalDate.parse(varDef.getVarValue()), projection.getEndDate().toLocalDate())
                                            .getYears()
                            );
                        }
                        return "-1";
                    } catch (Exception e) {
                        return "-1";
                    }
                case "policyTermMonths":
                    LocalDate st = projection.getStartDate().toLocalDate();
                    LocalDate ed = projection.getEndDate().toLocalDate();
                    Period p = Period.between(st, ed);
                    int m = p.getYears() * 12 + p.getMonths();
                    return Integer.toString(m);

                default:
                    return key + " Not Found";
            }
        } catch (Exception e) {
            return "";
        }
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
            if (Objects.equals(pvLimit.getSumInsured(), sumInsured)) {
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

        if (pvCover.getDeductibles() == null || pvCover.getDeductibles().isEmpty()) {
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
        if (pvCover.getIsDeductibleMandatory()) {
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

    public Map<String, Object> getMapVars(List<LobVar> lobVars) {
        Map<String, Object> mapVars = new HashMap<>();
        for (LobVar lobVar : lobVars) {
            mapVars.put(lobVar.getVarCode(), lobVar.getVarValue());
        }
        return mapVars;
    }

}
