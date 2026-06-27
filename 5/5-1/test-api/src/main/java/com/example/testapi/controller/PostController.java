package com.example.testapi.controller;

import com.example.testapi.controller.request.PostDeleteRequest;
import com.example.testapi.controller.request.PostEditRequest;
import com.example.testapi.controller.request.PostSaveRequest;
import com.example.testapi.controller.response.PostResponse;
import com.example.testapi.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> save(@RequestBody PostSaveRequest request) {
        PostResponse response = PostResponse.from(
                postService.save(request.title(), request.content(), request.boardId(), request.userId())
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(PostResponse.from(postService.findById(id)));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<PostResponse>> findAllByBoard(@PathVariable Long boardId) {
        List<PostResponse> responses = postService.findAllByBoard(boardId)
                .stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> findAllByUser(@PathVariable Long userId) {
        List<PostResponse> responses = postService.findAllByUser(userId)
                .stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping
    public ResponseEntity<PostResponse> edit(@RequestBody PostEditRequest request) {
        PostResponse response = PostResponse.from(
                postService.edit(request.postId(), request.title(), request.content())
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody PostDeleteRequest request) {
        postService.delete(request.postId());
        return ResponseEntity.ok().build();
    }
}
