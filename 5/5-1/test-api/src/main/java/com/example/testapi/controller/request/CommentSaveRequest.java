package com.example.testapi.controller.request;

public record CommentSaveRequest(String content, Long postId, Long userId, Long parentId) {}