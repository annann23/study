package com.example.testapi.repository;

import com.example.testapi.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("SELECT p FROM PostEntity p WHERE p.board.id = :boardId AND p.deletedAt IS NULL")
    List<PostEntity> findAllByBoardIdAndDeletedAtIsNull(@Param("boardId") Long boardId);

    @Query("SELECT p FROM PostEntity p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    List<PostEntity> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId);
}
