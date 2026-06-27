package com.example.testapi.repository;

import com.example.testapi.domain.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    boolean existsByName(String name);
    List<BoardEntity> findAllByBoardTypeId(Long boardTypeId);
}
