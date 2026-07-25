package com.example.testapi.controller;

import com.example.testapi.dtos.request.RoleSaveRequest;
import com.example.testapi.dtos.request.RoleUpdateRequest;
import com.example.testapi.dtos.response.RoleResponse;
import com.example.testapi.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleSaveRequest request) {
        return ResponseEntity.ok(RoleResponse.from(roleService.create(request.name(), request.permissionIds())));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> findAll() {
        List<RoleResponse> responses = roleService.findAll()
                .stream()
                .map(RoleResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(RoleResponse.from(roleService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(RoleResponse.from(roleService.update(id, request.name(), request.permissionIds())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
