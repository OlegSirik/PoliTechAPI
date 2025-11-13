package ru.pt.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pt.product.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByIdAndIsDeletedFalse(Integer id);

    Optional<ProductEntity> findByCodeAndIsDeletedFalse(String code);

    @Query("select p from ProductEntity p where p.isDeleted = false order by p.code")
    List<ProductEntity> listActive();

    // TODO выше точно такой же метод
    @Query("select p.id, p.lob, p.code, p.name, p.prodVersionNo, p.devVersionNo from ProductEntity p where p.isDeleted = false order by p.code")
    List<Object[]> listActiveSummaries();

    @Query(value = "SELECT nextval('pt_seq')", nativeQuery = true)
    Integer getNextProductId();

}
