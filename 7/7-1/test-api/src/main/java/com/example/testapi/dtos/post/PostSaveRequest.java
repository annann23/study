package com.example.testapi.dtos.request;

public record PostSaveRequest(PostContentDto post, Long boardId, Long userId) {}
