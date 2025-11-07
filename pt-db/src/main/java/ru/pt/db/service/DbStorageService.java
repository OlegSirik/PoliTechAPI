package ru.pt.db.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyData;
import ru.pt.api.dto.versioning.Version;
import ru.pt.api.service.db.StorageService;
import ru.pt.db.entity.PolicyEntity;
import ru.pt.db.entity.PolicyIndexEntity;
import ru.pt.db.repository.PolicyIndexRepository;
import ru.pt.db.repository.PolicyRepository;
import ru.pt.db.utils.PolicyMapper;
import ru.pt.db.utils.PolicyProjectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DbStorageService implements StorageService {

    private final PolicyRepository policyRepository;
    private final PolicyIndexRepository policyIndexRepository;
    private final PolicyProjectionService policyProjectionService;
    private final PolicyMapper policyMapper;

    public DbStorageService(PolicyRepository policyRepository, PolicyIndexRepository policyIndexRepository, PolicyProjectionService policyProjectionService, PolicyMapper policyMapper) {
        this.policyRepository = policyRepository;
        this.policyIndexRepository = policyIndexRepository;
        this.policyProjectionService = policyProjectionService;
        this.policyMapper = policyMapper;
    }

    @Transactional
    @Override
    public PolicyData save(String policy, UserData userData, Version version, UUID uuid) {
        var entity = new PolicyEntity();
        entity.setId(uuid);
        entity.setPolicy(policy);
        policyRepository.save(entity);

        var index = policyProjectionService.readPolicyIndex(uuid, version, userData, policy);
        policyIndexRepository.save(index);

        var policyData = new PolicyData();
        policyData.setPolicyIndex(policyMapper.toDto(index));
        policyData.setPolicyId(uuid);
        policyData.setPolicyStatus(index.getPolicyStatus());
        policyData.setPolicyNumber(index.getPolicyNumber());
        policyData.setPolicy(policy);
        return policyData;

    }

    @Override
    public void update(PolicyData policyData) {
        policyIndexRepository.save(policyMapper.toEntity(policyData.getPolicyIndex()));
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setPolicy(policyData.getPolicy());
        policyEntity.setId(policyData.getPolicyId());
        policyRepository.save(policyEntity);
    }

    @Override
    @Transactional
    public PolicyData update(String policy, UserData userData, Version version, String policyNumber) {
        return policyIndexRepository.findPolicyIndexEntityByPolicyNumber(policyNumber)
                .map(idx -> {
                    var uuid = idx.getPolicyId();
                    policyRepository.deleteById(uuid);
                    policyIndexRepository.deleteById(uuid);

                    return save(policy, userData, version, uuid);
                }).orElse(null);
    }

    @Override
    public PolicyData getPolicyById(UUID policyId) {
        var policy = policyIndexRepository.findById(policyId)
                .orElseThrow(() -> new IllegalStateException("No policy with id " + policyId));
        var dto = policyMapper.toDto(policy);

        var policyData = new PolicyData();
        policyData.setPolicyIndex(dto);
        policyData.setPolicyId(policy.getPolicyId());
        policyData.setPolicyStatus(policy.getPolicyStatus());
        policyData.setPolicyNumber(policy.getPolicyNumber());

        var json = policyRepository.findById(policy.getPolicyId())
                .map(PolicyEntity::getPolicy)
                .orElseThrow();

        policyData.setPolicy(json);

        return policyData;
    }

    @Override
    public PolicyData getPolicyByNumber(String policyNumber) {
        var policy = policyIndexRepository.findPolicyIndexEntityByPolicyNumber(policyNumber)
                .orElseThrow(() -> new IllegalStateException("No policy with number " + policyNumber));
        var dto = policyMapper.toDto(policy);

        var policyData = new PolicyData();
        policyData.setPolicyIndex(dto);
        policyData.setPolicyId(policy.getPolicyId());
        policyData.setPolicyStatus(policy.getPolicyStatus());
        policyData.setPolicyNumber(policy.getPolicyNumber());

        var json = policyRepository.findById(policy.getPolicyId())
                .map(PolicyEntity::getPolicy)
                .orElseThrow();

        policyData.setPolicy(json);

        return policyData;
    }

    @Override
    public List<PolicyData> getPoliciesForUser(UserData userData) {
        var policies = policyIndexRepository.findAllByClientAccountIdAndUserAccountId(userData.getClientId(), userData.getAccountId());

        var idToData = policies.stream().collect(Collectors.toMap(PolicyIndexEntity::getPolicyId, Function.identity()));

        var idToJson = policyRepository.findAllById(idToData.keySet())
                .stream()
                .collect(Collectors.toMap(PolicyEntity::getId, Function.identity()));

        var result = new ArrayList<PolicyData>(idToData.size());

        idToData.forEach((uuid, entity) -> {
            var dto = policyMapper.toDto(entity);

            var policyData = new PolicyData();
            policyData.setPolicyIndex(dto);
            policyData.setPolicyId(entity.getPolicyId());
            policyData.setPolicyStatus(entity.getPolicyStatus());
            policyData.setPolicyNumber(entity.getPolicyNumber());

            var json = idToJson.get(uuid).getPolicy();

            policyData.setPolicy(json);
            result.add(policyData);
        });

        return result;
    }
}
