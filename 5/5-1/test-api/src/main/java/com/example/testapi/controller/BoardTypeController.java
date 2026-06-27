package com.example.testapi.controller;

import com.example.testapi.controller.request.BoardTypeEditRequest;
import com.example.testapi.controller.request.BoardTypeSaveRequest;
import com.example.testapi.controller.response.BoardTypeResponse;
import com.example.testapi.service.BoardTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board-types")
@CrossOrigin(origins = "*")
public class BoardTypeController {

    private final BoardTypeService boardTypeService;

    public BoardTypeController(BoardTypeService boardTypeService) {
        this.boardTypeService = boardTypeService;
    }

    @PostMapping
    public ResponseEntity<BoardTypeResponse> create(@RequestBody BoardTypeSaveRequest request) {
        return ResponseEntity.ok(BoardTypeResponse.from(boardTypeService.create(request.name())));
    }

    @GetMapping
    public ResponseEntity<List<BoardTypeResponse>> findAll() {
        List<BoardTypeResponse> responses = boardTypeService.findAll()
                .stream()
                .map(BoardTypeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(BoardTypeResponse.from(boardTypeService.findById(id)));
    }

    @PutMapping
    public ResponseEntity<BoardTypeResponse> update(@RequestBody BoardTypeEditRequest request) {
        return ResponseEntity.ok(BoardTypeResponse.from(boardTypeService.update(request.boardTypeId(), request.name())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardTypeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
