package com.example.testapi.controller.request;

public record PostSaveRequest(PostContentDto post, Long boardId, Long userId) {}
