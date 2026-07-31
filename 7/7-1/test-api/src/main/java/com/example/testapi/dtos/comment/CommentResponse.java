package com.example.testapi.dtos.comment;
import com.example.testapi.domain.CommentEntity;

import java.time.ZonedDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long userId,
        String nickname,
        Boolean isEdited,
        ZonedDateTime createdAt
) {
    public static CommentResponse from(CommentEntity entity) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                entity.getUser().getId(),
                entity.getUser().getNickname(),
                entity.getIsEdited(),
                entity.getCreatedAt()
        );
    }
}
