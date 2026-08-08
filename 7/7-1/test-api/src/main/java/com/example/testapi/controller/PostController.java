package com.example.testapi.controller;

import com.example.testapi.dtos.post.*;
import com.example.testapi.dtos.like.LikeStatusResponse;
import com.example.testapi.security.CafeAuthUser;
import com.example.testapi.service.LikedService;
import com.example.testapi.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final LikedService likedService;

    public PostController(PostService postService, LikedService likedService) {
        this.postService = postService;
        this.likedService = likedService;
    }

    @PreAuthorize("hasPermission(null, 'POST', 'POST_CREATE')")
    @PostMapping
    public ResponseEntity<PostResponse> save(@RequestBody PostSaveRequest request, @AuthenticationPrincipal CafeAuthUser principal) {
        PostResponse response = PostResponse.from(
                postService.save(request.post(), request.boardId(), principal.getUserId())
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id, @AuthenticationPrincipal CafeAuthUser principal) {
        return ResponseEntity.ok(PostResponse.from(postService.findByIdWithAccessCheck(id, principal.getUserId(), principal.hasAuthority("PRIVATE_BOARD_ACCESS"))));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<Page<PostResponse>> findAllByBoard(
            @PathVariable Long boardId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> responses = postService.findAllByBoard(boardId, pageable)
                .map(post -> post.getBoard().isPrivate() ? PostResponse.preview(post) : PostResponse.from(post));
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

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> search(@RequestParam Long boardId, @RequestParam String keyword, @RequestParam PostSearchType type) {
        List<PostResponse> responses = postService.search(boardId, keyword, type)
                .stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }


    @PreAuthorize("hasPermission(#request.postId(), 'POST', 'POST_UPDATE_ANY') or hasPermission(#request.postId(), 'POST', 'POST_UPDATE_OWN')")
    @PutMapping
    public ResponseEntity<PostResponse> edit(@RequestBody PostEditRequest request) {
        PostResponse response = PostResponse.from(
                postService.edit(request.postId(), request.post())
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasPermission(#request.postId(), 'POST', 'POST_DELETE_ANY') or hasPermission(#request.postId(), 'POST', 'POST_DELETE_OWN')")
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody PostDeleteRequest request) {
        postService.delete(request.postId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasPermission(null, 'LIKE', 'LIKE_CREATE') or hasPermission(null, 'LIKE', 'LIKE_DELETE')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long postId, @AuthenticationPrincipal CafeAuthUser principal) {
        return ResponseEntity.ok(likedService.toggleLike(principal.getUserId(), postId));
    }

    @GetMapping("/{postId}/like")
    public ResponseEntity<LikeStatusResponse> getLikeStatus(@PathVariable Long postId, @AuthenticationPrincipal CafeAuthUser principal) {
        return ResponseEntity.ok(new LikeStatusResponse(likedService.countByPost(postId), likedService.isLikedBy(principal.getUserId(), postId)));
    }
}
