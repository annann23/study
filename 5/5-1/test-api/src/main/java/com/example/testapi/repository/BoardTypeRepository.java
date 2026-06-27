package com.example.testapi.repository;

import com.example.testapi.domain.BoardTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTypeRepository extends JpaRepository<BoardTypeEntity, Long> {
    boolean existsByName(String name);
}
