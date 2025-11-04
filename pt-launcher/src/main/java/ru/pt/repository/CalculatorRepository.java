package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.CalculatorEntity;

import java.util.Optional;

public interface CalculatorRepository extends JpaRepository<CalculatorEntity, Integer> {

    @Query("select c from CalculatorEntity c where c.productId = :productId and c.versionNo = :versionNo and c.packageNo = :packageNo")
    Optional<CalculatorEntity> findByKeys(@Param("productId") Integer productId,
                                          @Param("versionNo") Integer versionNo,
                                          @Param("packageNo") Integer packageNo);

    @Query("select nextval('pt_seq')")
    Integer nextCalculatorId();
}


