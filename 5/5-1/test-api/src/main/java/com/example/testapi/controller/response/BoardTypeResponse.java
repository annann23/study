package com.example.testapi.controller.response;

import com.example.testapi.domain.BoardTypeEntity;

import java.time.ZonedDateTime;

public record BoardTypeResponse(
        Long id,
        String name,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static BoardTypeResponse from(BoardTypeEntity entity) {
        return new BoardTypeResponse(
                entity.getId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
