package ru.pt.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.product.entity.LobEntity;

import java.util.List;
import java.util.Optional;

public interface LobRepository extends JpaRepository<LobEntity, Integer> {

    Optional<LobEntity> findByCodeAndIsDeletedFalse(String code);

    @Query("select l from LobEntity l where l.id = :id and l.isDeleted = false")
    Optional<LobEntity> findActiveById(@Param("id") Integer id);

    @Query("select l.id as id, l.code as code, l.name as name from LobEntity l where l.isDeleted = false order by l.code")
    List<Object[]> listActiveSummaries();

    @Query("select nextval('pt_seq')")
    Integer nextLobId();

    Optional<LobEntity> findByIdAndIsDeletedFalse(Integer id);
}
