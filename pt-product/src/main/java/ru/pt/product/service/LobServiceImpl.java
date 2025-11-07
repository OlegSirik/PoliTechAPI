package ru.pt.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Lob;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.exception.NotFoundException;
import ru.pt.api.dto.product.LobModel;
import ru.pt.api.service.product.LobService;
import ru.pt.product.entity.LobEntity;
import ru.pt.product.repository.LobRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LobServiceImpl implements LobService {

    private final LobRepository lobRepository;
    private final ObjectMapper objectMapper;
    //private final DataSource dataSource;

    public LobServiceImpl(LobRepository lobRepository, ObjectMapper objectMapper) {
        this.lobRepository = lobRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Object[]> listActiveSummaries() {
        return lobRepository.listActiveSummaries();
    }

    @Override
    public LobModel getByCode(String code) {
        LobEntity lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElse(null);
        if (lob == null) {
            return null;
        }
        try {
            return objectMapper.readValue(lob.getLob(), LobModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // get by id
    @Override
    public LobModel getById(Integer id) {
        LobEntity lob = lobRepository.findByIdAndIsDeletedFalse(id).orElse(null);
        if (lob == null) {
            return null;
        }
        try {
            return objectMapper.readValue(lob.getLob(), LobModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public LobModel create(LobModel payload) {

        String mpCode = payload.getMpCode();
        if (getByCode(mpCode) != null) {
            // проверка на дуюль кода
            throw new BadRequestException("Продукт с кодом " + mpCode + " уже существует");
        }

        String mpName = payload.getMpName();
        if (mpCode == null || mpCode.trim().isEmpty()) {
            throw new BadRequestException("mpCode must not be empty");
        }
        if (mpName == null || mpName.trim().isEmpty()) {
            throw new BadRequestException("mpName must not be empty");
        }

        if (payload.getMpVars() != null) {
            Set<String> varCodes = new HashSet<>();
            for (var var : payload.getMpVars()) {
                if (!varCodes.add(var.getVarCode())) {
                    throw new BadRequestException("Duplicate varCode found: " + var.getVarCode());
                }
            }
        }

        if (payload.getMpCovers() != null) {
            Set<String> coverCodes = new HashSet<>();
            for (var cover : payload.getMpCovers()) {
                if (!coverCodes.add(cover.getCoverCode())) {
                    throw new BadRequestException("Duplicate coverCode found: " + cover.getCoverCode());
                }
            }
        }

        long nextId = lobRepository.nextLobId();
        payload.setId(nextId);

        LobEntity lob = new LobEntity();
        lob.setId(nextId);
        lob.setCode(payload.getMpCode());
        lob.setName(payload.getMpName());
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        lob.setLob(payloadJson);

        try {
            return objectMapper.readValue(lobRepository.save(lob).getLob(), LobModel.class);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("LOB with code already exists: ");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public boolean softDeleteByCode(String code) {
        LobEntity lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElseThrow(() -> new NotFoundException("Lob not found"));
        lob.setDeleted(true);
        lobRepository.save(lob);
        return true;
    }

    @Transactional
    @Override
    public boolean softDeleteById(Integer id) {
        LobEntity lob = lobRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Lob not found"));
        lob.setDeleted(true);
        lobRepository.save(lob);
        return true;
    }

    // create method update by id. get lob from repository by id. if lob is not found, throw not found exception.
    // check that code is not changed. if changed, throw bad request exception.
    // update lob with payload. return updated lob.
    @Transactional
    @Override
    public LobModel updateByCode(String code, LobModel payload) {
        LobEntity lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElseThrow(() -> new NotFoundException("Lob not found"));
        if (!lob.getCode().equals(payload.getMpCode())) {
            throw new BadRequestException("Code cannot be changed");
        }

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        lob.setLob(payloadJson);
        lob.setName(payload.getMpName());
        try {
            return objectMapper.readValue(lobRepository.save(lob).getLob(), LobModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}