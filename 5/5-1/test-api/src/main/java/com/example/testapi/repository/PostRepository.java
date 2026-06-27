package com.example.testapi.repository;

import com.example.testapi.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByBoardIdAndDeletedAtIsNull(Long boardId);
    List<PostEntity> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
