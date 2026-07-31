package com.example.testapi.dtos.role;

import java.util.Set;

public record RoleSaveRequest(String name, Set<Long> permissionIds) {}