package com.example.testapi.controller;

import com.example.testapi.dtos.request.AttachmentSaveRequest;
import com.example.testapi.dtos.response.AttachmentResponse;
import com.example.testapi.service.AttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PreAuthorize("hasPermission(null, 'ATTACHMENT', 'ATTACHMENT_CREATE')")
    @PostMapping
    public ResponseEntity<AttachmentResponse> save(@RequestBody AttachmentSaveRequest request) {
        return ResponseEntity.ok(AttachmentResponse.from(
                attachmentService.save(request.postId(), request.name(), request.type(), request.fileUrl())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(AttachmentResponse.from(attachmentService.findById(id)));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<AttachmentResponse>> findAllByPost(@PathVariable Long postId) {
        List<AttachmentResponse> responses = attachmentService.findAllByPost(postId)
                .stream()
                .map(AttachmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasPermission(#id, 'ATTACHMENT', 'ATTACHMENT_DELETE_ANY') or hasPermission(#id, 'ATTACHMENT', 'ATTACHMENT_DELETE_OWN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attachmentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
