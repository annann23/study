package com.example.testapi.controller;

import com.example.testapi.dtos.auth.UserLoginRequest;
import com.example.testapi.dtos.auth.UserRegisterRequest;
import com.example.testapi.dtos.user.UserResponse;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.security.jwt.JwtProvider;
import com.example.testapi.security.jwt.RefreshTokenService;
import com.example.testapi.service.AuthService;
import com.example.testapi.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public AuthController(AuthService authService, UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeMillis) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMillis))
                .build();
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
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse httpResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password()));

        CafeAuthUser user = (CafeAuthUser) auth.getPrincipal();
        Long userId = Objects.requireNonNull(user).getUserId();

        String accessToken = jwtProvider.generateAccessToken(userId, user.getUsername(), user.getAuthorities());
        String refreshToken = jwtProvider.generateRefreshToken(userId);
        refreshTokenService.save(userId, refreshToken, refreshTokenExpiration);

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildCookie("accessToken", accessToken, accessTokenExpiration).toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refreshToken", refreshToken, refreshTokenExpiration).toString());

        return ResponseEntity.ok(UserResponse.from(userService.findById(userId)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CafeAuthUser user, HttpServletResponse httpResponse) {
        refreshTokenService.delete(user.getUserId());

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildCookie("accessToken", "", 0).toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refreshToken", "", 0).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse httpResponse) {
        Claims claims = jwtProvider.parseClaims(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());

        if(!refreshTokenService.isValid(userId, refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        UserEntity userEntity = userService.findById(userId);

        List<GrantedAuthority> authorities = userEntity.getAuthorityNames().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        String newAccessToken = jwtProvider.generateAccessToken(userId, userEntity.getLoginId(), authorities);

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildCookie("accessToken", newAccessToken, accessTokenExpiration).toString());

        return ResponseEntity.ok().build();
    }
}
