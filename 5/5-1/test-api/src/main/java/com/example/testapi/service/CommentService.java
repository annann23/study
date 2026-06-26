package com.example.testapi.service;

import com.example.testapi.domain.CommentEntity;
import com.example.testapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void save(String content, String nickname) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 비어있을 수 없습니다.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("nickname은 비어있을 수 없습니다.");
        }
        commentRepository.save(new CommentEntity(content, nickname));
    }

    public List<CommentEntity> findAll() {
        return commentRepository.findAll();
    }
}

