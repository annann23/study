package com.example.testapi.dtos.request;

public record UserRegisterRequest(String loginId, String password, String nickname) {
    public UserLoginRequest toLoginDto() {
        return new UserLoginRequest(loginId, password);
    }
}