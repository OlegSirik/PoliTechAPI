package ru.pt.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.numbers.NumberGeneratorDescription;
import ru.pt.api.dto.product.*;
import ru.pt.api.service.numbers.NumberGeneratorService;
import ru.pt.api.service.product.LobService;
import ru.pt.api.service.product.ProductService;
import ru.pt.product.entity.ProductEntity;
import ru.pt.product.entity.ProductVersionEntity;
import ru.pt.product.repository.ProductRepository;
import ru.pt.product.repository.ProductVersionRepository;
import ru.pt.product.utils.JsonExampleBuilder;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final LobService lobService;
    private final ObjectMapper objectMapper;
    private final ProductVersionRepository productVersionRepository;
    private final NumberGeneratorService numberGeneratorService;

    public ProductServiceImpl(ProductRepository productRepository, LobService lobService, ObjectMapper objectMapper, ProductVersionRepository productVersionRepository, NumberGeneratorService numberGeneratorService) {
        this.productRepository = productRepository;
        this.lobService = lobService;
        this.objectMapper = objectMapper;
        this.productVersionRepository = productVersionRepository;
        this.numberGeneratorService = numberGeneratorService;
    }

    @Override
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

    @Override
    @Transactional
    public ProductVersionModel create(ProductVersionModel productVersionModel) {

        if (StringUtils.isBlank(productVersionModel.getLob())) {
            throw new BadRequestException("lob must not be empty");
        }

        if (StringUtils.isBlank(productVersionModel.getCode())) {
            throw new BadRequestException("code must not be empty");
        }
        if (StringUtils.isBlank(productVersionModel.getName())) {
            throw new BadRequestException("name must not be empty");
        }

        Integer id = productRepository.getNextProductId();

        ProductEntity product = new ProductEntity();
        product.setId(id);
        if (productVersionModel.getVersionStatus().equals("PROD")) {
            product.setProdVersionNo(productVersionModel.getVersionNo());
        } else {
            product.setDevVersionNo(productVersionModel.getVersionNo());
        }
        product.setDeleted(false);
        product.setLob(productVersionModel.getLob());
        product.setCode(productVersionModel.getCode());
        product.setName(productVersionModel.getName());
        productRepository.save(product);

        productVersionModel.setId(id);

        if (productVersionModel.getQuoteValidator() == null) {
            productVersionModel.setQuoteValidator(new ArrayList<>());
        }
        if (productVersionModel.getSaveValidator() == null) {
            productVersionModel.setSaveValidator(new ArrayList<>());
        }
        if (productVersionModel.getPackages() == null) {
            productVersionModel.setPackages(new ArrayList<>());
        }

        if (productVersionModel.getPackages().isEmpty()) {
            PvPackage pvPackage = new PvPackage();
            pvPackage.setCode(0);
            pvPackage.setName("0");
            productVersionModel.getPackages().add(pvPackage);
            pvPackage.setCovers(new ArrayList<>());
        }

        ProductVersionEntity pv = new ProductVersionEntity();
        pv.setProductId(id);
        pv.setVersionNo(productVersionModel.getVersionNo());


        String productJson;
        try {
            productJson = objectMapper.writeValueAsString(productVersionModel);
        } catch (JsonProcessingException e) {
            // TODO exception handling
            throw new RuntimeException(e);
        }
        pv.setProduct(productJson);

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

    @Override
    public ProductVersionModel getVersion(Integer id, Integer versionNo) {
        try {
            ProductVersionEntity pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                    .orElse(null);

            return objectMapper.readValue(pv.getProduct(), ProductVersionModel.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Version not found");
        }
    }

    @Override
    public ProductVersionModel createVersionFrom(Integer id, Integer versionNo) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() != null) {
            throw new IllegalArgumentException("only one version can be in dev status");
        }
        int newVersion = product.getProdVersionNo() == null ? 1 : product.getProdVersionNo() + 1;

        String productVersionJson = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Base version not found"))
                .getProduct();

        ProductVersionModel productVersionModel;
        try {
            productVersionModel = objectMapper.readValue(productVersionJson, ProductVersionModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        productVersionModel.setVersionNo(newVersion);
        productVersionModel.setVersionStatus("DEV");

        ProductVersionEntity pv = new ProductVersionEntity();
        pv.setProductId(id);
        pv.setVersionNo(newVersion);
        try {
            pv.setProduct(objectMapper.writeValueAsString(productVersionModel));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        productVersionRepository.save(pv);

        product.setDevVersionNo(newVersion);
        productRepository.save(product);
        return productVersionModel;
    }

    @Override
    public ProductVersionModel updateVersion(Integer id, Integer versionNo, ProductVersionModel newProductVersionModel) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be updated");
        }

        newProductVersionModel.setId(id);
        newProductVersionModel.setVersionNo(versionNo);
        newProductVersionModel.setVersionStatus("DEV");

        ProductVersionEntity pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));

        String newProductVersionJson;
        try {
            newProductVersionJson = objectMapper.writeValueAsString(newProductVersionModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        pv.setProduct(newProductVersionJson);
        productVersionRepository.save(pv);

        if (newProductVersionModel.getNumberGeneratorDescription() != null) {
            var description = createNumberGeneratorDescription(newProductVersionModel);
            numberGeneratorService.create(description);

        }

        return newProductVersionModel;
    }

    @Override
    public void softDeleteProduct(Integer id) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public void deleteVersion(Integer id, Integer versionNo) {
        ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
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

    @Override
    public String getJsonExampleQuote(Integer id, Integer versionNo) {
        ProductVersionModel productVersionModel = getVersion(id, versionNo);
        LobModel lob = lobService.getByCode(productVersionModel.getLob());

        List<String> jsonPaths = new ArrayList<>();
        Map<String, String> jsonValues = new HashMap<>();

        jsonPaths.add("product.code");
        jsonValues.put("product.code", productVersionModel.getCode());

        jsonPaths.add("issueDate");
        jsonValues.put("issueDate", OffsetDateTime.now().toString());

        if (productVersionModel.getWaitingPeriod().getValidatorType().equals("LIST")) {
            String value = productVersionModel.getWaitingPeriod().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("waitingPeriod");
            jsonValues.put("waitingPeriod", value);
        } else {
            jsonPaths.add("startDate");
            jsonValues.put("startDate", OffsetDateTime.now().toString());
        }

        if (productVersionModel.getPolicyTerm().getValidatorType().equals("LIST")) {
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
            return JsonExampleBuilder.buildJsonExampleProduct(jsonPaths, jsonValues);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    public String getJsonExampleSave(Integer id, Integer versionNo) {
        ProductVersionModel productVersionModel = getVersion(id, versionNo);
        LobModel lob = lobService.getByCode(productVersionModel.getLob());

        List<String> jsonPaths = new ArrayList<>();
        Map<String, String> jsonValues = new HashMap<>();

        jsonPaths.add("product.code");
        jsonValues.put("product.code", productVersionModel.getCode());

        jsonPaths.add("issueDate");
        jsonValues.put("issueDate", OffsetDateTime.now().toString());

        if (productVersionModel.getWaitingPeriod().getValidatorType().equals("LIST")) {
            String value = productVersionModel.getWaitingPeriod().getValidatorValue().split(",")[0].trim();
            jsonPaths.add("waitingPeriod");
            jsonValues.put("waitingPeriod", value);
        } else {
            jsonPaths.add("startDate");
            jsonValues.put("startDate", OffsetDateTime.now().toString());
        }

        if (productVersionModel.getPolicyTerm().getValidatorType().equals("LIST")) {
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
            return JsonExampleBuilder.buildJsonExampleProduct(jsonPaths, jsonValues);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    public ProductVersionModel getProduct(Integer id, boolean forDev) {
        var entity = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        var versionNo = forDev ? entity.getDevVersionNo() : entity.getProdVersionNo();
        var pv = productVersionRepository.findByProductIdAndVersionNo(entity.getId(), versionNo)
                .orElseThrow();
        try {
            return objectMapper.readValue(pv.getProduct(), ProductVersionModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    //get product by code and isDeletedFalse
    @Override
    public ProductVersionModel getProductByCode(String code, boolean forDev) {
        var entity = productRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        var versionNo = forDev ? entity.getDevVersionNo() : entity.getProdVersionNo();

        var pv = productVersionRepository.findByProductIdAndVersionNo(entity.getId(), entity.getProdVersionNo())
                .orElseThrow();
        try {
            return objectMapper.readValue(pv.getProduct(), ProductVersionModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
}
