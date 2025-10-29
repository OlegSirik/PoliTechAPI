package ru.pt.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ru.pt.domain.lob.Lob;
import ru.pt.domain.lob.LobModel;
import ru.pt.exception.BadRequestException;
import ru.pt.exception.NotFoundException;
import ru.pt.repository.LobRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LobService {

    private final LobRepository lobRepository;
    //private final DataSource dataSource;

    public LobService(LobRepository lobRepository) {
        this.lobRepository = lobRepository;
        
    }

    public List<Object[]> listActiveSummaries() {
        return lobRepository.listActiveSummaries();
    }

    public LobModel getByCode(String code) {
        Lob lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElse(null);
        if (lob == null) {
            return null;
        }
        return lob.getLob();
    }

    // get by id
    public LobModel getById(Integer id) {
        Lob lob = lobRepository.findByIdAndIsDeletedFalse(id).orElse(null);
        if (lob == null) {
            return null;
        }
        return lob.getLob();
    }

    @Transactional
    public LobModel create(LobModel payload) {
        
        String mpCode = payload.getMpCode();
        if ( getByCode(mpCode) != null) {
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

        Lob lob = new Lob();
        lob.setId(nextId);
        lob.setCode(payload.getMpCode());
        lob.setName(payload.getMpName());
        lob.setLob(payload);
        
        try {
            return lobRepository.save(lob).getLob();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("LOB with code already exists: ");
        }
    }

    @Transactional
    public boolean softDeleteByCode(String code) {
        Lob lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElseThrow(() -> new NotFoundException("Lob not found"));
        lob.setDeleted(true); 
        lobRepository.save(lob);
        return true;
    }

    @Transactional
    public boolean softDeleteById(Integer id) {
        Lob lob = lobRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Lob not found"));
        lob.setDeleted(true); 
        lobRepository.save(lob);
        return true;
    }
    // create method update by id. get lob from repository by id. if lob is not found, throw not found exception.
    // check that code is not changed. if changed, throw bad request exception.
    // update lob with payload. return updated lob.
    @Transactional
    public LobModel updateByCode(String code, LobModel payload) {
        Lob lob = lobRepository.findByCodeAndIsDeletedFalse(code).orElseThrow(() -> new NotFoundException("Lob not found"));
        if (!lob.getCode().equals(payload.getMpCode())) {
            throw new BadRequestException("Code cannot be changed");
        }
        lob.setLob(payload);
        lob.setName(payload.getMpName());
        return lobRepository.save(lob).getLob();
    }
}


