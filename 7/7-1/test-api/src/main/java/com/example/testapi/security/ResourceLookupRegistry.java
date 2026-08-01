package com.example.testapi.security;

import com.example.testapi.repository.*;
import com.example.testapi.security.OwnableResource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class ResourceLookupRegistry {

    private final Map<String, Function<Long, OwnableResource>> lookups;

    public ResourceLookupRegistry(PostRepository postRepository,
                                  CommentRepository commentRepository,
                                  AttachmentRepository attachmentRepository,
                                  UserRepository userRepository,
                                  LikedRepository likedRepository) {
        this.lookups = Map.of(
                "POST", id -> (OwnableResource) postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다.")),
                "COMMENT", id -> (OwnableResource) commentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다.")),
                "ATTACHMENT", id -> (OwnableResource) attachmentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 첨부파일입니다.")),
                "USER", id -> (OwnableResource) userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")),
                "LIKE", id -> (OwnableResource) likedRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좋아요입니다."))
        );
    }

    public OwnableResource find(String targetType, Long id) {
        Function<Long, OwnableResource> lookup = lookups.get(targetType);
        if (lookup == null) {
            throw new IllegalArgumentException("등록되지 않은 리소스 타입입니다: " + targetType);
        }
        return lookup.apply(id);
    }
}