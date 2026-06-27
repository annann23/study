package com.example.testapi.controller.response;
import com.example.testapi.domain.CommentEntity;

import java.time.ZonedDateTime;

public record CommentResponse(
        Long id,
        String content,
        String userId,
        Boolean isEdited,
        ZonedDateTime createdAt
) {
    public static CommentResponse from(CommentEntity entity) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                entity.getUserId(),
                entity.getIsEdited(),
                entity.getCreatedAt()
        );
    }
}
