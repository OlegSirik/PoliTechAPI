package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.account.Login;

import java.util.List;
import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    /**
     * Find login by login name
     */
    Optional<Login> findByLogin(String login);

    /**
     * Find login by email
     */
    Optional<Login> findByEmail(String email);

    /**
     * Find login by phone
     */
    Optional<Login> findByPhone(String phone);

    /**
     * Find logins by first name containing the given string (case insensitive)
     */
    @Query("SELECT l FROM Login l WHERE LOWER(l.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) ORDER BY l.firstName, l.lastName")
    List<Login> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);

    /**
     * Find logins by last name containing the given string (case insensitive)
     */
    @Query("SELECT l FROM Login l WHERE LOWER(l.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) ORDER BY l.lastName, l.firstName")
    List<Login> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    /**
     * Find logins by full name containing the given string (case insensitive)
     */
    @Query("SELECT l FROM Login l WHERE LOWER(CONCAT(l.firstName, ' ', l.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%')) ORDER BY l.firstName, l.lastName")
    List<Login> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName);

    /**
     * Check if login exists by login name
     */
    boolean existsByLogin(String login);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Get next login ID from sequence
     */
    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextLoginId();
}
