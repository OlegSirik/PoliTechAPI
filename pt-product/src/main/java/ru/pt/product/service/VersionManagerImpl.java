package ru.pt.product.service;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.dto.product.ProductVersionModel;
import ru.pt.api.dto.versioning.Version;
import ru.pt.api.service.db.StorageService;
import ru.pt.api.service.product.ProductService;
import ru.pt.api.service.product.VersionManager;

@Component
public class VersionManagerImpl implements VersionManager {

    private final ProductService productService;
    private final StorageService storageService;

    public VersionManagerImpl(ProductService productService, StorageService storageService) {
        this.productService = productService;
        this.storageService = storageService;
    }

    @Override
    public Version getVersionByPolicyNumber(String policyNumber) {
        PolicyData policyByNumber = storageService.getPolicyByNumber(policyNumber);
        var versionNo = policyByNumber.getPolicyIndex().getVersionNo();
        var versionStatus = policyByNumber.getPolicyIndex().getVersionStatus();
        var version = new Version();
        if (versionStatus.equals("PROD")) {
            version.setProdVersion(versionNo);
        } else {
            version.setDevVersion(versionNo);
        }
        return version;
    }

    @Override
    public Version getLatestVersionByProductCode(String productCode) {
        try {
            ProductVersionModel productByCode = productService.getProductByCode(productCode, false);
            var version = new Version();
            version.setDevVersion(productByCode.getVersionNo());
            return version;
        } catch (Exception e) {
            ProductVersionModel productByCode = productService.getProductByCode(productCode, false);
            var version = new Version();
            version.setProdVersion(productByCode.getVersionNo());
            return version;
        }
    }

    // TODO надо дебажить
    @Override
    public void setVersion(String policyNumber, Version version) {
        PolicyData policyByNumber = storageService.getPolicyByNumber(policyNumber);
        policyByNumber.getPolicyIndex().setVersionNo(version.getDevVersion() == null ? version.getProdVersion() : version.getDevVersion());
        storageService.update(policyByNumber);
    }

    @Override
    public void updateVersion(String policyNumber, Version version) {
        setVersion(policyNumber, version);
    }

}
