package ru.pt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.auth.entity.AccountLoginEntity;

import java.util.List;

public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Long> {

    @Query("SELECT al FROM AccountLoginEntity al WHERE al.clientEntity.name = :client AND al.loginEntity.userLogin = :login ORDER BY al.id")
    List<AccountLoginEntity> findByClientAndLogin(@Param("client") String client, @Param("login") String login);

}
