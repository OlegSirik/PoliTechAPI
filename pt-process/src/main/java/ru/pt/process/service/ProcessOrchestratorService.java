package ru.pt.process.service;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.dto.db.PolicyStatus;
import ru.pt.api.dto.errors.ValidationError;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.payment.PaymentData;
import ru.pt.api.dto.process.Cover;
import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.process.ValidatorType;
import ru.pt.api.dto.product.LobModel;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.dto.versioning.Version;
import ru.pt.api.service.calculator.CalculatorService;
import ru.pt.api.service.db.StorageService;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.api.service.process.PostProcessService;
import ru.pt.api.service.process.PreProcessService;
import ru.pt.api.service.process.ProcessOrchestrator;
import ru.pt.api.service.process.ValidatorService;
import ru.pt.api.service.product.LobService;
import ru.pt.api.service.product.ProductService;
import ru.pt.api.service.product.VersionManager;
import ru.pt.process.utils.JsonProjection;
import ru.pt.process.utils.JsonSetter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class ProcessOrchestratorService implements ProcessOrchestrator {


    private final StorageService storageService;
    private final NumberGeneratorService numberGeneratorService;
    private final ProductService productService;
    private final VersionManager versionManager;
    private final CalculatorService calculatorService;
    private final LobService lobService;
    private final ValidatorService validatorService;
    private final PreProcessService preProcessService;
    private final PostProcessService postProcessService;

    public ProcessOrchestratorService(StorageService storageService, NumberGeneratorService numberGeneratorService, ProductService productService, VersionManager versionManager, CalculatorService calculatorService, LobService lobService, ValidatorService validatorService, PreProcessService preProcessService, PostProcessService postProcessService) {
        this.storageService = storageService;
        this.numberGeneratorService = numberGeneratorService;
        this.productService = productService;
        this.versionManager = versionManager;
        this.calculatorService = calculatorService;
        this.lobService = lobService;
        this.validatorService = validatorService;
        this.preProcessService = preProcessService;
        this.postProcessService = postProcessService;
    }

    @Override
    public String calculate(String policy) {

        var projection = new JsonProjection(policy);

        var productCode = projection.getProductCode();

        var product = productService.getProductByCode(productCode, false);

        LobModel lobModel = lobService.getByCode(productCode);

        policy = preProcessService.enrichPolicy(policy, product);

        List<LobVar> vars = preProcessService.evaluateAndEnrichVariables(policy, lobModel, productCode);

        List<ValidationError> errors = validatorService.validate(ValidatorType.QUOTE, product, vars);

        if (!errors.isEmpty()) {
            // TODO нормальную структуру под ошибки
            throw new BadRequestException(errors.stream()
                    .map(ValidationError::getReason)
                    .collect(Collectors.joining(","))
            );
        }

        InsuredObject insObject = preProcessService.getInsuredObject(policy, product);

        preProcessService.enrichVariablesBeforeCalculation(insObject, vars);

        List<LobVar> calculated = calculatorService.runCalculator(
                product.getId(), product.getVersionNo(), insObject.getPackageCode(), vars
        );

        insObject = postProcessService.setCovers(insObject, calculated);

        var setter = new JsonSetter(policy);

        setter.setObjectValue("insuredObject", insObject);

        Double premium = countPremium(insObject);

        setter.setRawValue("premium", premium.toString());

        return setter.writeValue();
    }


    @Override
    public String save(String policy) {

        String calculated = calculate(policy);

        var projection  = new JsonProjection(policy);

        var productCode = projection.getProductCode();

        var product = productService.getProductByCode(productCode, false);

        LobModel lobModel = lobService.getByCode(productCode);

        List<LobVar> vars = preProcessService.evaluateAndEnrichVariables(policy, lobModel, productCode);

        List<ValidationError> errors = validatorService.validate(ValidatorType.SAVE, product, vars);

        if (!errors.isEmpty()) {
            // TODO нормальную структуру под ошибки
            throw new BadRequestException(errors.stream()
                    .map(ValidationError::getReason)
                    .collect(Collectors.joining(","))
            );
        }

        var paramMap = projection.getProductMap(vars);

        var nextNumber = numberGeneratorService.getNextNumber(paramMap, productCode);

        var setter = new JsonSetter(calculated);

        setter.setRawValue("policyNumber", nextNumber);

        var userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var version = new Version(null, product.getVersionNo());

        storageService.save(policy, userData, version, projection.getPolicyId());

        // TODO async send KID + draft print form

        return setter.writeValue();
    }

    @Override
    public String update(String policy) {
        return "";
    }

    @Override
    public String createAddendum(String policy) {
        return "";
    }

    @Override
    public PaymentData payment(PaymentData paymentData) {
        return null;
    }

    @Override
    public PolicyData createPolicy(String policy) {
        var jsonProjection = new JsonProjection(policy);

        var productCode = jsonProjection.getProductCode();

        var version = versionManager.getLatestVersionByProductCode(productCode);

        var userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var uuid = UUID.fromString(MDC.get("correlationId"));

        return storageService.save(policy, userData, version, uuid);
    }

    @Override
    public PolicyData updatePolicy(String policyNumber, String policy) {
        var userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var policyData = storageService.getPolicyByNumber(policyNumber);
        if (policyData.getPolicyStatus() != PolicyStatus.NEW) {
            throw new BadRequestException("Can't update policy, bad policy status");
        }

        var policyIndex = policyData.getPolicyIndex();

        if (!policyIndex.getUserAccountId().equals(userData.getAccountId()) ||
                !policyIndex.getClientAccountId().equals(userData.getClientId())) {
            // TODO 403
            throw new BadRequestException("Unable to update policy");
        }

        var jsonProjection = new JsonProjection(policy);

        var productCode = jsonProjection.getProductCode();

        var version = versionManager.getLatestVersionByProductCode(productCode);

        return storageService.update(policy, userData, version, policyNumber);
    }

    @Override
    public PolicyData getPolicyById(UUID id) {
        var userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var policyData = storageService.getPolicyById(id);

        var policyIndex = policyData.getPolicyIndex();

        if (!policyIndex.getUserAccountId().equals(userData.getAccountId()) ||
                !policyIndex.getClientAccountId().equals(userData.getClientId())) {
            // TODO 403
            throw new BadRequestException("Unable to update policy");
        }

        return policyData;
    }

    @Override
    public PolicyData getPolicyByNumber(String policyNumber) {
        var userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var policyData = storageService.getPolicyByNumber(policyNumber);

        var policyIndex = policyData.getPolicyIndex();

        if (!policyIndex.getUserAccountId().equals(userData.getAccountId()) ||
                !policyIndex.getClientAccountId().equals(userData.getClientId())) {
            // TODO 403
            throw new BadRequestException("Unable to update policy");
        }

        return policyData;
    }

    private Double countPremium(InsuredObject insObject) {
        Double premium = 0.0;

        for (Cover cover : insObject.getCovers()) {
            if (cover.getPremium() != null) {
                premium += cover.getPremium();
            }
        }
        return premium;
    }

}
