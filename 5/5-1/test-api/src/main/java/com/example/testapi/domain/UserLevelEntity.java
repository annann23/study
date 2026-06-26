package com.example.testapi.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name="user_level")
public class UserLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public UserLevelEntity() {}

    public UserLevelEntity(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
}
