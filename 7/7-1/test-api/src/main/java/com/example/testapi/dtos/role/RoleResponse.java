package com.example.testapi.dtos.role;
import com.example.testapi.domain.PermissionEntity;
import com.example.testapi.domain.RoleEntity;

import java.util.Set;
import java.util.stream.Collectors;

public record RoleResponse(
        Long id,
        String name,
        Set<String> permissions
) {
    public static RoleResponse from(RoleEntity entity) {
        Set<String> permissions = entity.getPermissions().stream()
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());

        return new RoleResponse(
                entity.getId(),
                entity.getName(),
                permissions
        );
    }
}
