package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.account.AccountLogin;

import java.util.List;
import java.util.Optional;

public interface AccountLoginRepository extends JpaRepository<AccountLogin, Long> {

    /**
     * Find account login by unique combination of login, client, and nr
     */
    Optional<AccountLogin> findByLoginAndClientAndNr(String login, String client, Long nr);

    /**
     * Find all account logins for a specific account
     */
    @Query("SELECT al FROM AccountLogin al WHERE al.accountId = :accountId ORDER BY al.login, al.client")
    List<AccountLogin> findByAccountId(@Param("accountId") Long accountId);

    /**
     * Find account logins by login name
     */
    @Query("SELECT al FROM AccountLogin al WHERE al.login = :login ORDER BY al.client, al.nr")
    List<AccountLogin> findByLogin(@Param("login") String login);

    /**
     * Find account logins by client
     */
    @Query("SELECT al FROM AccountLogin al WHERE al.client = :client ORDER BY al.login, al.nr")
    List<AccountLogin> findByClient(@Param("client") String client);

    /**
     * Find account login by account ID and login name
     */
    @Query("SELECT al FROM AccountLogin al WHERE al.accountId = :accountId AND al.login = :login")
    Optional<AccountLogin> findByAccountIdAndLogin(@Param("accountId") Long accountId, @Param("login") String login);

    /**
     * Find account logins by account ID and client
     */
    @Query("SELECT al FROM AccountLogin al WHERE al.accountId = :accountId AND al.client = :client ORDER BY al.login, al.nr")
    List<AccountLogin> findByAccountIdAndClient(@Param("accountId") Long accountId, @Param("client") String client);

    @Query("SELECT al FROM AccountLogin al WHERE al.client = :client AND al.login = :login ORDER BY al.nr")
    List<AccountLogin> findByClientAndLogin(@Param("client") String client, @Param("login") String login);
    /**
     * Check if account login exists by unique combination
     */
    boolean existsByLoginAndClientAndNr(String login, String client, Long nr);

    /**
     * Check if login exists for any account
     */
    boolean existsByLogin(String login);

    /**
     * Check if client exists for any account login
     */
    boolean existsByClient(String client);

    /**
     * Get next account login ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextAccountLoginId();
}
