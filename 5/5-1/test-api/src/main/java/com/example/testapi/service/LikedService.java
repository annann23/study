package com.example.testapi.service;

import com.example.testapi.domain.LikedEntity;
import com.example.testapi.domain.PostEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.repository.LikedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikedService {

    private final LikedRepository likedRepository;
    private final UserService userService;
    private final PostService postService;

    public LikedService(LikedRepository likedRepository, UserService userService, PostService postService) {
        this.likedRepository = likedRepository;
        this.userService = userService;
        this.postService = postService;
    }

    @Transactional
    public boolean toggleLike(Long userId, Long postId) {
        if (likedRepository.existsByUserIdAndPostId(userId, postId)) {
            likedRepository.deleteByUserIdAndPostId(userId, postId);
            return false;
        }
        UserEntity user = userService.findById(userId);
        PostEntity post = postService.findById(postId);
        likedRepository.save(new LikedEntity(user, post));
        return true;
    }

    public List<LikedEntity> findAllByPost(Long postId) {
        postService.findById(postId);
        return likedRepository.findAllByPostId(postId);
    }

    public List<LikedEntity> findAllByUser(Long userId) {
        userService.findById(userId);
        return likedRepository.findAllByUserId(userId);
    }
}
