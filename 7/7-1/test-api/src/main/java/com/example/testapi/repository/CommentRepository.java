package com.example.testapi.repository;
import com.example.testapi.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.user.id = :userId AND c.deletedAt IS NULL")
    List<CommentEntity> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId);

    @Query("SELECT c FROM CommentEntity c WHERE c.post.id = :postId AND c.deletedAt IS NULL")
    List<CommentEntity> findAllByPostIdAndDeletedAtIsNull(@Param("postId") Long postId);
}