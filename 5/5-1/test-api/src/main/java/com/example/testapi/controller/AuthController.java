package com.example.testapi.controller;

import com.example.testapi.dtos.request.UserLoginRequest;
import com.example.testapi.dtos.request.UserRegisterRequest;
import com.example.testapi.dtos.response.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.service.AuthService;
import com.example.testapi.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return ResponseEntity.ok(UserResponse.from(userService.findById(userId)));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRegisterRequest request) {
        UserEntity user = authService.register(request.toLoginDto(), request.nickname());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request) {
        UserEntity user = authService.login(request);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
