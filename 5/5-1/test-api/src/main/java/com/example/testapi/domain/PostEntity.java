package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private UserEntity user;

    private String title;
    private String content;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public PostEntity() {};

    public PostEntity(BoardEntity board, UserEntity user, String title, String content) {
        this.board = board;
        this.user =  user;
        this.title = title;
        this.content = content;
    };

    public Long getId() { return id; }
    public BoardEntity getBoard() { return board; }
    public UserEntity getUser() { return user; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}
