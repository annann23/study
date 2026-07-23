package com.example.testapi.dtos.response;

import com.example.testapi.domain.PostEntity;

import java.time.ZonedDateTime;

public record PostResponse(
        Long id,
        Long boardId,
        Long userId,
        String title,
        String content,
        ZonedDateTime createdAt
) {
    public static PostResponse from(PostEntity entity) {
        return new PostResponse(
                entity.getId(),
                entity.getBoardId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
