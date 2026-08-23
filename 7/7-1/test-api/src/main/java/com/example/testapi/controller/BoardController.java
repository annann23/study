package com.example.testapi.controller;

import com.example.testapi.dtos.board.BoardEditNameRequest;
import com.example.testapi.dtos.board.BoardRequestDto;
import com.example.testapi.dtos.board.BoardSaveRequest;
import com.example.testapi.dtos.board.BoardResponse;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_CREATE')")
    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardSaveRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.create(request.name(), request.boardTypeId(), request.isPrivate())));
    }

    @PostMapping("/request")
    public ResponseEntity<BoardResponse> request(@RequestBody BoardRequestDto request, @AuthenticationPrincipal CafeAuthUser principal) {
        return ResponseEntity.ok(BoardResponse.from(boardService.request(request.name(), request.boardTypeId(), principal.getUserId(), request.reason())));
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_UPDATE')")
    @PutMapping("/name")
    public ResponseEntity<BoardResponse> updateName(@RequestBody BoardEditNameRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.updateName(request.boardId(), request.name())));
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_UPDATE')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<BoardResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.approve(id)));
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_UPDATE')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<BoardResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.reject(id)));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {
        List<BoardResponse> responses = boardService.findAll()
                .stream()
                .map(BoardResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_UPDATE')")
    @GetMapping("/pending")
    public ResponseEntity<List<BoardResponse>> findAllPending() {
        List<BoardResponse> responses = boardService.findAllPending()
                .stream()
                .map(BoardResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.findById(id)));
    }

    @PreAuthorize("hasPermission(null, 'BOARD', 'BOARD_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }
}
