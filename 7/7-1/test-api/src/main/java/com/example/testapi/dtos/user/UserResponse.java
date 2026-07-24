package com.example.testapi.dtos.response;
import com.example.testapi.domain.UserEntity;

import java.time.ZonedDateTime;

public record UserResponse(
        Long id,
        String loginId,
        String nickname,
        Long userLevel,
        ZonedDateTime createdAt
) {
    public static UserResponse from(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getLoginId(),
                entity.getNickname(),
                entity.getUserLevelId(),
                entity.getCreatedAt()
        );
    }
}
