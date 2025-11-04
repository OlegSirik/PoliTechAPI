package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.ProductVersion;

import java.util.Optional;

public interface ProductVersionRepository extends JpaRepository<ProductVersion, Integer> {

    @Query("select pv from ProductVersion pv where pv.productId = :productId and pv.versionNo = :versionNo")
    Optional<ProductVersion> findByProductIdAndVersionNo(@Param("productId") Integer productId,
                                                         @Param("versionNo") Integer versionNo);

    @Modifying
    @Query("delete from ProductVersion pv where pv.productId = :productId and pv.versionNo = :versionNo")
    int deleteByProductIdAndVersionNo(@Param("productId") Integer productId,
                                      @Param("versionNo") Integer versionNo);
}


