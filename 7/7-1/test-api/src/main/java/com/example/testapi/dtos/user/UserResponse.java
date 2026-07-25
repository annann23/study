package com.example.testapi.dtos.response;
import com.example.testapi.domain.PermissionEntity;
import com.example.testapi.domain.RoleEntity;
import com.example.testapi.domain.UserEntity;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String loginId,
        String nickname,
        Long userLevel,
        Set<String> roles,
        Set<String> permissions,
        ZonedDateTime createdAt
) {
    public static UserResponse from(UserEntity entity) {
        Set<String> roles = entity.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = entity.getRoles().stream()
                .map(RoleEntity::getPermissions)
                .flatMap(Set::stream)
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                entity.getId(),
                entity.getLoginId(),
                entity.getNickname(),
                entity.getUserLevelId(),
                roles,
                permissions,
                entity.getCreatedAt()
        );
    }
}
