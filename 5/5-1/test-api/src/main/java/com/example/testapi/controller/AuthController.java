package com.example.testapi.controller;

import com.example.testapi.controller.request.UserLoginDto;
import com.example.testapi.controller.request.UserRegisterRequest;
import com.example.testapi.controller.response.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.service.AuthService;
import com.example.testapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponse> me(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null) return ResponseEntity.status(401).build();

        Long userId = (Long) session.getAttribute("id");
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(UserResponse.from(userService.findById(userId)));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRegisterRequest request) {
        UserEntity user = authService.register(request.loginDto());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginDto request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if(session != null && Objects.requireNonNull(session).getAttribute("id") != null) {
            Long userId = (Long) session.getAttribute("id");
            return ResponseEntity.ok(UserResponse.from(userService.findById(userId)));
        }

        UserEntity user = authService.login(request);
        HttpSession newSession = httpRequest.getSession(true);
        newSession.setAttribute("id", user.getId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
