package ru.pt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.auth.entity.LoginEntity;

public interface LoginRepository extends JpaRepository<LoginEntity, Long> {

}
