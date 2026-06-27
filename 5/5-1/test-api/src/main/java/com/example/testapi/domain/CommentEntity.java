package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private CommentEntity parent;

    @Column(nullable = false, length = 100)
    private String content;
    private Boolean isEdited;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    public CommentEntity() {}

    public CommentEntity(String content, PostEntity post, UserEntity user, CommentEntity parent) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.parent = parent;
    }

    public Long getId() { return id; }
    public PostEntity getPost() { return post; }
    public UserEntity getUser() { return user; }
    public String getUserId() {return user.getLoginId();}
    public CommentEntity getParent() { return parent; }

    public String getContent() { return content; }
    public Boolean getIsEdited() { return isEdited; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public ZonedDateTime getDeletedAt() { return deletedAt; }

    public void setContent(String content) { this.content = content; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }
}