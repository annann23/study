package com.example.testapi.repository;
import com.example.testapi.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByUserIdAndDeletedAtIsNull(Long userId);
    List<CommentEntity> findAllByPostIdAndDeletedAtIsNull(Long postId);
}