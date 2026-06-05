package com.example.testapi.controller.response;

import com.example.testapi.domain.CommentEntity;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String nickname,
        LocalDateTime createdAt
) {
    public static CommentResponse from(CommentEntity entity) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                entity.getNickname(),
                entity.getCreatedAt()
        );
    }
}
