package com.example.testapi.dtos.comment;

public record CommentSaveRequest(String content, Long postId, Long parentId) {}