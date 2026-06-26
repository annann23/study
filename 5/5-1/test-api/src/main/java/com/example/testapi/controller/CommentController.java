//
package com.example.testapi.controller;

import com.example.testapi.controller.request.CommentRequest;
import com.example.testapi.controller.response.CommentResponse;
import com.example.testapi.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CommentRequest request) {
        commentService.save(request.content(), request.nickname());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> findAll() {
        List<CommentResponse> responses = commentService.findAll()
                .stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
