package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String nickname;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public CommentEntity() {}

    public CommentEntity(String content, String nickname) {
        this.content = content;
        this.nickname = nickname;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getNickname() { return nickname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}