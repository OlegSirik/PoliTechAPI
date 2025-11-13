package ru.pt.numbers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.numbers.entity.NumberGeneratorEntity;

import java.util.Optional;

public interface NumberGeneratorRepository extends JpaRepository<NumberGeneratorEntity, Integer> {

    Optional<NumberGeneratorEntity> findByProductCode(String productCode);
}