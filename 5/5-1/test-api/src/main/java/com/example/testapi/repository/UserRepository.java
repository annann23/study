package com.example.testapi.repository;

import com.example.testapi.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginIdAndDeletedAtIsNull(String loginId);
    boolean existsByLoginId(String loginId);
}
