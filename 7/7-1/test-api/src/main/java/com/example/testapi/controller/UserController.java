package com.example.testapi.controller;

import com.example.testapi.dtos.user.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.dtos.user.UserDataEditRequest;
import com.example.testapi.dtos.user.UserDeleteRequest;
import com.example.testapi.dtos.user.UserLevelEditRequest;
import com.example.testapi.dtos.user.UserPasswordEditRequest;
import com.example.testapi.dtos.user.UserRoleAssignRequest;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasPermission(principal.userId, 'USER', 'USER_UPDATE_OWN')")
    @PutMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(@RequestBody UserPasswordEditRequest request, @AuthenticationPrincipal CafeAuthUser principal) {
        UserEntity user = userService.updatePassword(principal.getUserId(), request.password());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PreAuthorize("hasPermission(principal.userId, 'USER', 'USER_UPDATE_OWN')")
    @PutMapping("/user")
    public ResponseEntity<UserResponse> updateUserData(@RequestBody UserDataEditRequest request, @AuthenticationPrincipal CafeAuthUser principal) {
        UserEntity user = userService.updateNickname(principal.getUserId(), request.nickname());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PreAuthorize("hasPermission(null, 'USER', 'USER_LEVEL_ASSIGN')")
    @PutMapping("/user-level")
    public ResponseEntity<UserResponse> updateLevel(@RequestBody UserLevelEditRequest request) {
        UserEntity user = userService.updateLevel(request.userId(), request.userLevelId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PreAuthorize("hasPermission(null, 'USER', 'ROLE_ASSIGN')")
    @PutMapping("/role")
    public ResponseEntity<UserResponse> assignRole(@RequestBody UserRoleAssignRequest request) {
        UserEntity user = userService.assignRole(request.userId(), request.roleId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> responses = userService.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findByLoginId(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userService.findById(id)));
    }

    @PreAuthorize("hasPermission(#request.userId(), 'USER', 'USER_DELETE_ANY') or hasPermission(#request.userId(), 'USER', 'USER_DELETE_OWN')")
    @DeleteMapping
    public void delete(@RequestBody UserDeleteRequest request) {
        userService.delete(request.userId());
    }
}
