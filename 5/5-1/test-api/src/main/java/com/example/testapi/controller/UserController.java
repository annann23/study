package com.example.testapi.controller;

import com.example.testapi.controller.request.*;
import com.example.testapi.controller.response.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestBody UserRegisterRequest request) {
        UserEntity user = userService.register(request.loginId(), request.password(), request.userLevelId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(@RequestBody UserPasswordEditRequest request) {
        UserEntity user = userService.updatePassword(request.userId(), request.password());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/user-level")
    public ResponseEntity<UserResponse> updateLevel(@RequestBody UserLevelEditRequest request) {
        UserEntity user = userService.updateLevel(request.userId(), request.userLevelId());
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

    @DeleteMapping
    public void delete(@RequestBody UserDeleteRequest request) {
        userService.delete(request.userId());
    }
}
