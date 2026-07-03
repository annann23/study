package com.example.testapi.controller.request;

public record UserRegisterRequest(String loginId, String password, String nickname) {
    public UserLoginRequest toLoginDto() {
        return new UserLoginRequest(loginId, password);
    }
}