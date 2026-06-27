package com.example.testapi.service;

import com.example.testapi.domain.CommentEntity;
import com.example.testapi.domain.PostEntity;
import com.example.testapi.domain.UserEntity;
import com.example.testapi.repository.CommentRepository;
import com.example.testapi.repository.PostRepository;
import com.example.testapi.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository ;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    //c
    public CommentEntity save(String content, Long postId, Long userId, Long parentId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 비어있을 수 없습니다.");
        }

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        CommentEntity parent = null;
        if (parentId != null ) {
           parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        }

        return commentRepository.save(new CommentEntity(content, post, user, parent));
    }

    //r
    public List<CommentEntity> findAllByPost(Long postId) {
        return commentRepository.findAllByPostIdAndDeletedAtIsNull(postId);
    }

    public List<CommentEntity> findAllByUser(Long userId) {
        return commentRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    //u
    public CommentEntity edit(Long id, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("content는 비어있을 수 없습니다.");
        }

        CommentEntity comment = getActiveCommentOrThrow(id);   // private 헬퍼로
        comment.setContent(newContent);
        comment.setIsEdited(true);

        return commentRepository.save(comment);
    }

    public void delete(Long id) {
        CommentEntity comment = getActiveCommentOrThrow(id);
        comment.setDeletedAt(ZonedDateTime.now());
        commentRepository.save(comment);
    }

    private CommentEntity getActiveCommentOrThrow(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (comment.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 댓글입니다.");
        }
        return comment;
    }
}

