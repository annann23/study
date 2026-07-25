package com.example.testapi.dtos.request;

import java.util.Set;

public record RoleSaveRequest(String name, Set<Long> permissionIds) {}