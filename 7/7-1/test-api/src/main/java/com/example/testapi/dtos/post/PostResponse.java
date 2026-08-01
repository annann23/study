package com.example.testapi.dtos.post;

import com.example.testapi.domain.PostEntity;

import java.time.ZonedDateTime;

public record PostResponse(
        Long id,
        Long boardId,
        Long userId,
        String nickName,
        String userLevel,
        String title,
        String content,
        ZonedDateTime createdAt
) {
    public static PostResponse from(PostEntity entity) {
        return new PostResponse(
                entity.getId(),
                entity.getBoardId(),
                entity.getUserId(),
                entity.getUser().getNickname(),
                entity.getUser().getUserLevel().getName(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
    public static PostResponse preview(PostEntity entity) {
        return new PostResponse(
                entity.getId(),
                entity.getBoardId(),
                entity.getUserId(),
                entity.getUser().getNickname(),
                entity.getUser().getUserLevel().getName(),
                entity.getTitle(),
                null,
                entity.getCreatedAt()
        );
    }
}
