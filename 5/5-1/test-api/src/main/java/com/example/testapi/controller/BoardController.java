package com.example.testapi.controller;

import com.example.testapi.controller.request.BoardEditNameRequest;
import com.example.testapi.controller.request.BoardSaveRequest;
import com.example.testapi.controller.response.BoardResponse;
import com.example.testapi.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@CrossOrigin(origins = "*")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardSaveRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.create(request.name(), request.boardTypeId())));
    }

    @PutMapping("/name")
    public ResponseEntity<BoardResponse> updateName(@RequestBody BoardEditNameRequest request) {
        return ResponseEntity.ok(BoardResponse.from(boardService.updateName(request.boardId(), request.name())));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {
        List<BoardResponse> responses = boardService.findAll()
                .stream()
                .map(BoardResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardResponse.from(boardService.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }
}
