package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pt.domain.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by ID with eager loading of children
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.children WHERE a.id = :id")
    Optional<Account> findByIdWithChildren(Long id);

    /**
     * Find account by ID with eager loading of parent
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.parent WHERE a.id = :id")
    Optional<Account> findByIdWithParent(Long id);

    /**
     * Find all root accounts (accounts without parent)
     */
    @Query("SELECT a FROM Account a WHERE a.parentId IS NULL ORDER BY a.name")
    List<Account> findRootAccounts();

    /**
     * Find all child accounts of a specific parent
     */
    @Query("SELECT a FROM Account a WHERE a.parentId = :parentId ORDER BY a.name")
    List<Account> findByParentId(Long parentId);

    /**
     * Find accounts by node type
     */
    @Query("SELECT a FROM Account a WHERE a.nodeType = :nodeType ORDER BY a.name")
    List<Account> findByNodeType(String nodeType);

    /**
     * Find accounts by name containing the given string (case insensitive)
     */
    @Query("SELECT a FROM Account a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY a.name")
    List<Account> findByNameContainingIgnoreCase(String name);

    /**
     * Get next account ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextAccountId();
}
