package com.example.testapi.dtos.request;

public record CommentSaveRequest(String content, Long postId, Long userId, Long parentId) {}