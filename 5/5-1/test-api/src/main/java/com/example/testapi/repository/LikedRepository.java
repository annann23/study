package com.example.testapi.repository;

import com.example.testapi.domain.LikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedRepository extends JpaRepository<LikedEntity, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    List<LikedEntity> findAllByPostId(Long postId);
    List<LikedEntity> findAllByUserId(Long userId);
}
