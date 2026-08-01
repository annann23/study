package com.example.testapi.config;

import com.example.testapi.domain.UserEntity;
import com.example.testapi.repository.UserRepository;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.security.OwnableResource;
import com.example.testapi.security.ResourceLookupRegistry;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UserRepository userRepository;
    private final ResourceLookupRegistry resourceLookupRegistry;

    public CustomPermissionEvaluator(UserRepository userRepository, ResourceLookupRegistry resourceLookupRegistry) {
        this.userRepository = userRepository;
        this.resourceLookupRegistry = resourceLookupRegistry;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        throw new UnsupportedOperationException("targetType 없이 도메인 객체로 권한을 확인하는 방식은 지원하지 않습니다.");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        Long userId = ((CafeAuthUser) authentication.getPrincipal()).getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        String permissionName = (String) permission;

        if (!user.hasPermission(permissionName)) {
            return false;
        }

        if (targetId == null) {
            return true;
        }

        if (permissionName.endsWith("_ANY")) {
            return true;
        }

        OwnableResource resource = resourceLookupRegistry.find(targetType, (Long) targetId);
        return resource.getOwnerId().equals(userId);
    }
}