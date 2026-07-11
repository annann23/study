package com.example.testapi.dtos.response;
import com.example.testapi.domain.UserLevelEntity;

public record UserLevelResponse(
        Long id,
        String name
) {
    public static UserLevelResponse from(UserLevelEntity entity) {
        return new UserLevelResponse(
                entity.getId(),
                entity.getName()
        );
    }
}
