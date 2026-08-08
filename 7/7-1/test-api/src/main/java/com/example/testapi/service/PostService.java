package com.example.testapi.service;

import com.example.testapi.dtos.post.PostContentDto;
import com.example.testapi.domain.BoardEntity;
import com.example.testapi.domain.PostEntity;
import com.example.testapi.domain.UserEntity;

import com.example.testapi.dtos.post.PostSearchType;
import com.example.testapi.repository.BoardRepository;
import com.example.testapi.repository.PostRepository;
import com.example.testapi.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                          BoardRepository boardRepository,
                          UserRepository userRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    //c
    public PostEntity save(PostContentDto post, Long boardId, Long userId) {
        if (post.title() == null || post.title().isBlank()) {
            throw new IllegalArgumentException("title은 비어있을 수 없습니다.");
        }

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));


        return postRepository.save(new PostEntity(board, user, post));
    }

    //r
    public PostEntity findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    public PostEntity findByIdWithAccessCheck(Long id, Long userId, boolean isAccessible) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다"));
        if (post.getBoard().isPrivate()
                && !isAccessible
                && !post.getUserId().equals(userId)) {
            throw new AccessDeniedException("비공개 게시판 글입니다.");
        }
        return post;
    }

    public Page<PostEntity> findAllByBoard(Long boardId, Pageable pageable) {
        return postRepository.findAllByBoardIdAndDeletedAtIsNull(boardId, pageable);
    }

    public List<PostEntity> findAllByUser(Long userId) {
        return postRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    public List<PostEntity> search(Long boardId, String keyword, PostSearchType type) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
        if (board.isPrivate()) {
            return List.of();
        }


        return switch (type) {
            case TITLE -> postRepository.searchByTitle(boardId, keyword);
            case CONTENT -> postRepository.searchByContent(boardId, keyword);
        };
    }


    //u
    public PostEntity edit(Long id, PostContentDto updatedPost ) {
        if (updatedPost.title() == null || updatedPost.title().isBlank()) {
            throw new IllegalArgumentException("제목은 비어있을 수 없습니다.");
        }

        PostEntity post = getActivePostOrThrow(id);
        post.setTitle(updatedPost.title());
        post.setContent(updatedPost.content());

        return postRepository.save(post);
    }

    public void delete(Long id) {
        PostEntity post = getActivePostOrThrow(id);
        post.setDeletedAt(ZonedDateTime.now());
        postRepository.save(post);
    }

    private PostEntity getActivePostOrThrow(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (post.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 게시글입니다.");
        }
        return post;
    }
}

