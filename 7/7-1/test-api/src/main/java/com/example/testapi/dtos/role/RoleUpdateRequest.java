package com.example.testapi.dtos.role;

import java.util.Set;

public record RoleUpdateRequest(Long id, String name, Set<Long> permissionIds) {}