package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "attachment")
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 100)
    private String fileUrl;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public AttachmentEntity() {}

    public AttachmentEntity(PostEntity post, String name, String type, String fileUrl) {
        this.post = post;
        this.name = name;
        this.type = type;
        this.fileUrl = fileUrl;
    }

    public Long getId() { return id; }
    public PostEntity getPost() { return post; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getFileUrl() { return fileUrl; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}
