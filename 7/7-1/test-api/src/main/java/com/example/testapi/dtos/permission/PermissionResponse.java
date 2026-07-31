package com.example.testapi.dtos.permission;
import com.example.testapi.domain.PermissionEntity;

public record PermissionResponse(
        Long id,
        String name
) {
    public static PermissionResponse from(PermissionEntity entity) {
        return new PermissionResponse(
                entity.getId(),
                entity.getName()
        );
    }
}
