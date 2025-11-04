package ru.pt.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import ru.pt.api.dto.numbers.NumberGeneratorDescription;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.domain.productVersion.PvPackage;
import ru.pt.domain.productVersion.PvVar;
import ru.pt.domain.lob.LobModel;
import ru.pt.domain.lob.LobVar;
import ru.pt.exception.BadRequestException;
import ru.pt.hz.JsonExampleBuilder;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final NumberGeneratorService numberGeneratorService;
    private final LobService lobService;

    public ProductService(ProductRepository productRepository,
                          ProductVersionRepository productVersionRepository,
                          NumberGeneratorService numberGeneratorService,
                          LobService lobService) {
        this.productRepository = productRepository;
        this.productVersionRepository = productVersionRepository;
        this.numberGeneratorService = numberGeneratorService;
        this.lobService = lobService;
    }

    public List<Map<String, Object>> listSummaries() {
        return productRepository.listActiveSummaries().stream()
                .map(r -> {
                    java.util.LinkedHashMap<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id", r[0]);
                    m.put("lob", r[1]);
                    m.put("code", r[2]);
                    m.put("name", r[3]);
                    m.put("prodVersionNo", r[4]);
                    m.put("devVersionNo", r[5]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductVersionModel create(ProductVersionModel productVersionModel) {
        if (productVersionModel.getLob() == null || productVersionModel.getLob().trim().isEmpty()) {
            throw new BadRequestException("lob must not be empty");
        }
        if (productVersionModel.getCode() == null || productVersionModel.getCode().trim().isEmpty()) {
            throw new BadRequestException("code must not be empty");
        }
        if (productVersionModel.getName() == null || productVersionModel.getName().trim().isEmpty()) {
            throw new BadRequestException("name must not be empty");
        }

        Integer id = productRepository.getNextProductId();

        Product product = new Product();
        product.setId(id);
        product.setProdVersionNo(null);
        product.setDevVersionNo(1);
        product.setDeleted(false);
        product.setLob(productVersionModel.getLob());
        product.setCode(productVersionModel.getCode());
        product.setName(productVersionModel.getName());
        productRepository.save(product);

        productVersionModel.setId(id);
        productVersionModel.setVersionNo(1);
        productVersionModel.setVersionStatus("DEV");
 
        if (productVersionModel.getQuoteValidator() == null) {
            productVersionModel.setQuoteValidator(new ArrayList<>());
        }
        if (productVersionModel.getSaveValidator() == null) {
            productVersionModel.setSaveValidator(new ArrayList<>());
        }
        if (productVersionModel.getPackages() == null ) {
            productVersionModel.setPackages(new ArrayList<>());
            }
        if (productVersionModel.getPackages().size() == 0) {
            PvPackage pvPackage = new PvPackage();
            pvPackage.setCode(0);
            pvPackage.setName("0");
            productVersionModel.getPackages().add(pvPackage);
            pvPackage.setCovers(new ArrayList<>());
        }

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(1);
        pv.setProduct(productVersionModel);
        
// copy lob.mpVars to productVersionModel.vars
        LobModel lob = lobService.getByCode(productVersionModel.getLob());
        if (lob != null) {
            if (productVersionModel.getVars() == null) {
                productVersionModel.setVars(new ArrayList<>());
            }
            for (LobVar var : lob.getMpVars()) {
                PvVar pvVar = new PvVar();
                pvVar.setVarCode(var.getVarCode());
                pvVar.setVarName(var.getVarName());
                pvVar.setVarPath(var.getVarPath());
                pvVar.setVarType(var.getVarType());
                pvVar.setVarValue(var.getVarValue());
                pvVar.setVarDataType(var.getVarDataType());
                productVersionModel.getVars().add(pvVar);
            }
        }


        productVersionRepository.save(pv);

        //if productVersion.getNumberGenerator() is not null, then create a new number generator
        if (productVersionModel.getNumberGeneratorDescription() != null) {
            var numberGeneratorDescription = createNumberGeneratorDescription(productVersionModel);
            numberGeneratorService.create(numberGeneratorDescription);

        }
        return productVersionModel;
    }

    private static NumberGeneratorDescription createNumberGeneratorDescription(ProductVersionModel productVersionModel) {
        NumberGeneratorDescription numberGeneratorDescription = new NumberGeneratorDescription();
        numberGeneratorDescription.setId(productVersionModel.getId());
        numberGeneratorDescription.setMask(productVersionModel.getNumberGeneratorDescription().getMask());
        numberGeneratorDescription.setMaxValue(productVersionModel.getNumberGeneratorDescription().getMaxValue());
        numberGeneratorDescription.setProductCode(productVersionModel.getCode());
        numberGeneratorDescription.setResetPolicy(productVersionModel.getNumberGeneratorDescription().getResetPolicy());
        return numberGeneratorDescription;
    }

    public ProductVersionModel getVersion(Integer id, Integer versionNo) {
        try {
        ProductVersion pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo).orElse(null);
                
        ProductVersionModel productVersionModel = pv.getProduct();
        return productVersionModel;
        } catch (Exception e) {
            throw new IllegalArgumentException("Version not found");
        }
    }

    @Transactional
    public ProductVersion createVersionFrom(Integer id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() != null) {
            throw new IllegalArgumentException("only one version can be in dev status");
        }
        int newVersion = product.getProdVersionNo() == null ? 1 : product.getProdVersionNo() + 1;

        ProductVersionModel productVersionModel = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Base version not found"))
                .getProduct();

        productVersionModel.setVersionNo(newVersion);
        productVersionModel.setVersionStatus("DEV");

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(newVersion);
        pv.setProduct(productVersionModel);
        productVersionRepository.save(pv);

        product.setDevVersionNo(newVersion);
        productRepository.save(product);
        return pv;
    }

    @Transactional
    public ProductVersionModel updateVersion(Integer id, Integer versionNo, ProductVersionModel newProductVersionModel) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be updated");
        }

        newProductVersionModel.setId(id);
        newProductVersionModel.setVersionNo(versionNo);
        newProductVersionModel.setVersionStatus("DEV");

        ProductVersion pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
        pv.setProduct(newProductVersionModel);
        productVersionRepository.save(pv);

        if (newProductVersionModel.getNumberGeneratorDescription() != null) {
            var description = createNumberGeneratorDescription(newProductVersionModel);
            numberGeneratorService.create(description);

        }

        return newProductVersionModel;
    }

    @Transactional
    public void softDeleteProduct(Integer id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public void deleteVersion(Integer id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be deleted");
        }
        int deleted = productVersionRepository.deleteByProductIdAndVersionNo(id, versionNo);
        if (deleted == 0) {
            throw new IllegalArgumentException("Version not found");
        }
        product.setDevVersionNo(null);
        Integer pv = product.getProdVersionNo();
        product.setProdVersionNo(pv == null ? null : Math.max(0, pv));
        productRepository.save(product);
    }
    // get product by id
    public Product getProduct(Integer id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
    //get product by code and isDeletedFalse
    public Product getProductByCode(String code) {
        return productRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public String getJsonExampleQuote(Integer id, Integer versionNo) {
        ProductVersionModel productVersionModel = getVersion(id, versionNo);
        LobModel lob = lobService.getByCode(productVersionModel.getLob());

        List<String> jsonPaths = new ArrayList<>();
        Map<String, String> jsonValues = new HashMap<>();

        jsonPaths.add("product.code");
        jsonValues.put("product.code", productVersionModel.getCode());

        jsonPaths.add("issueDate");
        jsonValues.put("issueDate", OffsetDateTime.now().toString());

        if ( productVersionModel.getWaitingPeriod().getValidatorType().equals("LIST") ) {
            String value = productVersionModel.getWaitingPeriod().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("waitingPeriod");
            jsonValues.put("waitingPeriod", value);
        } else {
            jsonPaths.add("startDate");
            jsonValues.put("startDate", OffsetDateTime.now().toString());
        }

        if ( productVersionModel.getPolicyTerm().getValidatorType().equals("LIST") ) {
            String value = productVersionModel.getPolicyTerm().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("policyTerm");
            jsonValues.put("policyTerm", value);
        } else {
            jsonPaths.add("endDate");
            jsonValues.put("endDate", OffsetDateTime.now().plusYears(1).toString());
        }

        Set<String> validatorKeys = new HashSet<>();

        productVersionModel.getQuoteValidator().forEach(validator -> {
            validatorKeys.add(validator.getKeyLeft());
            validatorKeys.add(validator.getKeyRight());
        });

        // for each validatorKeys get path by key from lob.mpVars
        lob.getMpVars().forEach(mpVar -> {
            if (validatorKeys.contains(mpVar.getVarCode())) {
                jsonPaths.add(mpVar.getVarPath());
            }
        });

        try {
        String ret = JsonExampleBuilder.buildJsonExampleProduct(jsonPaths, jsonValues);
        //List<String> jsonPaths = lob.getMpVars().stream().map(LobVar::getVarPath).collect(Collectors.toList());
        return ret;
        } catch (Exception e) {
            return "{}";
        }
        //List<String> jsonPaths
        //JsonExampleBuilder.buildJsonQuote(lob.getMpVars().stream().map(LobVar::getVarPath).collect(Collectors.toList()));
    }

    public String getJsonExampleSave(Integer id, Integer versionNo) {
        ProductVersionModel productVersionModel = getVersion(id, versionNo);
        LobModel lob = lobService.getByCode(productVersionModel.getLob());

        List<String> jsonPaths = new ArrayList<>();
        Map<String, String> jsonValues = new HashMap<>();

        jsonPaths.add("product.code");
        jsonValues.put("product.code", productVersionModel.getCode());

        jsonPaths.add("issueDate");
        jsonValues.put("issueDate", OffsetDateTime.now().toString());

        if ( productVersionModel.getWaitingPeriod().getValidatorType().equals("LIST") ) {
            String value = productVersionModel.getWaitingPeriod().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("waitingPeriod");
            jsonValues.put("waitingPeriod", value);
        } else {
            jsonPaths.add("startDate");
            jsonValues.put("startDate", OffsetDateTime.now().toString());
        }

        if ( productVersionModel.getPolicyTerm().getValidatorType().equals("LIST") ) {
            String value = productVersionModel.getPolicyTerm().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("policyTerm");
            jsonValues.put("policyTerm", value);
        } else {
            jsonPaths.add("endDate");
            jsonValues.put("endDate", OffsetDateTime.now().plusYears(1).toString());
        }

        jsonPaths.add("insuredObject.packageCode");
        jsonValues.put("insuredObject.packageCode", "0");

        Set<String> validatorKeys = new HashSet<>();

        productVersionModel.getSaveValidator().forEach(validator -> {
            validatorKeys.add(validator.getKeyLeft());
            validatorKeys.add(validator.getKeyRight());
        });

        // for each validatorKeys get path by key from lob.mpVars
        lob.getMpVars().forEach(mpVar -> {
            if (validatorKeys.contains(mpVar.getVarCode())) {
                jsonPaths.add(mpVar.getVarPath());
            }
        });

        try {
        String ret = JsonExampleBuilder.buildJsonExampleProduct(jsonPaths, jsonValues);
        //List<String> jsonPaths = lob.getMpVars().stream().map(LobVar::getVarPath).collect(Collectors.toList());
        return ret;
        } catch (Exception e) {
            return "{}";
        }
    }
    
}


