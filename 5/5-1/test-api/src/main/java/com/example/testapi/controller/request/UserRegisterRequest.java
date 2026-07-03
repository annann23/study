package com.example.testapi.controller.request;

public record UserRegisterRequest(UserLoginDto loginDto, String nickname) {}