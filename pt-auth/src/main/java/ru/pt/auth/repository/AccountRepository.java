package ru.pt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pt.auth.entity.AccountEntity;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Find account by ID with eager loading of children
     */
    @Query("SELECT a FROM AccountEntity a LEFT JOIN FETCH a.children WHERE a.id = :id")
    Optional<AccountEntity> findByIdWithChildren(Long id);

    /**
     * Find account by ID with eager loading of parent
     */
    @Query("SELECT a FROM AccountEntity a LEFT JOIN FETCH a.parent WHERE a.id = :id")
    Optional<AccountEntity> findByIdWithParent(Long id);

    /**
     * Find all root accounts (accounts without parent)
     */
    @Query("SELECT a FROM AccountEntity a WHERE a.parent.id IS NULL ORDER BY a.name")
    List<AccountEntity> findRootAccounts();

    /**
     * Find all child accounts of a specific parent
     */
    @Query("SELECT a FROM AccountEntity a WHERE a.parent.id = :parentId ORDER BY a.name")
    List<AccountEntity> findByParentId(Long parentId);

    /**
     * Find accounts by node type
     */
    @Query("SELECT a FROM AccountEntity a WHERE a.nodeType = :nodeType ORDER BY a.name")
    List<AccountEntity> findByNodeType(String nodeType);

    /**
     * Find accounts by name containing the given string (case insensitive)
     */
    @Query("SELECT a FROM AccountEntity a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY a.name")
    List<AccountEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Get next account ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextAccountId();
}
