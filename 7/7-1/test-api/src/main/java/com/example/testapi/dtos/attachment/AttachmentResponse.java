package com.example.testapi.dtos.attachment;

import com.example.testapi.domain.AttachmentEntity;

public record AttachmentResponse(
        Long id,
        Long postId,
        String name,
        String type,
        String fileUrl
) {
    public static AttachmentResponse from(AttachmentEntity entity) {
        return new AttachmentResponse(
                entity.getId(),
                entity.getPost().getId(),
                entity.getName(),
                entity.getType(),
                entity.getFileUrl()
        );
    }
}
