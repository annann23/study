package com.example.testapi.service;

import com.example.testapi.domain.PermissionEntity;
import com.example.testapi.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    // c
    public PermissionEntity create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 권한입니다.");
        }

        return permissionRepository.save(new PermissionEntity(name));
    }

    // r
    public PermissionEntity findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
    }

    public List<PermissionEntity> findAll() {
        return permissionRepository.findAll();
    }

    // u
    public PermissionEntity update(Long id, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (permissionRepository.existsByName(newName)) {
            throw new IllegalArgumentException("이미 존재하는 권한입니다.");
        }

        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));

        permission.setName(newName);
        return permissionRepository.save(permission);
    }

    // d
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 권한입니다.");
        }
        permissionRepository.deleteById(id);
    }
}
