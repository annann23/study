package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.ZonedDateTime;

@Entity
@Table(name = "liked", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
public class LikedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public LikedEntity() {};

    public LikedEntity(UserEntity user, PostEntity post) {
        this.user = user;
        this.post = post;
    };

    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public PostEntity getPost() { return post; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}
