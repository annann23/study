package com.example.testapi.controller.request;

public record UserRegisterRequest(String loginId, String password, Long userLevelId) {}