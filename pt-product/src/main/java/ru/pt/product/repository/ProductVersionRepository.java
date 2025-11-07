package ru.pt.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.product.entity.ProductVersionEntity;

import java.util.Optional;

public interface ProductVersionRepository extends JpaRepository<ProductVersionEntity, Integer> {

    @Query("select pv from ProductVersionEntity pv where pv.productId = :productId and pv.versionNo = :versionNo")
    Optional<ProductVersionEntity> findByProductIdAndVersionNo(@Param("productId") Integer productId,
                                                               @Param("versionNo") Integer versionNo);

    @Modifying
    @Query("delete from ProductVersionEntity pv where pv.productId = :productId and pv.versionNo = :versionNo")
    int deleteByProductIdAndVersionNo(@Param("productId") Integer productId,
                                      @Param("versionNo") Integer versionNo);
}