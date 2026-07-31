package com.example.testapi.controller;

import com.example.testapi.dtos.auth.UserLoginRequest;
import com.example.testapi.dtos.auth.UserRegisterRequest;
import com.example.testapi.dtos.user.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.service.AuthService;
import com.example.testapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, UserService userService, SecurityContextRepository securityContextRepository, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal CafeAuthUser principal) {
        return ResponseEntity.ok(UserResponse.from(userService.findById(principal.getUserId())));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRegisterRequest request) {
        UserEntity user = authService.register(request.toLoginDto(), request.nickname());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        CafeAuthUser principal = (CafeAuthUser) auth.getPrincipal();
        return ResponseEntity.ok(UserResponse.from(userService.findById(Objects.requireNonNull(principal).getUserId())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
