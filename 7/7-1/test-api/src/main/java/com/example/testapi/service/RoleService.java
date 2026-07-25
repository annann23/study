package com.example.testapi.service;

import com.example.testapi.domain.RoleEntity;
import com.example.testapi.domain.PermissionEntity;
import com.example.testapi.repository.RoleRepository;
import com.example.testapi.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // c
    public RoleEntity create(String name, Set<Long> permissionIds) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 역할입니다.");
        }

        RoleEntity role = new RoleEntity(name);

        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다: " + permissionId));
                role.addPermission(permission);
            }
        }

        return roleRepository.save(role);
    }

    // r
    public RoleEntity findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    // u
    public RoleEntity update(Long id, String newName, Set<Long> permissionIds) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }

        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));

        if (!role.getName().equals(newName) && roleRepository.existsByName(newName)) {
            throw new IllegalArgumentException("이미 존재하는 역할입니다.");
        }

        role.setName(newName);

        if (permissionIds != null) {
            role.getPermissions().clear();
            for (Long permissionId : permissionIds) {
                PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다: " + permissionId));
                role.addPermission(permission);
            }
        }

        return roleRepository.save(role);
    }

    // d
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 역할입니다.");
        }
        roleRepository.deleteById(id);
    }
}
