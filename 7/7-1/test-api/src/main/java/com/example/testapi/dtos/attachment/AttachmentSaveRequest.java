package com.example.testapi.dtos.request;

public record AttachmentSaveRequest(Long postId, String name, String type, String fileUrl) {}
