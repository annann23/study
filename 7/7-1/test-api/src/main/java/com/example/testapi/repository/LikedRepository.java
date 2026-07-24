package com.example.testapi.repository;

import com.example.testapi.domain.LikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikedRepository extends JpaRepository<LikedEntity, Long> {
    @Query("SELECT COUNT(l) > 0 FROM LikedEntity l WHERE l.user.id = :userId AND l.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM LikedEntity l WHERE l.user.id = :userId AND l.post.id = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT l FROM LikedEntity l WHERE l.post.id = :postId")
    List<LikedEntity> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT l FROM LikedEntity l WHERE l.user.id = :userId")
    List<LikedEntity> findAllByUserId(@Param("userId") Long userId);
}
