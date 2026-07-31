package com.example.testapi.domain;

import com.example.testapi.dtos.post.PostContentDto;
import com.example.testapi.security.OwnableResource;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="post")
public class PostEntity implements OwnableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length=20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    public PostEntity() {};

    public PostEntity(BoardEntity board, UserEntity user, PostContentDto post) {
        this.board = board;
        this.user =  user;
        this.title = post.title();
        this.content = post.content();
    };

    public Long getId() { return id; }
    public BoardEntity getBoard() { return board; }
    public Long getBoardId() { return board.getId(); }
    public UserEntity getUser() { return user; }
    public Long getUserId() { return user.getId(); }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public ZonedDateTime getDeletedAt() { return deletedAt; }

    @Override
    public Long getOwnerId() { return user.getId(); }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }

}
