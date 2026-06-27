package com.example.testapi.repository;

import com.example.testapi.domain.UserLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLevelRepository extends JpaRepository<UserLevelEntity, Long> {
    boolean existsByName(String name);
}
