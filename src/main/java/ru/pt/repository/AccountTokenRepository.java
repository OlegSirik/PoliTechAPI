package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.account.AccountToken;

import java.util.List;
import java.util.Optional;

public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {

    /**
     * Find account token by unique combination of token and client
     */
    Optional<AccountToken> findByTokenAndClient(String token, String client);

    /**
     * Find all account tokens for a specific account
     */
    @Query("SELECT at FROM AccountToken at WHERE at.accountId = :accountId ORDER BY at.createdAt DESC")
    List<AccountToken> findByAccountId(@Param("accountId") Long accountId);

    /**
     * Find account tokens by token
     */
    @Query("SELECT at FROM AccountToken at WHERE at.token = :token ORDER BY at.createdAt DESC")
    List<AccountToken> findByToken(@Param("token") String token);

    /**
     * Find account tokens by client
     */
    @Query("SELECT at FROM AccountToken at WHERE at.client = :client ORDER BY at.createdAt DESC")
    List<AccountToken> findByClient(@Param("client") String client);

    /**
     * Find account token by account ID and token
     */
    @Query("SELECT at FROM AccountToken at WHERE at.accountId = :accountId AND at.token = :token")
    Optional<AccountToken> findByAccountIdAndToken(@Param("accountId") Long accountId, @Param("token") String token);

    /**
     * Find account tokens by account ID and client
     */
    @Query("SELECT at FROM AccountToken at WHERE at.accountId = :accountId AND at.client = :client ORDER BY at.createdAt DESC")
    List<AccountToken> findByAccountIdAndClient(@Param("accountId") Long accountId, @Param("client") String client);

    /**
     * Find the most recent token for an account and client
     */
    @Query("SELECT at FROM AccountToken at WHERE at.accountId = :accountId AND at.client = :client ORDER BY at.createdAt DESC LIMIT 1")
    Optional<AccountToken> findMostRecentByAccountIdAndClient(@Param("accountId") Long accountId, @Param("client") String client);

    /**
     * Check if account token exists by unique combination
     */
    boolean existsByTokenAndClient(String token, String client);

    /**
     * Check if token exists for any account
     */
    boolean existsByToken(String token);

    /**
     * Check if client exists for any account token
     */
    boolean existsByClient(String client);

    /**
     * Delete all tokens for a specific account
     */
    void deleteByAccountId(Long accountId);

    /**
     * Delete all tokens for a specific account and client
     */
    void deleteByAccountIdAndClient(Long accountId, String client);

    /**
     * Get next account token ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextAccountTokenId();
}
