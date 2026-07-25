package com.example.testapi.controller;

import com.example.testapi.dtos.request.PermissionSaveRequest;
import com.example.testapi.dtos.request.PermissionUpdateRequest;
import com.example.testapi.dtos.response.PermissionResponse;
import com.example.testapi.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionSaveRequest request) {
        return ResponseEntity.ok(PermissionResponse.from(permissionService.create(request.name())));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> findAll() {
        List<PermissionResponse> responses = permissionService.findAll()
                .stream()
                .map(PermissionResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(PermissionResponse.from(permissionService.findById(id)));
    }

    @PutMapping
    public ResponseEntity<PermissionResponse> update(@RequestBody PermissionUpdateRequest request) {
        return ResponseEntity.ok(PermissionResponse.from(permissionService.update(request.permissionId(), request.name())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
