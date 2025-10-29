package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.account.ProductRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRoleRepository extends JpaRepository<ProductRole, Long> {

    /**
     * Find product role by account ID, role product ID, and role account ID
     */
    Optional<ProductRole> findByAccountIdAndRoleProductIdAndRoleAccountId(
            Long accountId, Long roleProductId, Long roleAccountId);

    /**
     * Find all product roles for a specific account
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.accountId = :accountId ORDER BY pr.roleProductId, pr.roleAccountId")
    List<ProductRole> findByAccountId(@Param("accountId") Long accountId);

    /**
     * Find all product roles for a specific role product
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.roleProductId = :roleProductId ORDER BY pr.accountId")
    List<ProductRole> findByRoleProductId(@Param("roleProductId") Long roleProductId);

    /**
     * Find all product roles for a specific role account
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.roleAccountId = :roleAccountId ORDER BY pr.accountId, pr.roleProductId")
    List<ProductRole> findByRoleAccountId(@Param("roleAccountId") Long roleAccountId);

    /**
     * Find product roles with specific permission (e.g., can read)
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.accountId = :accountId AND pr.canRead = true ORDER BY pr.roleProductId")
    List<ProductRole> findByAccountIdAndCanReadTrue(@Param("accountId") Long accountId);

    /**
     * Find product roles with quote permission
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.accountId = :accountId AND pr.canQuote = true ORDER BY pr.roleProductId")
    List<ProductRole> findByAccountIdAndCanQuoteTrue(@Param("accountId") Long accountId);

    /**
     * Find product roles with policy permission
     */
    @Query("SELECT pr FROM ProductRole pr WHERE pr.accountId = :accountId AND pr.canPolicy = true ORDER BY pr.roleProductId")
    List<ProductRole> findByAccountIdAndCanPolicyTrue(@Param("accountId") Long accountId);

    /**
     * Check if account has any permissions for a specific product
     */
    @Query("SELECT COUNT(pr) > 0 FROM ProductRole pr WHERE pr.accountId = :accountId AND pr.roleProductId = :roleProductId")
    boolean existsByAccountIdAndRoleProductId(@Param("accountId") Long accountId, @Param("roleProductId") Long roleProductId);

    /**
     * Get next product role ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextProductRoleId();

    @Query(
        value = "WITH RECURSIVE category_levels AS ( " +
                "SELECT id, parent_id, 0 as lvl FROM acc_accounts WHERE id = :accountId " +
                "UNION ALL " +
                "SELECT c.id, c.parent_id, cl.lvl + 1 FROM acc_accounts c JOIN category_levels cl ON cl.parent_id = c.id " +
                ") " +
                "SELECT rl.* , 'PROD' roleProductCode FROM category_levels cl " +
                "JOIN acc_product_roles rl ON cl.id = rl.role_account_id AND rl.account_id = :accountId " +
                "ORDER BY rl.role_product_id, lvl ASC",
        nativeQuery = true
    )
    List<Map<String, Object>> findAllProductRolesByAccountId(@Param("accountId") Long accountId);
}
