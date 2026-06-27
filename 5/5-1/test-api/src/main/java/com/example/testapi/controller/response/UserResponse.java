package com.example.testapi.controller.response;
import com.example.testapi.domain.UserEntity;

import java.time.ZonedDateTime;

public record UserResponse(
        Long id,
        String loginId,
        Long userLevel,
        ZonedDateTime createdAt
) {
    public static UserResponse from(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getLoginId(),
                entity.getUserLevelId(),
                entity.getCreatedAt()
        );
    }
}
