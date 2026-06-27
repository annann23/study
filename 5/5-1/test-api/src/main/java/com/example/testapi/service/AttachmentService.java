package com.example.testapi.service;

import com.example.testapi.domain.AttachmentEntity;
import com.example.testapi.domain.PostEntity;
import com.example.testapi.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PostService postService;

    public AttachmentService(AttachmentRepository attachmentRepository, PostService postService) {
        this.attachmentRepository = attachmentRepository;
        this.postService = postService;
    }

    public AttachmentEntity save(Long postId, String name, String type, String fileUrl) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type은 비어있을 수 없습니다.");
        }
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("fileUrl은 비어있을 수 없습니다.");
        }

        PostEntity post = postService.findById(postId);
        return attachmentRepository.save(new AttachmentEntity(post, name, type, fileUrl));
    }

    public AttachmentEntity findById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 첨부파일입니다."));
    }

    public List<AttachmentEntity> findAllByPost(Long postId) {
        postService.findById(postId);
        return attachmentRepository.findAllByPostId(postId);
    }

    public void delete(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 첨부파일입니다.");
        }
        attachmentRepository.deleteById(id);
    }
}
