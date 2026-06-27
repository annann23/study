package com.example.testapi.controller.request;

public record PostSaveRequest(String title, String content, Long boardId, Long userId) {}
