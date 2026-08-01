package com.example.testapi.dtos.board;
import com.example.testapi.domain.BoardEntity;

public record BoardResponse(
        Long id,
        Long boardTypeId,
        String name,
        boolean isPrivate
) {
    public static BoardResponse from(BoardEntity entity) {
        return new BoardResponse(
                entity.getId(),
                entity.getBoardTypeId(),
                entity.getName(),
                entity.isPrivate()
        );
    }
}
