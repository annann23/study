package com.example.testapi.dtos.board;
import com.example.testapi.domain.BoardEntity;

public record BoardResponse(
        Long id,
        Long boardTypeId,
        String name,
        boolean isPrivate,
        BoardEntity.BoardStatus status,
        Long operatorId,
        Long requestedById,
        String reason
) {
    public static BoardResponse from(BoardEntity entity) {
        return new BoardResponse(
                entity.getId(),
                entity.getBoardTypeId(),
                entity.getName(),
                entity.isPrivate(),
                entity.getStatus(),
                entity.getOperatorId(),
                entity.getRequestedBy() == null ? null : entity.getRequestedBy().getId(),
                entity.getReason()
        );
    }
}
