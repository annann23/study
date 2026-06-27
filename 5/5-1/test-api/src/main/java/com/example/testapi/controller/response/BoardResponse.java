package com.example.testapi.controller.response;
import com.example.testapi.domain.BoardEntity;

public record BoardResponse(
        Long id,
        Long boardTypeId,
        String name
) {
    public static BoardResponse from(BoardEntity entity) {
        return new BoardResponse(
                entity.getId(),
                entity.getBoardTypeId(),
                entity.getName()
        );
    }
}
