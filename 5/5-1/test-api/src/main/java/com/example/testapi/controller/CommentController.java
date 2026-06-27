//
package com.example.testapi.controller;

import com.example.testapi.controller.request.CommentDeleteRequest;
import com.example.testapi.controller.request.CommentEditRequest;
import com.example.testapi.controller.request.CommentSaveRequest;
import com.example.testapi.controller.response.CommentResponse;
import com.example.testapi.domain.CommentEntity;
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
    public ResponseEntity<CommentResponse> save(@RequestBody CommentSaveRequest request) {
        CommentEntity comment = commentService.save(request.content(), request.postId(), request.userId(), request.parentId());
        return ResponseEntity.ok(CommentResponse.from(comment));
    }

    @PutMapping
    public ResponseEntity<CommentResponse> edit(@RequestBody CommentEditRequest request) {
        CommentEntity comment = commentService.edit(request.commentId(), request.content());
        return ResponseEntity.ok(CommentResponse.from(comment));
    }

    @GetMapping("/post")
    public ResponseEntity<List<CommentResponse>> findAllByPost(@RequestParam Long postId) {
        List<CommentResponse> responses = commentService.findAllByPost(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CommentResponse>> findAllByUser(@RequestParam Long userId) {
        List<CommentResponse> responses = commentService.findAllByUser(userId)
                .stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping
    public void delete(@RequestBody CommentDeleteRequest request) {
        commentService.delete(request.commentId());
    }
}
