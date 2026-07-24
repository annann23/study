package com.example.testapi.controller;

import com.example.testapi.dtos.request.UserLoginRequest;
import com.example.testapi.dtos.request.UserRegisterRequest;
import com.example.testapi.dtos.response.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.service.AuthService;
import com.example.testapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(AuthService authService, UserService userService, SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
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
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        UserEntity user = authService.login(request);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getId(), null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
