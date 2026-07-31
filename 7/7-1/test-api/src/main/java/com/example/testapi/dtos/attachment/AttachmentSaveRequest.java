package com.example.testapi.dtos.attachment;

public record AttachmentSaveRequest(Long postId, String name, String type, String fileUrl) {}
