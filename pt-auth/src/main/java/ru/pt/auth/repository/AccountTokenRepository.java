package ru.pt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.auth.entity.AccountTokenEntity;

public interface AccountTokenRepository extends JpaRepository<AccountTokenEntity, Long> {

}
