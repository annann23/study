package com.example.testapi.controller.request;

public record AttachmentSaveRequest(Long postId, String name, String type, String fileUrl) {}
