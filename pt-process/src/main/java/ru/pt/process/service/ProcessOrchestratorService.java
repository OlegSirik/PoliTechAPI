package ru.pt.process.service;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.dto.db.PolicyIndex;
import ru.pt.api.dto.db.PolicyStatus;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.payment.PaymentData;
import ru.pt.api.dto.versioning.Version;
import ru.pt.api.service.db.StorageService;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.api.service.process.ProcessOrchestrator;
import ru.pt.api.service.product.ProductService;
import ru.pt.api.service.product.VersionManager;
import ru.pt.process.utils.JsonProjection;

import java.util.UUID;


@Component
public class ProcessOrchestratorService implements ProcessOrchestrator {


    private final StorageService storageService;
    private final NumberGeneratorService numberGeneratorService;
    private final ProductService productService;
    private final VersionManager versionManager;

    public ProcessOrchestratorService(StorageService storageService, NumberGeneratorService numberGeneratorService, ProductService productService, VersionManager versionManager) {
        this.storageService = storageService;
        this.numberGeneratorService = numberGeneratorService;
        this.productService = productService;
        this.versionManager = versionManager;
    }
    // TODO нужен модуль калькулятора
    @Override
    public String calculate(String policy) {
        return "";
    }

    @Override
    public String save(String policy) {
        return "";
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

}
