package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.CoefficientData;

import java.util.List;

public interface CoefficientDataRepository extends JpaRepository<CoefficientData, Integer> {

    @Query("select c from CoefficientData c where c.calculatorId = :calculatorId and c.coefficientCode = :code order by c.col1, c.col2, c.col3, c.col4, c.col5")
    List<CoefficientData> findAllByCalcAndCode(@Param("calculatorId") Integer calculatorId,
                                               @Param("code") String code);

    @Modifying
    @Query("delete from CoefficientData c where c.calculatorId = :calculatorId and c.coefficientCode = :code")
    int deleteAllByCalcAndCode(@Param("calculatorId") Integer calculatorId,
                               @Param("code") String code);
}


